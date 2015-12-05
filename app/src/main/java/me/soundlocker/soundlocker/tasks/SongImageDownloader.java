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
 * Task for downloading an album image
 */
public class SongImageDownloader extends AsyncTask<URL, Integer, Drawable> {

    private static final String TAG = "SongImageDownloader";
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
            InputStream fileInputStream = new BufferedInputStream(urlConnection.getInputStream());
            Drawable image = Drawable.createFromStream(fileInputStream, "");
            return image;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    protected void onPostExecute(Byte[] result) {
        if (result == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("No Internet Connection");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage("Please check your Wifi settings\n");
            builder.setPositiveButton("Ok", null);
            final AlertDialog alert = builder.create();
            activity.runOnUiThread(new java.lang.Runnable() {
                public void run() {
                    alert.show();
                }
            });
        }
    }
}
