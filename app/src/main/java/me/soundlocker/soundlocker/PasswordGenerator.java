package me.soundlocker.soundlocker;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class PasswordGenerator {
    private static final String TAG = "PasswordGenerator";
    private final PasswordScreen passwordScreen;
    private final String previewStringURL;
    private final String BLANK_LINES = "--------------";
    private String appName;
    private String masterId;

    PasswordGenerator(PasswordScreen passwordScreen, String previewStringURL, String appName, String masterId) {
        this.passwordScreen = passwordScreen;
        this.previewStringURL = previewStringURL;
        this.appName = appName;
        this.masterId = masterId;
    }

    String generatePassword() {
        URL url = buildURL();
        if (url == null) {
            return BLANK_LINES;
        } else {
            return generatePasswordForNotNullURL(url);
        }
    }

    private URL buildURL() {
        if (previewStringURL == null) {
            return null;
        } else {
            try {
                return new URL(previewStringURL);
            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }
    }

    private String generatePasswordForNotNullURL(URL url) {
        SongByteDataDownloader task = new SongByteDataDownloader(passwordScreen);
        task.execute(url);
        Byte[] songByteData = getSongByteData(task);
        if (songByteData == null) {
            return "--------------";
        } else {
            String password = hash(ArrayUtils.toPrimitive(songByteData));
            return password;
        }
    }

    private Byte[] getSongByteData(SongByteDataDownloader task) {
        Byte[] songByteData = new Byte[0];
        try {
            songByteData = task.get();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
        return songByteData;
    }

    /**
     * @param data the byte[] from the song
     * @return byte[] created hashing song bytes
     */
    private String hash(byte[] data) {
        byte[] passBytes = hash256(data);
        String password = bytesToString(passBytes);
        return password;
    }

    private byte[] hash256(byte[] data) {
        MessageDigest md = getMessageDigest();
        md.update(data);
        md.update(this.appName.getBytes());
        md.update(this.masterId.getBytes());

        byte[] passBytes = md.digest();
        return passBytes;
    }

    private MessageDigest getMessageDigest() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage());
        }
        return md;
    }

    /**
     * @param bytes - the byte array generated using hash
     * @return the array displayed as a hex string
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private String bytesToString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}