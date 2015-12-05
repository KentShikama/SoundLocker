package me.soundlocker.soundlocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ApplicationAdderScreen extends Activity {
    private static final int DEFAULT_PASSWORD_LENGTH = 6;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_application_adder);
        final Button addAppButton = (Button) findViewById(R.id.addApplication);
        addAppButton.setOnClickListener(addAppListener());
        ArrayList<String> websiteNames = StorageWrapper.getWebsiteNames(this.getApplicationContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, websiteNames);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.applicationName);
        textView.setAdapter(adapter);
    }

    private View.OnClickListener addAppListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                String appName = getAppName();
                boolean successful = StorageWrapper.addApplication(ApplicationAdderScreen.this.getApplicationContext(), new Application(appName, DEFAULT_PASSWORD_LENGTH));
                if (successful) {
                    Intent intent = new Intent(ApplicationAdderScreen.this, ApplicationsListScreen.class);
                    startActivity(intent);
                } else {
                    showErrorDialog();
                }
            }

            private void showErrorDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationAdderScreen.this);
                builder.setTitle("Failed to Add App");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("You already have this app added\n");
                builder.setPositiveButton("Ok", null);
                final AlertDialog alert = builder.create();
                ApplicationAdderScreen.this.runOnUiThread(new Runnable() {
                    public void run() {
                        alert.show();
                    }
                });
            }
        };
    }

    private String getAppName() {
        EditText appNameField = (EditText) findViewById(R.id.applicationName);
        String appName = appNameField.getText().toString();
        return appName;
    }
}