package me.soundlocker.soundlocker;

import android.util.Log;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class PasswordGenerator {
    private static final String TAG = "PasswordGenerator";
    private static final String BLANK_LINES = "--------------";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private final PasswordScreen passwordScreen;
    private final String previewStringURL;
    private final String SHA_256 = "SHA-256";
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
            return BLANK_LINES;
        } else {
            String password = generatePasswordFromSongByteData(ArrayUtils.toPrimitive(songByteData));
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

    private String generatePasswordFromSongByteData(byte[] songByteData) {
        byte[] hashedByteData = hashByteData(songByteData);
        String password = convertBytesToString(hashedByteData);
        return password;
    }

    private byte[] hashByteData(byte[] data) {
        MessageDigest md = buildMessageDigest(data);
        byte[] hashedByteData = md.digest();
        return hashedByteData;
    }

    private MessageDigest buildMessageDigest(byte[] data) {
        MessageDigest md = buildMessageDigestAux();
        try {
            md.update(data);
            md.update(convertStringToHex(appName));
            md.update(convertStringToHex(masterId));
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return md;
    }

    private MessageDigest buildMessageDigestAux() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage());
        }
        return md;
    }

    private byte[] convertStringToHex(String string) throws DecoderException {
        return Hex.decodeHex(string.toCharArray());
    }

    private String convertBytesToString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}