package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class PasswordScreen extends Activity {
    private static final String APP_NAME = "app_name";
    private static final String SONG_NAME = "song_name";
    private static final String PREVIEW_URL = "preview_url";
    private static final String WEBSITE = "website";
    private static final String LABEL = "label";
    private static final int DEFAULT_PASSWORD_LENGTH = 10;
    private static final int MINIMUM_PASSWORD_LENGTH = 3;
    private static final int MAXIMUM_PASSWORD_LENGTH = 10;
    private String previewUrl;
    private String appName;
    private String password = "";
    private ApplicationPersistence storage = new ApplicationPersistence();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_screen);
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
        setSongName(intent);
        setPasswordLength();
        previewUrl = intent.getStringExtra(PREVIEW_URL);
    }

    private void setPasswordLength() {
        NumberPicker passwordLengthPicker = (NumberPicker) findViewById(R.id.passwordLength);
        passwordLengthPicker.setMinValue(MINIMUM_PASSWORD_LENGTH);
        passwordLengthPicker.setMaxValue(MAXIMUM_PASSWORD_LENGTH);
        int passwordLength = getPasswordLength();
        passwordLengthPicker.setValue(passwordLength);
        passwordLengthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                storage.saveApplicationPasswordLength(PasswordScreen.this.getApplicationContext(), appName, newVal);
            }
        });
    }

    private int getPasswordLength() {
        int passwordLength = storage.getApplicationPasswordLength(this, appName);
        if (passwordLength == -1) {
            passwordLength = DEFAULT_PASSWORD_LENGTH;
        }
        return passwordLength;
    }

    private void setSongName(Intent intent) {
        String songName = intent.getStringExtra(SONG_NAME);
        if (songName != null) {
            Button chooseSongButton = (Button) findViewById(R.id.chooseSong);
            chooseSongButton.setText(songName);
        }
    }

    private void setTitle(Intent intent) {
        appName = intent.getStringExtra(APP_NAME);
        TextView title = (TextView) findViewById(R.id.textView);
        title.setText(appName);
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
        intent.putExtra(APP_NAME, appName);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Generate Password button
     */
    public void displayPassword(View view) {
        PasswordGenerator generator = new PasswordGenerator(this, previewUrl);
        String password = generator.generatePassword();
        int passwordLength = fetchPasswordLength();
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(password.substring(0, Math.min(MAXIMUM_PASSWORD_LENGTH, passwordLength)));
    }

    private int fetchPasswordLength() {
        NumberPicker passwordLengthPicker = (NumberPicker) findViewById(R.id.passwordLength);
        int passwordLengthString = passwordLengthPicker.getValue();
        return passwordLengthString;
    }

    /**
     * Called when users clicks the Copy to Clipboard button. Will take text from textView and copy.
     */
    public void copyToClipboard(View view){
        TextView tv = (TextView)findViewById(R.id.textView);
        String text = tv.getText().toString();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(LABEL, text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Called when users click the Open Web View button.
     */
    public void openWebView(View view){
        Intent intent = new Intent(this, WebViewer.class);
        intent.putExtra(WEBSITE, appName);
        startActivity(intent);
    }
}
