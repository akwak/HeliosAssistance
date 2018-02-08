package edu.akwak.heliosassistance;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.akwak.heliosassistance.logic.MyRequestQueue;
import edu.akwak.heliosassistance.logic.VoteCodeGenerator;
import edu.akwak.heliosassistance.utils.TextviewUpdater;

public class VoteActivity  extends AppCompatActivity {

    private TextView question;
    private List<TextView> codeTextViewList = new ArrayList<>();
    private List<TextView> colorsTextViewList = new ArrayList<>();
    private String baseUrl = Settings.HELIOS_URL + "assistance/elections/";
    private GridLayout gridLayout;
    private VoteCodeGenerator voteCodeGenerator = new VoteCodeGenerator();
    private String sessionId = "";
    private String electionId = "";
    private String[] voteCodes;
    private String keyByteString = "";
    private PublicKey pubKey = null;
    private Button inviButton;
    private DisplayMetrics metrics;


    private int colorLeftMargin;
    private int colorRightMargin;
    private int codeLeftMargin;
    private int codeRightMargin;
    private int topMargin;
    private int bottomMargin;
    private boolean assistant;
    private Button encryptButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        sessionId = b.getString("sessionId");
        electionId = b.getString("electionId");
        keyByteString = b.getString("b64pubkey");
        assistant = b.getBoolean("assistant");

        try {
            pubKey = Settings.getPublicKey(keyByteString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }


        setContentView(R.layout.vote_activity);


        inviButton =(Button) findViewById(R.id.send_button);
        encryptButton = (Button) findViewById(R.id.encrypt_button);


        question = (TextView) findViewById(R.id.question);
        question.setText(sessionId);
        question.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        gridLayout = (GridLayout) findViewById(R.id.grid_layout);


        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        codeLeftMargin = metrics.widthPixels / 5;
        colorRightMargin = metrics.widthPixels / 5;
        colorLeftMargin = metrics.widthPixels / 20;
        codeRightMargin = metrics.widthPixels / 20;
        topMargin = 15;
        bottomMargin = 15;

        adjustViewToRole(assistant);
        if(assistant) {
            doSth();
        }
    }

    private void adjustViewToRole(boolean assistant) {
        if(!assistant) {
            inviButton.setEnabled(true);
            inviButton.setVisibility(View.VISIBLE);
            Button encButton = (Button) findViewById(R.id.encrypt_button);
            encButton.setVisibility(View.INVISIBLE);
            encButton.setEnabled(false);
            gridLayout.setVisibility(View.INVISIBLE);
        }
    }


    public void doSth() {
        String url = baseUrl + electionId + "/get_answer_tokens";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            JSONArray colors = obj.getJSONArray("colors");
                            for(int i = 0; i < colors.length(); i++) {
                                TextView colorTextView = new TextView(getApplicationContext());
                                String colorsString = colors.getString(i);
                                colorTextView.setText(colorsString);
                                int colorInt = Color.parseColor(colorsString);
                                colorTextView.setTextSize(20f);
                                colorTextView.setBackgroundColor(colorInt);
                                colorTextView.setTextColor(Color.WHITE);
                                colorTextView.setGravity(Gravity.CENTER);
                                colorsTextViewList.add(colorTextView);
                            }

                            voteCodes = voteCodeGenerator.generateCodes(colors.length());

                            //JSONArray codes = obj.getJSONArray("codes");
                            for(int i = 0; i < voteCodes.length; i++) {
                                TextView codeTextView = new TextView(getApplicationContext());
                                String codeString = voteCodes[i];
                                codeTextView.setText(codeString);
                                codeTextView.setTextColor(Color.BLACK);
                                codeTextView.setTextSize(20f);
                                codeTextViewList.add(codeTextView);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(codeTextViewList.size() != colorsTextViewList.size()) {
                            Toast.makeText(getApplicationContext(), "Problems with codes, aborting...", Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (int i = 0; i < codeTextViewList.size(); i++) {
                            addTextViewToGrid(codeTextViewList.get(i),codeLeftMargin,topMargin,codeRightMargin,bottomMargin);
                            addTextViewToGrid(colorsTextViewList.get(i),colorLeftMargin, topMargin, colorRightMargin, bottomMargin);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void addTextViewToGrid(TextView textView , int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        gridLayout.addView(textView);
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) textView.getLayoutParams();
        params.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
        params.width = (gridLayout.getWidth()/gridLayout.getColumnCount()) -params.rightMargin - params.leftMargin;

        textView.setLayoutParams(params);
    }


    public void encryptVoteCodes(final View view) {
        encryptVoteCodesAndShowBtn(view, true);

    }

    private void encryptVoteCodesAndShowBtn(final View view, final boolean show) {
        String url = Settings.HELIOS_URL + "assistance/" + "elections/" + electionId + "/post_vote_codes";
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    //    Button button = (Button) view.findViewById(R.id.encrypt_button);
                        encryptButton.setEnabled(false);
                        if(show) {
                            showCheckButton(view);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if(error !=null) {
                          Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                           Log.e("ENC_CODE", error.toString());
                    //       Log.e("ENC_CODE", error.getMessage());
                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("session_id", sessionId);
                String voteCodeString = getVoteCodeToString();
                params.put("vote_codes", voteCodeString);
                return params;
            }


        };
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(strRequest);
    }

    private void showCheckButton(View view) {
        inviButton.setEnabled(true);
        inviButton.setVisibility(view.VISIBLE);
        inviButton.setEnabled(true);
    }

    @NonNull
    private String getVoteCodeToString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < voteCodes.length; i++) {
            sb.append(voteCodes[i]);
            sb.append(';');

        }
        sb.setLength(sb.length()-1);
        return sb.toString();
    }

    public void checkVoteCode(View view) {
        String url = Settings.HELIOS_URL + "assistance/" + "elections/" + electionId + "/check_vote_code";
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        TextviewUpdater textViewThread = new TextviewUpdater(response, (TextView) findViewById(R.id.vote_code));
                        textViewThread.run();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if(error !=null) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("ENC_CODE", error.toString());
                            //       Log.e("ENC_CODE", error.getMessage());
                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("session_id", sessionId);
                return params;
            }


        };
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(strRequest);

    }

    public void auditVoteCodes(View view) {
        encryptVoteCodesAndShowBtn(view, false);

    }
}

