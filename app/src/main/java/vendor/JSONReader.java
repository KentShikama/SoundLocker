package vendor;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Loads JSON from an JSON file under the assets folder
 * Code borrowed from http://stackoverflow.com/a/13814551/2750819
 */
public class JSONReader {

    private static final String WEBSITES_JSON_FILE = "websites.json";
    private static final String TAG = "JSONReader";
    private static final String UTF8 = "UTF-8";

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(WEBSITES_JSON_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, UTF8);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return json;
    }
}
