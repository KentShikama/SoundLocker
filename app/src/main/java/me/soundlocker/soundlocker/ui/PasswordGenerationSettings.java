package me.soundlocker.soundlocker.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import me.soundlocker.soundlocker.R;
import me.soundlocker.soundlocker.SoundLockerConstants;
import me.soundlocker.soundlocker.StorageWrapper;
import me.soundlocker.soundlocker.tasks.PasswordGenerator;

/**
 * Screen in which the user can choose the password length and song to generate the password with.
 */
public class PasswordGenerationSettings extends Activity {
    private static final int DEFAULT_PASSWORD_LENGTH = 10;
    private static final int MINIMUM_PASSWORD_LENGTH = 3;
    private static final int MAXIMUM_PASSWORD_LENGTH = 30;
    private String previewUrl;
    private String appName;
    private String password;
    private String masterId;
    private boolean preregistered;
    private String songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_screen);
        Intent intent = getIntent();
        setInitialValues(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        setInitialValues(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                setInitialValues(intent);
            }
        }
    }

    /**
     * Called when the user clicks the Choose Song button
     */
    public void showSongPicker(View view) {
        Intent intent = new Intent(this, SongPicker.class);
        intent.putExtra(SoundLockerConstants.APP_NAME, appName);
        intent.putExtra(SoundLockerConstants.MASTER_ID, masterId);
        intent.putExtra(SoundLockerConstants.PREREGISTERED, preregistered);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Generate Password button
     */
    public void generatePasswordAndContinue(View view) {
        generatePassword();
        if (password != null && !password.isEmpty()) {
            showPasswordConfirmationScreen();
        }
    }

    private void setInitialValues(Intent intent) {
        setTitle(intent);
        setSongName(intent);
        setPasswordLength();
        previewUrl = intent.getStringExtra(SoundLockerConstants.PREVIEW_URL);
        masterId = intent.getStringExtra(SoundLockerConstants.MASTER_ID);
        preregistered = intent.getBooleanExtra(SoundLockerConstants.PREREGISTERED, false);
    }

    private void setTitle(Intent intent) {
        appName = intent.getStringExtra(SoundLockerConstants.APP_NAME);
        TextView title = (TextView) findViewById(R.id.applicationName);
        title.setText(appName);
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
                StorageWrapper.saveApplicationPasswordLength(PasswordGenerationSettings.this.getApplicationContext(), appName, newVal);
            }
        });
    }

    private int getPasswordLength() {
        int passwordLength = StorageWrapper.getApplicationPasswordLength(this, appName);
        if (passwordLength == -1) {
            passwordLength = DEFAULT_PASSWORD_LENGTH;
        }
        return passwordLength;
    }

    private void setSongName(Intent intent) {
        songName = intent.getStringExtra(SoundLockerConstants.SONG_NAME);
        if (songName != null) {
            Button chooseSongButton = (Button) findViewById(R.id.chooseSong);
            chooseSongButton.setText(songName);
        }
    }

    private void generatePassword() {
        PasswordGenerator generator = new PasswordGenerator(this, previewUrl, appName, masterId);
        String longPassword = generator.generatePassword();
        if (!longPassword.isEmpty()) {
            int passwordLength = fetchPasswordLength();
            password = longPassword.substring(0, Math.min(MAXIMUM_PASSWORD_LENGTH, passwordLength));
        }
    }

    private int fetchPasswordLength() {
        NumberPicker passwordLengthPicker = (NumberPicker) findViewById(R.id.passwordLength);
        int passwordLengthString = passwordLengthPicker.getValue();
        return passwordLengthString;
    }

    private void showPasswordConfirmationScreen() {
        Intent intent = new Intent(this, GeneratedPasswordConfirmation.class);
        intent.putExtra(SoundLockerConstants.APP_NAME, appName);
        intent.putExtra(SoundLockerConstants.SONG_NAME, songName);
        intent.putExtra(SoundLockerConstants.PASSWORD, password);
        intent.putExtra(SoundLockerConstants.MASTER_ID, masterId);
        intent.putExtra(SoundLockerConstants.PREREGISTERED, preregistered);
        startActivity(intent);
    }
}
