package me.soundlocker.soundlocker;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.apache.commons.lang3.tuple.ImmutablePair;

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
class SongSearcher extends AsyncTask<String, Integer, ArrayList<ImmutablePair<URL, URL>>> {

    private static final int SEARCH_RESULT_LIMIT = 3;
    private static final String TAG = "SongSearcher";
    private static final String SPOTIFY_API_SEARCH_ENDPOINT = "https://api.spotify.com/v1/search";

    @Override
    protected ArrayList<ImmutablePair<URL, URL>> doInBackground(String... params) {
        URL url = getUrlFromParams(params);
        ArrayList<ImmutablePair<URL, URL>> previewUrls = readPreviewUrls(url);
        return previewUrls;
    }

    private ArrayList<ImmutablePair<URL, URL>> readPreviewUrls(URL url) {
        ArrayList<ImmutablePair<URL, URL>> previewUrls = new ArrayList<>(SEARCH_RESULT_LIMIT);
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

    private void readSearchJSON(ArrayList<ImmutablePair<URL, URL>> previewUrls, JsonReader reader) throws IOException {
        reader.beginObject();
        String name = reader.nextName();
        if (name.equals("tracks")) {
            readTracks(previewUrls, reader);
        } else {
            throw new IOException("Cannot find tracks object");
        }
        reader.endObject();
    }

    private void readTracks(ArrayList<ImmutablePair<URL, URL>> previewUrls, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String itemName = reader.nextName();
            if (itemName.equals("items")) {
                readItems(previewUrls, reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readItems(ArrayList<ImmutablePair<URL, URL>> previewUrls, JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readItemContents(previewUrls, reader);
        }
        reader.endArray();
    }

    private void readItemContents(ArrayList<ImmutablePair<URL, URL>> previewUrls, JsonReader reader) throws IOException {
        ItemContentReader itemContentReader = new ItemContentReader(reader);
        URL previewUrl = itemContentReader.getPreviewUrl();
        URL imageUrl = itemContentReader.getImageUrl();
        previewUrls.add(new ImmutablePair<>(previewUrl, imageUrl));
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
        private JsonReader reader;
        private URL previewUrl;
        private URL imageUrl;

        ItemContentReader(JsonReader reader) throws IOException {
            this.reader = reader;
            readItemContent();
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
                if (symbol.equals("preview_url")) {
                    String previewUrlString = reader.nextString();
                    previewUrl = new URL(previewUrlString);
                } else if (symbol.equals("album")) {
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
                if (name.equals("images")) {
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
            while (reader.hasNext()) {
                String imageSymbol = reader.nextName();
                if (imageSymbol.equals("url")) {
                    String imageUrlString = reader.nextString();
                    assignImageURLIf640x640(imageUrlString);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }

        private void assignImageURLIf640x640(String imageUrlString) throws MalformedURLException {
            if (imageUrl == null) {
                imageUrl = new URL(imageUrlString);
            }
        }
    }
}