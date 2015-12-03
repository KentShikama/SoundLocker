package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// TODO: Create way to go backwards and pass back appName, preregistered, and songName
public class PasswordConfirmationScreen extends Activity {

    private static final String LABEL = "label";
    private final String INSERT_PASSWORD_TO_WEBVIEW = "Insert Password Into Website";
    private final String COPY_TO_CLIPBOARD = "Copy Password To Clipboard";

    private String appName;
    private String songName;
    private String password;
    private String masterId;
    private boolean preregistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_confirmation_screen);
        setInitialValues();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setInitialValues();
    }

    private void setInitialValues() {
        Intent intent = getIntent();
        setTitle(intent);
        setPreregisteredAndFinalButton(intent);
        password = intent.getStringExtra(ApplicationConstants.PASSWORD);
        songName = intent.getStringExtra(ApplicationConstants.SONG_NAME);
        masterId = intent.getStringExtra(ApplicationConstants.MASTER_ID);
    }

    private void setTitle(Intent intent) {
        appName = intent.getStringExtra(ApplicationConstants.APP_NAME);
        TextView title = (TextView) findViewById(R.id.applicationName);
        title.setText(appName);
    }

    private void setPreregisteredAndFinalButton(Intent intent) {
        Button button = (Button) findViewById(R.id.insertPasswordOrCopyToClipboard);
        preregistered = intent.getBooleanExtra(ApplicationConstants.PREREGISTERED, false);
        if (preregistered) {
            button.setText(INSERT_PASSWORD_TO_WEBVIEW);
        } else {
            button.setText(COPY_TO_CLIPBOARD);
        }
    }

    /**
     * insertPasswordOrCopyToClipboard is called when the user
     * clicks the Copy to Clipboard or Insert Password Into Application
     */
    public void insertPasswordOrCopyToClipboard(View view) {
        if (preregistered) {
            insertPasswordToWebView();
        } else {
            copyToClipboard();
        }
    }

    private void insertPasswordToWebView(){
        Intent intent = new Intent(this, WebViewer.class);
        intent.putExtra(ApplicationConstants.WEBSITE, appName);
        intent.putExtra(ApplicationConstants.PASSWORD, password);
        startActivity(intent);
    }

    private void copyToClipboard(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(LABEL, password);
        clipboard.setPrimaryClip(clip);
        // TODO: Set intent back to application list
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sendOnBackData();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        sendOnBackData();
    }

    private void sendOnBackData() {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.APP_NAME, appName);
        intent.putExtra(ApplicationConstants.SONG_NAME, songName);
        intent.putExtra(ApplicationConstants.PREREGISTERED, preregistered);
        intent.putExtra(ApplicationConstants.MASTER_ID, masterId);
        setResult(RESULT_OK, intent);
        finish();
    }
}

