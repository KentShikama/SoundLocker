package me.soundlocker.soundlocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SongByteDataDownloader extends AsyncTask<URL, Integer, Byte[]> {

    private final Activity activity;

    public SongByteDataDownloader(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Byte[] doInBackground(URL... params) {
        URL url = params[0];
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream fileInputStream = new BufferedInputStream(urlConnection.getInputStream());
            byte[] result = inputStreamToByteArray(fileInputStream);
            return ArrayUtils.toObject(result);
        } catch (IOException e) {
            Log.e("SongByteDataDownloader", e.getMessage());
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

    private byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) > 0) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }
}
