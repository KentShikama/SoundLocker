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

public class ApplicationsList extends ListActivity {
    private static final String PASSWORD_LENGTH = "password_length";
    private static final String MASTER_ID = "master_id";

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
        applicationsList = buildApplicationsList();
        applicationsNameList = buildApplicationsNameList(applicationsList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, applicationsNameList);
        setListAdapter(adapter);

        firstBoot = StorageWrapper.getFirstBoot(this.getApplicationContext());
        if (firstBoot == false){
            masterId = new BigInteger(256, random).toString(32);
            StorageWrapper.saveMasterId(ApplicationsList.this.getApplicationContext(), masterId);
            StorageWrapper.saveFirstBoot(ApplicationsList.this.getApplicationContext(), true);
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
            applicationNameList.add(application.applicationName);
        }
        return applicationNameList;
    }

    public void addItems(View v) {
        Intent intent = new Intent(this, ApplicationAdder.class);
        startActivity(intent);
    }

    protected void onListItemClick(ListView list, View view, int position, long id) {
        Intent intent = new Intent(this, PasswordScreen.class);
        Application selectedApplication = applicationsList.get(position);
        boolean isPreregistered = StorageWrapper.isPreregistered(this.getApplicationContext(), selectedApplication.applicationName);
        intent.putExtra(ApplicationConstants.APP_NAME, selectedApplication.applicationName);
        intent.putExtra(PASSWORD_LENGTH, selectedApplication.passwordLength);
        intent.putExtra(MASTER_ID, masterId);
        intent.putExtra(ApplicationConstants.PREREGISTERED, isPreregistered);
        startActivity(intent);
    }
}
