package me.soundlocker.soundlocker.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.soundlocker.soundlocker.R;
import me.soundlocker.soundlocker.SoundLockerConstants;

/**
 * Screen in which the user can confirm that they have generated a password.
 * Here they will be given an option to copy the generated password to the clipboard
 * or insert the generated password to the preregistered website through a webview.
 */
public class GeneratedPasswordConfirmation extends Activity {

    private static final String CLIPBOARD_LABEL = "label";
    private static final String INSERT_PASSWORD_TO_WEBVIEW = "Insert Password Into Website";
    private static final String COPY_TO_CLIPBOARD = "Copy Password To Clipboard";

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

    /**
     * Called when the user clicks the "Copy to Clipboard" or "Insert Password Into Application" button
     */
    public void insertPasswordOrCopyToClipboard(View view) {
        if (preregistered) {
            insertPasswordToWebView();
        } else {
            copyToClipboard();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goToPasswordScreen();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        goToPasswordScreen();
    }

    private void setInitialValues() {
        Intent intent = getIntent();
        setTitle(intent);
        setPasswordField(intent);
        setPreregisteredAndFinalButton(intent);
        songName = intent.getStringExtra(SoundLockerConstants.SONG_NAME);
        masterId = intent.getStringExtra(SoundLockerConstants.MASTER_ID);
    }

    private void setTitle(Intent intent) {
        appName = intent.getStringExtra(SoundLockerConstants.APP_NAME);
        TextView title = (TextView) findViewById(R.id.applicationName);
        title.setText(appName);
    }

    private void setPasswordField(Intent intent) {
        password = intent.getStringExtra(SoundLockerConstants.PASSWORD);
        TextView passwordField = (TextView) findViewById(R.id.generatedPassword);
        passwordField.setText(password);
    }

    private void setPreregisteredAndFinalButton(Intent intent) {
        Button button = (Button) findViewById(R.id.insertPasswordOrCopyToClipboard);
        preregistered = intent.getBooleanExtra(SoundLockerConstants.PREREGISTERED, false);
        if (preregistered) {
            button.setText(INSERT_PASSWORD_TO_WEBVIEW);
        } else {
            button.setText(COPY_TO_CLIPBOARD);
        }
    }

    private void insertPasswordToWebView() {
        Intent intent = new Intent(this, WebViewer.class);
        intent.putExtra(SoundLockerConstants.PREREGISTERED_WEBSITE, appName);
        intent.putExtra(SoundLockerConstants.PASSWORD, password);
        startActivity(intent);
    }

    private void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(CLIPBOARD_LABEL, password);
        clipboard.setPrimaryClip(clip);
        goToApplicationsListScreen();
    }

    private void goToApplicationsListScreen() {
        Intent intent = new Intent(this, ApplicationsList.class);
        startActivity(intent);
    }

    private void goToPasswordScreen() {
        Intent intent = new Intent();
        intent.putExtra(SoundLockerConstants.APP_NAME, appName);
        intent.putExtra(SoundLockerConstants.SONG_NAME, songName);
        intent.putExtra(SoundLockerConstants.PREREGISTERED, preregistered);
        intent.putExtra(SoundLockerConstants.MASTER_ID, masterId);
        setResult(RESULT_OK, intent);
        finish();
    }
}

