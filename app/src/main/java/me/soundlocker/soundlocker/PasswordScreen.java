package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;

import java.net.MalformedURLException;
import java.net.URL;
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

    /** Called when the user clicks the Generate button */
    public void displayPassword(View view) {
        String password = generatePassword();
        TextView tv = (TextView)findViewById(R.id.textView);
        tv.setText(password);
    }

    private String generatePassword() {
        URL url = buildURL();
        DownloadSongByteData task = new DownloadSongByteData();
        task.execute(url);
        Byte[] songByteData = getSongByteData(task);
        String password = hash(ArrayUtils.toPrimitive(songByteData));
        return password;
    }

    private Byte[] getSongByteData(DownloadSongByteData task) {
        Byte[] songByteData = new Byte[0];
        try {
            songByteData = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return songByteData;
    }

    // TODO: Implement real hash function
    private String hash(byte[] result) {
        return String.valueOf(result[0]) + String.valueOf(result[1]) + String.valueOf(result[2]);
    }

    private URL buildURL() {
        URL url = null;
        try {
            url = new URL("https://p.scdn.co/mp3-preview/6af04a222889b42239a867b0c9991e7fdc599762");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
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
