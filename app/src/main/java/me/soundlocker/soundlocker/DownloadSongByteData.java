package me.soundlocker.soundlocker;

import android.os.AsyncTask;

import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadSongByteData extends AsyncTask<URL, Integer, Byte[]> {

    @Override
    protected Byte[] doInBackground(URL... params) {
        URL url = params[0];
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream fileInputStream = new BufferedInputStream(urlConnection.getInputStream());
            byte[] result = inputStreamToByteArray(fileInputStream);
            return ArrayUtils.toObject(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
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
