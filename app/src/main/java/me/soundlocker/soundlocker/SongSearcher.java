package me.soundlocker.soundlocker;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SongSearcher extends AsyncTask<String, Integer, ArrayList<URL>> {

    private static final int SEARCH_RESULT_LIMIT = 3;
    private static final String TAG = "SongSearcher";
    private static final String SPOTIFY_API_SEARCH_ENDPOINT = "https://api.spotify.com/v1/search";

    @Override
    protected ArrayList<URL> doInBackground(String... params) {
        URL url = getUrlFromParams(params);
        ArrayList<URL> previewUrls = readPreviewUrls(url);
        return previewUrls;
    }

    private ArrayList<URL> readPreviewUrls(URL url) {
        ArrayList<URL> previewUrls = new ArrayList<>(SEARCH_RESULT_LIMIT);
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream fileInputStream = new BufferedInputStream(urlConnection.getInputStream());
            JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream));
            readSearchJSON(previewUrls, reader);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            urlConnection.disconnect();
        }
        return previewUrls;
    }

    private void readSearchJSON(ArrayList<URL> previewUrls, JsonReader reader) throws IOException {
        reader.beginObject();
        String name = reader.nextName();
        if (name.equals("tracks")) {
            readTracks(previewUrls, reader);
        } else {
            throw new IOException("Cannot find tracks object");
        }
        reader.endObject();
    }

    private void readTracks(ArrayList<URL> previewUrls, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String itemName = reader.nextName();
            if (itemName.equals("items")) {
                readItem(previewUrls, reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readItem(ArrayList<URL> previewUrls, JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readItemContents(previewUrls, reader);
        }
        reader.endArray();
    }

    private void readItemContents(ArrayList<URL> previewUrls, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String symbol = reader.nextName();
            if (symbol.equals("preview_url")) {
                String previewUrl = reader.nextString();
                previewUrls.add(new URL(previewUrl));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private URL getUrlFromParams(String[] params) {
        String urlString = SPOTIFY_API_SEARCH_ENDPOINT +
                "?type=track" +
                "&limit=" + String.valueOf(SEARCH_RESULT_LIMIT) +
                "&q=" + params[0];
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }
        return url;
    }
}
