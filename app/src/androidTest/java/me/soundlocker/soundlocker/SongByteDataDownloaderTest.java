package me.soundlocker.soundlocker;

import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SongByteDataDownloaderTest {

    private static final String NATIVE_PREVIEW_URL_STRING =
            "https://p.scdn.co/mp3-preview/c58cd3ffb9e61ad38cde7658f436b4245cfd2666";
    private static final Byte[] NATIVE_BYTE_DATA_TRUNCATED = {73,68,51};

    @Test
    public void songByteDataIsConsistent() {
        SongByteDataDownloader task = new SongByteDataDownloader(null);
        URL url = buildURL();
        task.execute(url);
        Byte[] data = getSongByteData(task);
        assertEquals(data[0], NATIVE_BYTE_DATA_TRUNCATED[0]);
        assertEquals(data[1], NATIVE_BYTE_DATA_TRUNCATED[1]);
        assertEquals(data[2], NATIVE_BYTE_DATA_TRUNCATED[2]);
    }

    @Nullable
    private URL buildURL() {
        URL url = null;
        try {
            url = new URL(NATIVE_PREVIEW_URL_STRING);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private Byte[] getSongByteData(SongByteDataDownloader task) {
        Byte[] data = null;
        try {
            data = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return data;
    }
}