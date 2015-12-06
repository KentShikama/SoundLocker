package me.soundlocker.soundlocker.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
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
    private static final String OK = "OK";
    private static final String CANCEL = "Cancel";
    private static final String VIEW_EDIT_MASTER_ID_MESSAGE = "View/Edit Master ID";
    private static final String MASTER_ID_MEANING_MESSAGE = "The Master ID is a unique identifier for your application. Changing it will change your generated passwords.";
    private static final int LEFT_RIGHT_TEXTVIEW_PADDING = 50;
    private static final int TOP_BOTTOM_TEXTVIEW_PADDING = 30;
    private ArrayList<Application> applicationsList;
    private String masterId;
    private final SecureRandom random = new SecureRandom();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
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

    /**
     * Called when "View/Edit Master ID" menu item is clicked
     */
    public void masterIdPopup(MenuItem item) {
        AlertDialog.Builder builder = buildEditMasterIdPopup();
        builder.show();
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
            masterId = new BigInteger(64, random).toString(32);
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

    private AlertDialog.Builder buildEditMasterIdPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(VIEW_EDIT_MASTER_ID_MESSAGE);
        builder.setMessage(MASTER_ID_MEANING_MESSAGE);
        final EditText masterIdField = buildMasterIdEditText();
        builder.setView(masterIdField);
        configureOkButton(builder, masterIdField);
        configureCancelButton(builder);
        return builder;
    }

    private EditText buildMasterIdEditText() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT); // Set input as a password
        input.setText(masterId);
        input.setPadding(LEFT_RIGHT_TEXTVIEW_PADDING, TOP_BOTTOM_TEXTVIEW_PADDING,
                LEFT_RIGHT_TEXTVIEW_PADDING, TOP_BOTTOM_TEXTVIEW_PADDING);
        return input;
    }

    private void configureOkButton(AlertDialog.Builder builder, final EditText masterIdField) {
        builder.setPositiveButton(OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String masterId = masterIdField.getText().toString();
                if (!masterId.isEmpty()) {
                    StorageWrapper.saveMasterId(ApplicationsList.this.getApplicationContext(), masterId);
                }
            }
        });
    }

    private void configureCancelButton(AlertDialog.Builder builder) {
        builder.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }
}
