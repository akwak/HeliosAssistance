package edu.akwak.heliosassistance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.security.PublicKey;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends AppCompatActivity {
    private ZXingScannerView mScannerView;
    private PublicKey pubKey = null;
    boolean signatureVerification = false;
    private TextView tv;
    private String sessionId = "";
    private String electionId = "";
    private String b64KeyBytesString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_board);
        tv = (TextView) findViewById(R.id.session_id);
    }




    public void assistantClick(View view) {
        goToMenuActivity(true);
    }

    private void goToMenuActivity( boolean assistant) {
        Intent intent = new Intent(this, MenuActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("assistant", assistant);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void observerClick(View view) {
        goToMenuActivity(false);
    }
}