package me.soundlocker.soundlocker.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import me.soundlocker.soundlocker.models.Song;

/**
 * Task for searching a song on Spotify.
 *
 * The task returns an ArrayList of pairs of
 * 30 second preview URLs of the song and the album's image URL.
 */
public class SongSearcher extends AsyncTask<String, Integer, ArrayList<Song>> {

    private static final String TAG = "SongSearcher";

    private static final int SEARCH_RESULT_LIMIT = 7;
    private static final String SPOTIFY_API_SEARCH_ENDPOINT = "https://api.spotify.com/v1/search";
    private static final String ITEMS = "items";
    private static final String TRACKS = "tracks";
    private static final String TRACK_OBJECTS_NOT_FOUND_MESSAGE = "Cannot find tracks object";
    private static final String CHECK_WIFI_MESSAGE = "Please check your Wifi settings\n";
    private static final String NO_INTERNET_MESSAGE = "No Internet Connection";
    private static final String OK = "Ok";

    private final Activity activity;

    public SongSearcher(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<Song> doInBackground(String... params) {
        URL url = getUrlFromParams(params);
        ArrayList<Song> songs = readPreviewUrls(url);
        return songs;
    }

    @Override
    protected void onPostExecute(ArrayList<Song> result) {
        if (result == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(NO_INTERNET_MESSAGE);
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

    private ArrayList<Song> readPreviewUrls(URL url) {
        ArrayList<Song> songs = new ArrayList<>(SEARCH_RESULT_LIMIT);
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream fileInputStream = new BufferedInputStream(urlConnection.getInputStream());
            JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream));
            readSearchJSON(songs, reader);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return songs;
    }

    private void readSearchJSON(ArrayList<Song> songs, JsonReader reader) throws IOException {
        reader.beginObject();
        String name = reader.nextName();
        if (name.equals(TRACKS)) {
            readTracks(songs, reader);
        } else {
            throw new IOException(TRACK_OBJECTS_NOT_FOUND_MESSAGE);
        }
        reader.endObject();
    }

    private void readTracks(ArrayList<Song> songs, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String itemName = reader.nextName();
            if (itemName.equals(ITEMS)) {
                readItems(songs, reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readItems(ArrayList<Song> songs, JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readItemContents(songs, reader);
        }
        reader.endArray();
    }

    private void readItemContents(ArrayList<Song> songs, JsonReader reader) throws IOException {
        ItemContentReader itemContentReader = new ItemContentReader(reader);
        String songName = itemContentReader.getSongName();
        URL previewUrl = itemContentReader.getPreviewUrl();
        URL imageUrl = itemContentReader.getImageUrl();
        if (songName != null && previewUrl != null && imageUrl != null) {
            Song song = new Song(songName, previewUrl, imageUrl);
            songs.add(song);
        }
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

        private final JsonReader reader;
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
                switch (symbol) {
                    case PREVIEW_URL:
                        readPreviewUrl();
                        break;
                    case NAME:
                        readName();
                        break;
                    case ALBUM:
                        readAlbum();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
        }

        private void readPreviewUrl() throws IOException {
            if (reader.peek() == JsonToken.STRING) {
                String previewUrlString = reader.nextString();
                previewUrl = new URL(previewUrlString);
            } else {
                reader.skipValue();
            }
        }

        private void readName() throws IOException {
            if (reader.peek() == JsonToken.STRING) {
                songName = reader.nextString();
            } else {
                reader.skipValue();
            }
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