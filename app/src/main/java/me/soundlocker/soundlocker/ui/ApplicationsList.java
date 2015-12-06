package me.soundlocker.soundlocker.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.software.shell.fab.ActionButton;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import me.soundlocker.soundlocker.R;
import me.soundlocker.soundlocker.StorageWrapper;
import me.soundlocker.soundlocker.models.Application;
import me.soundlocker.soundlocker.ApplicationConstants;

public class ApplicationsList extends ListActivity {
    private ArrayList<Application> applicationsList;
    private String masterId;
    private final SecureRandom random = new SecureRandom();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_applications_list);
        handleMasterId();
        styleActionButton();
        applicationsList = buildApplicationsList();
        ArrayList<String> applicationsNameList = buildApplicationsNameList(applicationsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, applicationsNameList);
        setListAdapter(adapter);
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

    /**
     * addItems is called whenever the user clicks the '+' action button
     */
    public void addItems(View v) {
        Intent intent = new Intent(this, ApplicationAdder.class);
        startActivity(intent);
    }

    protected void onListItemClick(ListView list, View view, int position, long id) {
        Application selectedApplication = applicationsList.get(position);
        String applicationName = selectedApplication.getApplicationName();
        goToPasswordScreen(applicationName);
    }

    private void goToPasswordScreen(String applicationName) {
        Intent intent = new Intent(this, PasswordGenerationSettings.class);
        boolean isPreregistered = StorageWrapper.isPreregistered(this.getApplicationContext(), applicationName);
        intent.putExtra(ApplicationConstants.APP_NAME, applicationName);
        intent.putExtra(ApplicationConstants.MASTER_ID, masterId);
        intent.putExtra(ApplicationConstants.PREREGISTERED, isPreregistered);
        startActivity(intent);
    }
}
