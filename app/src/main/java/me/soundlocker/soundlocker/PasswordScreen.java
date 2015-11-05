package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class PasswordScreen extends Activity {

    private static final String TAG = "PasswordScreen";
    private String previewUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_screen);

        Intent intent = getIntent();
        String appName = intent.getStringExtra("app_name");
        TextView title = (TextView) findViewById(R.id.textView);
        title.setText(appName);

        String songName = intent.getStringExtra("song_name");
        if (songName != null) {
            Button chooseSongButton = (Button) findViewById(R.id.chooseSong);
            chooseSongButton.setText(songName);
        }

        previewUrl = intent.getStringExtra("preview_url");
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
     * Called when the user clicks the Choose Song button
     */
    public void showSongPicker(View view) {
        Intent intent = new Intent(this, SongPickerScreen.class);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Generate Password button
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
        if (url == null) {
            return "--------------";
        } else {
            return generatePasswordForNotNullURL(url);
        }
    }

    private URL buildURL() {
        if (previewUrl == null) {
            return null;
        } else {
            try {
                return new URL(previewUrl);
            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }
    }

    private String generatePasswordForNotNullURL(URL url) {
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
    private static String bytesToString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
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
