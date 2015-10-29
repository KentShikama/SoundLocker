package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by SamW on 10/24/2015.
 */
public class ApplicationAdder extends Activity {
    Intent intent;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_application_adder);
        intent = new Intent(this, ApplicationsList.class);
        final Button addAppButt = (Button) findViewById(R.id.addApp);
        addAppButt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                EditText appName = (EditText) findViewById(R.id.applicationName);
                String appNameString = getAppName();
                ApplicationsList.updateList(appNameString);
                startActivity(intent);
            }
        });
    }
    private String getAppName() {
        EditText appName = (EditText) findViewById(R.id.applicationName);
        String appNameString = appName.getText().toString();
        return appNameString;
    }

}
