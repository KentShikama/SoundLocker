package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordScreen extends Activity {

    private int passLength = 6;

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

    /** Called when the user clicks the Generate button */
    public void displayPassword(View view) {
        String password = generatePassword();
        TextView tv = (TextView)findViewById(R.id.textView);
        tv.setText(password);
    }

    private String generatePassword() {
        // Song's id
        long songId = 1;
        // Title of the song for password
        String songTitle = "";
        // Read in user's music
        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            // Query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // No media on the device
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            // Loop through all songs (TODO: Refactor as this is unnecessary)
            do {
                songId = cursor.getLong(idColumn);
                songTitle = cursor.getString(titleColumn);
            } while (cursor.moveToNext());
        }
        Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
        try {
            InputStream fileInputStream= getContentResolver().openInputStream(contentUri);
            byte[] result = inputStreamToByteArray(fileInputStream);
            byte[] hashedResult = hashSongData(result);
            String password = bytesToHex(hashedResult);
            return password.substring(0,passLength);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //parameter: the byte[] from the song
    //return: byte[] created hashing song bytes
    private byte[] hashSongData(byte[] data) {
        byte[] passBytes = hash256(data);
        return passBytes;
    }

    private byte[] hash256(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md.update(data);
        byte[] passBytes = md.digest();
        return passBytes;
    }

    //parameter: the byte array generated using hash
    //return: the array displayed as a hex string 
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    //parameter: the byte[] generated using hash
    //output: string of correct length of first i elements
    private String hashToString (byte[] hashData){
        String result = hashToStringHelper(hashData);
        return result;

    }

    private String hashToStringHelper (byte[] hashData){
        String result = null; // for UTF-8 encoding
        try {
            result = new String(hashData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;

    }

    private byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) > 0) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }


    /** Called when users clicks the Copy to Clipboard button. Will take text from textView and copy. */
    public void copyToClipboard(View view){
        TextView tv = (TextView)findViewById(R.id.textView);
        String text = tv.getText().toString();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
    }
}
