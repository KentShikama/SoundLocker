package me.soundlocker.soundlocker;

import android.graphics.drawable.Drawable;
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
public class SongImageDownloaderTest {

    private static final String NATIVE_IMAGE_URL_STRING =
            "https://i.scdn.co/image/45b8542038b3b21e392ffead938153448c68ab1d";
    private static final int IMAGE_WIDTH = 213;
    private static final int IMAGE_HEIGHT = 213;

    @Test
    public void songByteDataIsConsistent() {
        SongImageDownloader task = new SongImageDownloader(null);
        URL url = buildURL();
        task.execute(url);
        Drawable image = getDrawable(task);
        assertEquals(image.getIntrinsicWidth(), IMAGE_WIDTH);
        assertEquals(image.getIntrinsicHeight(), IMAGE_HEIGHT);
    }

    @Nullable
    private URL buildURL() {
        URL url = null;
        try {
            url = new URL(NATIVE_IMAGE_URL_STRING);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private Drawable getDrawable(SongImageDownloader task) {
        Drawable data = null;
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