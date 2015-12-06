package me.soundlocker.soundlocker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.ArrayList;

import me.soundlocker.soundlocker.R;
import me.soundlocker.soundlocker.StorageWrapper;
import me.soundlocker.soundlocker.models.Application;

public class ApplicationAdder extends Activity {
    private final String FAILURE_MESSAGE = "Failed to Add App";
    private final String FAILURE_TO_ADD_APP_MESSAGE = "You already have this app added\n";
    private final String OK = "Ok";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_application_adder);
        ArrayList<String> websiteNames = StorageWrapper.getWebsiteNames(this.getApplicationContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, websiteNames);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.applicationName);
        textView.setAdapter(adapter);
    }

    /**
     * saveApplication is called whenever the user clicks the 'Add Application' button
     */
    public void addApplication(View view) {
        String appName = getAppName();
        boolean successful = StorageWrapper.saveApplication(ApplicationAdder.this.getApplicationContext(), new Application(appName));
        if (successful) {
            Intent intent = new Intent(ApplicationAdder.this, ApplicationsList.class);
            startActivity(intent);
        } else {
            showErrorDialog();
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationAdder.this);
        builder.setTitle(FAILURE_MESSAGE);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(FAILURE_TO_ADD_APP_MESSAGE);
        builder.setPositiveButton(OK, null);
        final AlertDialog alert = builder.create();
        ApplicationAdder.this.runOnUiThread(new Runnable() {
            public void run() {
                alert.show();
            }
        });
    }

    private String getAppName() {
        EditText appNameField = (EditText) findViewById(R.id.applicationName);
        String appName = appNameField.getText().toString();
        return appName;
    }
}