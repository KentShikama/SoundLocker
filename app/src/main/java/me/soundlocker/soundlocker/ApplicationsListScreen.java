package me.soundlocker.soundlocker;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

public class ApplicationsListScreen extends ListActivity {
    private ArrayList<Application> applicationsList;
    private ArrayList<String> applicationsNameList;
    private ArrayAdapter<String> adapter;
    private boolean firstBoot;
    private String masterId;
    private SecureRandom random = new SecureRandom();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_applications_list);
        handleMasterId();
        applicationsList = buildApplicationsList();
        applicationsNameList = buildApplicationsNameList(applicationsList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, applicationsNameList);
        setListAdapter(adapter);
    }

    private void handleMasterId() {
        firstBoot = StorageWrapper.getFirstBoot(this.getApplicationContext());
        if (firstBoot == true) {
            masterId = new BigInteger(256, random).toString(32);
            StorageWrapper.saveMasterId(this.getApplicationContext(), masterId);
            StorageWrapper.saveFirstBoot(this.getApplicationContext(), false);
        } else {
            masterId = StorageWrapper.getMasterId(this.getApplicationContext());
        }
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

    public void addItems(View v) {
        Intent intent = new Intent(this, ApplicationAdderScreen.class);
        startActivity(intent);
    }

    protected void onListItemClick(ListView list, View view, int position, long id) {
        Application selectedApplication = applicationsList.get(position);
        String applicationName = selectedApplication.getApplicationName();
        goToPasswordScreen(applicationName);
    }

    private void goToPasswordScreen(String applicationName) {
        Intent intent = new Intent(this, PasswordScreen.class);
        boolean isPreregistered = StorageWrapper.isPreregistered(this.getApplicationContext(), applicationName);
        intent.putExtra(ApplicationConstants.APP_NAME, applicationName);
        intent.putExtra(ApplicationConstants.MASTER_ID, masterId);
        intent.putExtra(ApplicationConstants.PREREGISTERED, isPreregistered);
        startActivity(intent);
    }
}
