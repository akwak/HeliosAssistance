package edu.akwak.heliosassistance;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AuditActivity extends AppCompatActivity {


    private String electionId;
    private String sessionId;
    private String keyByteString;
    private GridLayout gridLayout;
    private String baseUrl = Settings.HELIOS_URL + "assistance/elections/";
    private List<TextView> codeTextViewList = new ArrayList<>();
    private List<TextView> colorsTextViewList = new ArrayList<>();
    //private List<TextView> candidatesTextViewList=

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        sessionId = b.getString("sessionId");
        electionId = b.getString("electionId");
        keyByteString = b.getString("b64pubkey");

        setContentView(R.layout.audit_layout);

        gridLayout = (GridLayout) findViewById(R.id.grid_layout);
    }



//    public void fetchAuditData(View view) {
//            String url = baseUrl + electionId + "/get_audit_tokens";
//            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            JSONObject obj = null;
//                            try {
//                                obj = new JSONObject(response);
//                                JSONArray colors = obj.getJSONArray("colors");
//                                for(int i = 0; i < colors.length(); i++) {
//                                    TextView colorTextView = new TextView(getApplicationContext());
//                                    String colorsString = colors.getString(i);
//                                    colorTextView.setText(colorsString);
//                                    int colorInt = Color.parseColor(colorsString);
//                                    colorTextView.setTextSize(20f);
//                                    colorTextView.setBackgroundColor(colorInt);
//                                    colorTextView.setTextColor(Color.WHITE);
//                                    colorTextView.setGravity(Gravity.CENTER);
//                                    colorsTextViewList.add(colorTextView);
//                                }
//                                JSONArray codes = obj.getJSONArray("codes");
//                                for(int i = 0; i <codes.length(); i++) {
//                                    TextView codeTextView = new TextView(getApplicationContext());
//                                    String colorsString = codes.getString(i);
//                                    codeTextView .setText(colorsString);
////                                    codesTextViewList.add(codeTextView);
//                                }
//                                JSONArray candidates = obj.getJSONArray("cand");
//                                for(int i = 0; i <codes.length(); i++) {
//                                    TextView candidateTextView = new TextView(getApplicationContext());
//                                    String colorsString = candidates.getString(i);
//                                    candidateTextView.setText(colorsString);
////                                    candidatesTextViewList.add(candidateTextView);
//                                }
//
//
//
//
//                                //JSONArray codes = obj.getJSONArray("codes");
////                                for(int i = 0; i < voteCodes.length; i++) {
//                                    TextView codeTextView = new TextView(getApplicationContext());
////                                    String codeString = voteCodes[i];
////                                    codeTextView.setText(codeString);
//                                    codeTextView.setTextColor(Color.BLACK);
//                                    codeTextView.setTextSize(20f);
//                                    codeTextViewList.add(codeTextView);
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            if(codeTextViewList.size() != colorsTextViewList.size()) {
//                                Toast.makeText(getApplicationContext(), "Problems with codes, aborting...", Toast.LENGTH_LONG).show();
//                                return;
//                            }
//
//                            for (int i = 0; i < codeTextViewList.size(); i++) {
//                                addTextViewToGrid(codeTextViewList.get(i),codeLeftMargin,topMargin,codeRightMargin,bottomMargin);
//                                addTextViewToGrid(colorsTextViewList.get(i),colorLeftMargin, topMargin, colorRightMargin, bottomMargin);
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            });
//            MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
//        }


}
