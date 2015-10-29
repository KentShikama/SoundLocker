package me.soundlocker.soundlocker;

import android.util.JsonReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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