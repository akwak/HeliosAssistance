package edu.akwak.heliosassistance;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import edu.akwak.heliosassistance.logic.MyRequestQueue;
import edu.akwak.heliosassistance.utils.TextviewUpdater;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MenuActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
        private ZXingScannerView mScannerView;
        private PublicKey pubKey = null;
        boolean signatureVerification = false;
        private TextView tv;
        private String sessionId = "";
        private String electionId = "";
        private String b64KeyBytesString = "";
        private boolean assistant = true;
        private Button votingButton;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle b = getIntent().getExtras();
            assistant = b.getBoolean("assistant");

            setContentView(R.layout.activity_main);

            tv = (TextView) findViewById(R.id.session_id);
            votingButton = (Button) findViewById(R.id.goToVotingBtn);

        }

    public void scanBarcode(View view) {
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    public void fetchSessionId(View view) {
        if(!signatureVerification) {
            Toast.makeText(this.getApplicationContext(), "You need to scan proper QR first", Toast.LENGTH_LONG).show();
            return;
        }
        String url = Settings.HELIOS_URL + "assistance/get_session_id";



        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] split = response.split(";");


                        sessionId = split[0];
                        electionId = split[1];
                        TextviewUpdater textViewThread = new TextviewUpdater(sessionId, (TextView) findViewById(R.id.session_id));
                        textViewThread.run();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "Session id acquired", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Settings.SESSION_ID, sessionId);
                return params;
            };
        };
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);


    }
    public void goToQuestions(View view) {
        if(electionId==null || electionId.equals("")) {
            Toast.makeText(this.getApplicationContext(), "You need to fetch session id first", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, VoteActivity.class);
        Bundle b = new Bundle();
        b.putString("electionId",electionId);
        b.putString("sessionId", sessionId);
        b.putString("b64pubkey", b64KeyBytesString);
        b.putBoolean("assistant", assistant);
        intent.putExtras(b);
        startActivity(intent);

    }

    @Override
    public void handleResult(Result result) {
        String message = parseResult(result.getText());
        JSONObject jsonData = parseToJSON(message);
        if(!testJsonFields(jsonData)) {
            return;
        }
        try {
            acquireKeyAndVerify(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(pubKey!=null) {
            Log.d("KEY READY", "key ready");
        }
//        try {
//            PublicKey pubKey = getKey();
//        } catch (Exception e) {
//            Log.e("KEY", e.getMessage());
//        }

        Log.i("decodeMessage", message);
        Log.v("handleResult", result.getText());
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Scan result");
        builder.setMessage(result.getText());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean testJsonFields(JSONObject jsonData) {
        boolean isCorrect = false;
        try {
            isCorrect = jsonData.has("key_rand") && jsonData.get("key_rand") != null;
            isCorrect = jsonData.has("session_id") && jsonData.get("session_id") != null;
            isCorrect = jsonData.has("sign") && jsonData.get("sign") != null;
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
        }
        return isCorrect;
    }


    private String parseResult(String text) {
        try {
            byte[] decodeBytes = text.getBytes("UTF-8");
            byte[] bytes = Base64.decode(decodeBytes,Base64.DEFAULT);
            String parsedMsg = new String(bytes, "UTF-8");
            return parsedMsg;

        } catch (UnsupportedEncodingException e) {
            Log.e("DECODE", e.getMessage());
        }
        return "nothing";
    }



    private JSONObject parseToJSON(String jsonString) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
        }
        return obj;
    }

    public void acquireKeyAndVerify(final JSONObject obj)
            throws Exception {

        String url = Settings.HELIOS_URL + "assistance/get_public_key";



        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            signatureVerification = verifySignature(response, obj);

                            Log.i("VERIFY", "sign status: " + signatureVerification);

                            if(signatureVerification) {
                                Toast.makeText(getApplicationContext(), "Verification success", Toast.LENGTH_LONG).show();

                            }
                            setContentView(R.layout.activity_main);

                            TextviewUpdater textViewThread = new TextviewUpdater(obj.getString(Settings.SESSION_ID), (TextView) findViewById(R.id.session_id));
                            textViewThread.run();
                            Thread.sleep(500);

                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (SignatureException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error!=null) {
                            Log.e("PUBKEY_GET", error.getMessage());
                            Log.e("PUBKEY_GET", error.getLocalizedMessage());
                        }
                    }
                });
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private boolean verifySignature(String response, JSONObject obj) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, JSONException {
        b64KeyBytesString = response;
        pubKey = Settings.getPublicKey(response);

        sessionId = obj.getString(Settings.SESSION_ID);

        Signature rsaSha256Signature = Signature.getInstance("SHA256withRSA");
        rsaSha256Signature.initVerify(pubKey);
        rsaSha256Signature.update(obj.getString(Settings.SESSION_ID).getBytes("UTF-8"));
        rsaSha256Signature.update(obj.getString(Settings.KEY_RAND).getBytes("UTF-8"));
        byte[] bytes = Base64.decode(obj.getString(Settings.SIGN).getBytes("UTF-8"),Base64.DEFAULT);
        return rsaSha256Signature.verify(bytes);
    }

}

