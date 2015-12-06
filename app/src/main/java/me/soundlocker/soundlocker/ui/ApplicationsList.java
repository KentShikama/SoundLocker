package me.soundlocker.soundlocker.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.software.shell.fab.ActionButton;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import me.soundlocker.soundlocker.R;
import me.soundlocker.soundlocker.SoundLockerConstants;
import me.soundlocker.soundlocker.StorageWrapper;
import me.soundlocker.soundlocker.models.Application;

/**
 * Screen showing a list of applications that the user has generated passwords for.
 * There is a '+' button that the user can click to add a new application to the list.
 * The starting screen of SoundLocker.
 */
public class ApplicationsList extends ListActivity {
    private ArrayList<Application> applicationsList;
    private String masterId;
    private final SecureRandom random = new SecureRandom();
    private Context appContext;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        appContext = this.getApplicationContext();
        setContentView(R.layout.activity_applications_list);
        handleMasterId();
        styleActionButton();
        handleApplicationsList();
    }

    /**
     * Called whenever the user clicks the '+' action button
     */
    public void addItems(View v) {
        Intent intent = new Intent(this, ApplicationAdder.class);
        startActivity(intent);
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        Application selectedApplication = applicationsList.get(position);
        String applicationName = selectedApplication.getApplicationName();
        goToPasswordScreen(applicationName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void handleMasterId() {
        boolean firstBoot = StorageWrapper.getFirstBoot(this.getApplicationContext());
        if (firstBoot) {
            masterId = new BigInteger(256, random).toString(32);
            StorageWrapper.saveMasterId(this.getApplicationContext(), masterId);
            StorageWrapper.saveBooted(this.getApplicationContext());
        } else {
            masterId = StorageWrapper.getMasterId(this.getApplicationContext());
        }
    }

    private void styleActionButton() {
        ActionButton actionButton = (ActionButton) findViewById(R.id.addApplicationButton);
        actionButton.setType(ActionButton.Type.DEFAULT);
        actionButton.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        actionButton.setImageResource(R.drawable.fab_plus_icon);
    }

    private void handleApplicationsList() {
        applicationsList = buildApplicationsList();
        ArrayList<String> applicationsNameList = buildApplicationsNameList(applicationsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, applicationsNameList);
        setListAdapter(adapter);
    }

    private ArrayList<Application> buildApplicationsList() {
        ArrayList<Application> applicationsList = StorageWrapper.getApplications(this.getApplicationContext());
        if (applicationsList == null) {
            applicationsList = new ArrayList<>();
        }
        return applicationsList;
    }

    private ArrayList<String> buildApplicationsNameList(ArrayList<Application> applicationsList) {
        ArrayList<String> applicationNameList = new ArrayList<>();
        for (Application application : applicationsList) {
            applicationNameList.add(application.getApplicationName());
        }
        return applicationNameList;
    }

    private void goToPasswordScreen(String applicationName) {
        Intent intent = new Intent(this, PasswordGenerationSettings.class);
        boolean isPreregistered = StorageWrapper.isPreregistered(this.getApplicationContext(), applicationName);
        intent.putExtra(SoundLockerConstants.APP_NAME, applicationName);
        intent.putExtra(SoundLockerConstants.MASTER_ID, masterId);
        intent.putExtra(SoundLockerConstants.PREREGISTERED, isPreregistered);
        startActivity(intent);
    }

    public void modifyMasterId(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("View/Edit Master Id");
        builder.setMessage("Warning: Any modifications to the Master ID will change your generated passwords");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(masterId);

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString() != "") {
                    masterId = input.getText().toString();
                    StorageWrapper.saveMasterId(appContext, masterId);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
