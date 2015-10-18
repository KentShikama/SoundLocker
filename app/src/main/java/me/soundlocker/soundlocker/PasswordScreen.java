package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PasswordScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_screen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the user clicks the Generate Password button
     * @param view
     */
    public void displayPassword(View view) {
        String password = generatePassword();
        int passwordLength = getPasswordLength();
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(password.substring(0, Math.min(6, passwordLength)));
    }

    private int getPasswordLength() {
        EditText passwordLengthField = (EditText) findViewById(R.id.passwordLength);
        String passwordLengthString = passwordLengthField.getText().toString();
        if (passwordLengthString.isEmpty()) {
            return 5;
        } else {
            return Integer.valueOf(passwordLengthString);
        }
    }

    private String generatePassword() {
        URL url = buildURL();
        SongByteDataDownloader task = new SongByteDataDownloader(this);
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
            Log.e("PasswordScreen", e.getMessage());
        } catch (ExecutionException e) {
            Log.e("PasswordScreen", e.getMessage());
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
        byte[] passBytes = md.digest();
        return passBytes;
    }

    private MessageDigest getMessageDigest() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Log.e("PasswordScreen", e.getMessage());
        }
        return md;
    }

    /**
     * @param bytes - the byte array generated using hash
     * @return the array displayed as a hex string
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static String bytesToString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private URL buildURL() {
        String songName = buildSongName();
        SongSearcher task = new SongSearcher();
        task.execute(songName);
        ArrayList<ImmutablePair<URL, URL>> urls = getSongUrls(task);
        return urls.get(0).getLeft();
    }

    private String buildSongName() {
        EditText passwordLengthField = (EditText) findViewById(R.id.chooseSong);
        String passwordLengthString = passwordLengthField.getText().toString();
        String songName = "";
        if (passwordLengthString.isEmpty()) {
            songName = "native";
        } else {
            songName = passwordLengthString;
        } return songName;
    }

    private ArrayList<ImmutablePair<URL, URL>> getSongUrls(SongSearcher task) {
        ArrayList<ImmutablePair<URL, URL>> urls = null;
        try {
            urls = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return urls;
    }

    /**
     * Called when users clicks the Copy to Clipboard button. Will take text from textView and copy.
     * @param view
     */
    public void copyToClipboard(View view){
        TextView tv = (TextView)findViewById(R.id.textView);
        String text = tv.getText().toString();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
    }
}
