package me.soundlocker.soundlocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Task for searching a song on Spotify.
 *
 * The task returns an ArrayList of pairs of
 * 30 second preview URLs of the song and the album's image URL.
 */
class SongSearcher extends AsyncTask<String, Integer, ArrayList<ImmutableTriple<String, URL, URL>>> {

    private static final int SEARCH_RESULT_LIMIT = 7;
    private static final String TAG = "SongSearcher";
    private static final String SPOTIFY_API_SEARCH_ENDPOINT = "https://api.spotify.com/v1/search";
    private final String ITEMS = "items";
    private final String TRACKS = "tracks";
    private final String TRACK_OBJECTS_NOT_FOUND = "Cannot find tracks object";
    private final String CHECK_WIFI = "Please check your Wifi settings\n";
    private final String NO_INTERNET = "No Internet Connection";
    private final String OK = "Ok";

    private final Activity activity;

    public SongSearcher(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<ImmutableTriple<String, URL, URL>> doInBackground(String... params) {
        URL url = getUrlFromParams(params);
        ArrayList<ImmutableTriple<String, URL, URL>> previewUrls = readPreviewUrls(url);
        return previewUrls;
    }

    protected void onPostExecute(ArrayList<ImmutableTriple<String, URL, URL>> result) {
        if (result == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(NO_INTERNET);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(CHECK_WIFI);
            builder.setPositiveButton(OK, null);
            final AlertDialog alert = builder.create();
            activity.runOnUiThread(new java.lang.Runnable() {
                public void run() {
                    alert.show();
                }
            });
        }
    }

    private ArrayList<ImmutableTriple<String, URL, URL>> readPreviewUrls(URL url) {
        ArrayList<ImmutableTriple<String, URL, URL>> previewUrls = new ArrayList<>(SEARCH_RESULT_LIMIT);
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream fileInputStream = new BufferedInputStream(urlConnection.getInputStream());
            JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream));
            readSearchJSON(previewUrls, reader);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            urlConnection.disconnect();
        }
        return previewUrls;
    }

    private void readSearchJSON(ArrayList<ImmutableTriple<String, URL, URL>> previewUrls, JsonReader reader) throws IOException {
        reader.beginObject();
        String name = reader.nextName();
        if (name.equals(TRACKS)) {
            readTracks(previewUrls, reader);
        } else {
            throw new IOException(TRACK_OBJECTS_NOT_FOUND);
        }
        reader.endObject();
    }

    private void readTracks(ArrayList<ImmutableTriple<String, URL, URL>> previewUrls, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String itemName = reader.nextName();
            if (itemName.equals(ITEMS)) {
                readItems(previewUrls, reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readItems(ArrayList<ImmutableTriple<String, URL, URL>> previewUrls, JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readItemContents(previewUrls, reader);
        }
        reader.endArray();
    }

    private void readItemContents(ArrayList<ImmutableTriple<String, URL, URL>> previewUrls, JsonReader reader) throws IOException {
        ItemContentReader itemContentReader = new ItemContentReader(reader);
        String songName = itemContentReader.getSongName();
        URL previewUrl = itemContentReader.getPreviewUrl();
        URL imageUrl = itemContentReader.getImageUrl();
        previewUrls.add(new ImmutableTriple<>(songName, previewUrl, imageUrl));
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

    /**
     * Takes in a JSONReader for a single "song" item object
     * and allows access to the item's preview URL and album image URL
     */
    class ItemContentReader {
        private final String HEIGHT = "height";
        private final String URL = "url";
        private final String IMAGES = "images";
        private final String ALBUM = "album";
        private final String NAME = "name";
        private final String PREVIEW_URL = "preview_url";
        private final int MAX_IMAGE_HEIGHT = 100;

        private JsonReader reader;
        private String songName;
        private URL previewUrl;
        private URL imageUrl;

        ItemContentReader(JsonReader reader) throws IOException {
            this.reader = reader;
            readItemContent();
        }

        String getSongName() {
            return songName;
        }

        URL getPreviewUrl() {
            return previewUrl;
        }

        URL getImageUrl() {
            return imageUrl;
        }

        private void readItemContent() throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
                String symbol = reader.nextName();
                if (symbol.equals(PREVIEW_URL)) {
                    if (reader.peek() == JsonToken.STRING) {
                        String previewUrlString = reader.nextString();
                        previewUrl = new URL(previewUrlString);
                    } else {
                        reader.skipValue();
                    }
                } else if (symbol.equals(NAME)) {
                    if (reader.peek() == JsonToken.STRING) {
                        songName = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                } else if (symbol.equals(ALBUM)) {
                    readAlbum();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }

        private void readAlbum() throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals(IMAGES)) {
                    readImages();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }

        private void readImages() throws IOException {
            reader.beginArray();
            while (reader.hasNext()) {
                readImage();
            }
            reader.endArray();
        }

        private void readImage() throws IOException {
            reader.beginObject();
            int height = 0;
            while (reader.hasNext()) {
                String imageSymbol = reader.nextName();
                if (imageSymbol.equals(URL)) {
                    if (reader.peek() == JsonToken.STRING) {
                        String imageUrlString = reader.nextString();
                        assignImageURLIfSmall(imageUrlString, height);
                    } else {
                        reader.skipValue();
                    }
                } else if (imageSymbol.equals(HEIGHT)) {
                    if (reader.peek() == JsonToken.NUMBER) {
                        height = reader.nextInt();
                    } else {
                        reader.skipValue();
                    }
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }

        private void assignImageURLIfSmall(String imageUrlString, int height) throws MalformedURLException {
            if (height != 0 && height <= MAX_IMAGE_HEIGHT) {
                imageUrl = new URL(imageUrlString);
            }
        }
    }
}