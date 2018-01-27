package edu.akwak.heliosassistance;


import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Settings {
    public static final String SESSION_ID = "session_id";
    public static final String KEY_RAND = "key_rand";
    public static final String SIGN = "sign";
  //  public static final String HELIOS_URL = "http://192.168.0.14:8000/";
    public static final String HELIOS_URL = BuildConfig.HELIOS_URL;

    public static PublicKey getPublicKey(String response) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = new byte[0];
        keyBytes = Base64.decode(response.getBytes("UTF-8"), Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = null;
        kf = KeyFactory.getInstance("RSA");

        return kf.generatePublic(spec);
    }
}
