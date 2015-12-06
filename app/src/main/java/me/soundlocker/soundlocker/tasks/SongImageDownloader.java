package me.soundlocker.soundlocker.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Task for downloading a song's album image
 */
public class SongImageDownloader extends AsyncTask<URL, Integer, Drawable> {

    private static final String TAG = "SongImageDownloader";
    private static final String NO_INTERNET_TITLE = "No Internet Connection";
    private static final String CHECK_WIFI_MESSAGE = "Please check your Wifi settings\n";
    private static final String OK = "Ok";
    private final Activity activity;

    public SongImageDownloader(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Drawable doInBackground(URL... params) {
        URL url = params[0];
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream fileInputStreamOfImage = new BufferedInputStream(urlConnection.getInputStream());
            Drawable image = buildDrawableFromFileStreamOfImage(fileInputStreamOfImage);
            return image;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    private Drawable buildDrawableFromFileStreamOfImage(InputStream fileInputStream) {
        return Drawable.createFromStream(fileInputStream, "");
    }

    @Override
    protected void onPostExecute(Drawable result) {
        if (result == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(NO_INTERNET_TITLE);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(CHECK_WIFI_MESSAGE);
            builder.setPositiveButton(OK, null);
            final AlertDialog alert = builder.create();
            activity.runOnUiThread(new java.lang.Runnable() {
                public void run() {
                    alert.show();
                }
            });
        }
    }
}
