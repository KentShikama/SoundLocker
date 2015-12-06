package me.soundlocker.soundlocker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.soundlocker.soundlocker.models.Application;
import me.soundlocker.soundlocker.models.PreregisteredWebsite;
import vendor.JSONReader;

public class StorageWrapper {

    private static final String PREFS_NAME = "soundlocker";
    private static final String APPLICATION_LIST = "applications";
    private static final String FIRST_BOOT = "firstBoot";
    private static final String MASTER_ID = "master_Id";
    private static final String WEBSITES = "websites";
    private static final String SHORT_NAME = "shortname";
    private static final String LOGIN_URL = "loginUrl";
    private static final String PASSWORD_FIELD_ELEMENT = "passwordFieldElement";
    private static final Gson gson = new Gson();

    private StorageWrapper() {}

    public static ArrayList<Application> getApplications(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.contains(APPLICATION_LIST)) {
            String jsonFavorites = settings.getString(APPLICATION_LIST, null);
            Application[] applicationList = gson.fromJson(jsonFavorites, Application[].class);
            ArrayList<Application> applications = new ArrayList<>(Arrays.asList(applicationList));
            return applications;
        } else {
            return null;
        }
    }

    public static boolean getFirstBoot(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.contains(FIRST_BOOT)) {
            return settings.getBoolean(FIRST_BOOT, true);
        } else {
            return true;
        }
    }

    public static String getMasterId(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.contains(MASTER_ID)) {
            return settings.getString(MASTER_ID, null);
        } else {
            return null;
        }
    }

    private static void saveApplications(Context context, List<Application> applications) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        String applicationsString = gson.toJson(applications);
        editor.putString(APPLICATION_LIST, applicationsString);
        editor.apply();
    }

    public static void saveBooted(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        editor.putBoolean(FIRST_BOOT, false);
        editor.apply();
    }

    public static void saveMasterId(Context context, String masterId) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        editor.putString(MASTER_ID, masterId);
        editor.apply();
    }

    /**
     * addApplication Adds a new application to the application list in storage
     *
     * @param context        The current application context
     * @param newApplication The application to add
     * @return true if successfully added
     */
    public static boolean addApplication(Context context, Application newApplication) {
        List<Application> applications = getApplications(context);
        if (applications == null) {
            applications = new ArrayList<>();
        }
        for (Application application : applications) {
            if (application.getApplicationName().equals(newApplication.getApplicationName())) {
                return false;
            }
        }
        applications.add(newApplication);
        saveApplications(context, applications);
        return true;
    }

    /**
     * getApplicationPasswordLength Gets the password length for an application
     *
     * @param context         The current application context
     * @param applicationName The name of the application to update the password length for
     * @return the application's password length or -1 if not obtainable
     */
    public static int getApplicationPasswordLength(Context context, String applicationName) {
        List<Application> applications = getApplications(context);
        if (applications == null) {
            applications = new ArrayList<>();
        }
        for (Application application : applications) {
            if (application.getApplicationName().equals(applicationName)) {
                return application.getPasswordLength();
            }
        }
        return -1;
    }

    /**
     * saveApplicationPasswordLength Updates the password length for an application
     *
     * @param context           The current application context
     * @param applicationName   The name of the application to update the password length for
     * @param newPasswordLength The new password length for this application
     * @return true if successfully updated
     */
    public static boolean saveApplicationPasswordLength(Context context, String applicationName, int newPasswordLength) {
        List<Application> applications = getApplications(context);
        for (Application application : applications) {
            if (application.getApplicationName().equals(applicationName)) {
                application.setPasswordLength(newPasswordLength);
                saveApplications(context, applications);
                return true;
            }
        }
        return false;
    }

    /**
     * Unused
     * @param context
     * @param application
     */
    public static void removeApplication(Context context, Application application) {
        List<Application> applications = getApplications(context);
        if (applications != null) {
            applications.remove(application);
            saveApplications(context, applications);
        }
    }

    private static ArrayList<PreregisteredWebsite> getWebsites(Context context) {
        String jsonString = JSONReader.loadJSONFromAsset(context);
        ArrayList<PreregisteredWebsite> preregisteredWebsites = buildWebsites(jsonString);
        return preregisteredWebsites;
    }

    public static ArrayList<String> getWebsiteNames(Context context) {
        String jsonString = JSONReader.loadJSONFromAsset(context);
        ArrayList<PreregisteredWebsite> preregisteredWebsites = buildWebsites(jsonString);
        ArrayList<String> websiteNames = new ArrayList<>();
        for (PreregisteredWebsite preregisteredWebsite : preregisteredWebsites) {
            websiteNames.add(preregisteredWebsite.getShortName());
        }
        return websiteNames;
    }

    private static ArrayList<PreregisteredWebsite> buildWebsites(String jsonString) {
        ArrayList<PreregisteredWebsite> preregisteredWebsites = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(jsonString);
            JSONArray jsonWebsites = jsonobject.getJSONArray(WEBSITES);
            for (int position = 0; position < jsonWebsites.length(); position++) {
                PreregisteredWebsite preregisteredWebsite = readWebsite(jsonWebsites, position);
                preregisteredWebsites.add(preregisteredWebsite);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return preregisteredWebsites;
    }

    private static PreregisteredWebsite readWebsite(JSONArray jsonWebsites, int i) throws JSONException {
        JSONObject jsonWebsite = (JSONObject) jsonWebsites.get(i);
        String shortname = jsonWebsite.getString(SHORT_NAME);
        String loginUrl = jsonWebsite.getString(LOGIN_URL);
        String passwordFieldElement = jsonWebsite.getString(PASSWORD_FIELD_ELEMENT);
        return new PreregisteredWebsite(shortname, loginUrl, passwordFieldElement);
    }

    public static boolean isPreregistered(Context context, String applicationName) {
        ArrayList<PreregisteredWebsite> preregisteredWebsites = getWebsites(context);
        for (PreregisteredWebsite preregisteredWebsite : preregisteredWebsites) {
            if (preregisteredWebsite.getShortName().equals(applicationName)) {
                return true;
            }
        }
        return false;
    }

    public static PreregisteredWebsite getWebsite(Context context, String applicationName) {
        ArrayList<PreregisteredWebsite> preregisteredWebsites = getWebsites(context);
        for (PreregisteredWebsite preregisteredWebsite : preregisteredWebsites) {
            if (preregisteredWebsite.getShortName().equals(applicationName)) {
                return preregisteredWebsite;
            }
        }
        return null;
    }
}