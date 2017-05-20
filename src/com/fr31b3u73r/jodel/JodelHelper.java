package com.fr31b3u73r.jodel;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;
import java.util.Random;

public class JodelHelper {
    public static String getRandomColor() {
        String[] allColors = {JodelPostColor.GREEN, JodelPostColor.BLUE, JodelPostColor.RED,
                JodelPostColor.ORANGE, JodelPostColor.TEAL, JodelPostColor.YELLOW};

        int idx = new Random().nextInt(allColors.length);
        String randomColor = (allColors[idx]);
        return randomColor;
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String hexString = formatter.toString();
        formatter.close();
        return hexString;
    }

    protected static String calculateHMAC(String key, String data)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes())).toUpperCase();
    }
}
