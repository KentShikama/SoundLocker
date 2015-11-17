package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ApplicationAdder extends Activity {
    private static final int DEFAULT_PASSWORD_LENGTH = 6;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_application_adder);
        final Button addAppButton = (Button) findViewById(R.id.addApp);
        addAppButton.setOnClickListener(addAppListener());
    }

    private View.OnClickListener addAppListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                String appName = getAppName();
                ApplicationPersistence storage = new ApplicationPersistence();
                boolean successful = storage.addApplication(ApplicationAdder.this.getApplicationContext(), new Application(appName, DEFAULT_PASSWORD_LENGTH));
                if (successful) {
                    Intent intent = new Intent(ApplicationAdder.this, ApplicationsList.class);
                    startActivity(intent);
                } else {
                    // TODO: Show error dialog
                }
            }
        };
    }

    private String getAppName() {
        EditText appNameField = (EditText) findViewById(R.id.applicationName);
        String appName = appNameField.getText().toString();
        return appName;
    }
}