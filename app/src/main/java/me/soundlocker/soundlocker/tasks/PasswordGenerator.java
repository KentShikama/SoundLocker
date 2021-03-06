package me.soundlocker.soundlocker.tasks;

import android.util.Log;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import me.soundlocker.soundlocker.ui.PasswordGenerationSettings;

/**
 * Task for generating a password using the song, the application's name, and the {@link me.soundlocker.soundlocker.SoundLockerConstants#MASTER_ID}.
 */
public class PasswordGenerator {
    private static final String TAG = "PasswordGenerator";
    private static final String EMPTY = "";
    private static final String SHA_256 = "SHA-256";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private final PasswordGenerationSettings passwordGenerationSettings;
    private final String previewStringURL;
    private final String appName;
    private final String masterId;

    public PasswordGenerator(PasswordGenerationSettings passwordGenerationSettings, String previewStringURL, String appName, String masterId) {
        this.passwordGenerationSettings = passwordGenerationSettings;
        this.previewStringURL = previewStringURL;
        this.appName = appName;
        this.masterId = masterId;
    }

    public String generatePassword() {
        URL url = buildURL();
        if (url == null) {
            return EMPTY;
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
        SongByteDataDownloader task = new SongByteDataDownloader(passwordGenerationSettings);
        task.execute(url);
        Byte[] songByteData = getSongByteData(task);
        if (songByteData == null) {
            return EMPTY;
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

    // This method maps each byte value in the byte array to a corresponding character in the {@link #HEX_ARRAY}.
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