package me.soundlocker.soundlocker;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SongSearcherTest {

    private static final String NATIVE_URL_STRING =
            "https://p.scdn.co/mp3-preview/c58cd3ffb9e61ad38cde7658f436b4245cfd2666";

    @Test
    public void readNativeByOneRepublicSong() {
        SongSearcher task = new SongSearcher();
        task.execute("native");
        ArrayList<URL> urls = getSongUrls(task);
        assertEquals(urls.get(0).toString(), NATIVE_URL_STRING);
    }

    private ArrayList<URL> getSongUrls(SongSearcher task) {
        ArrayList<URL> urls = null;
        try {
            urls = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return urls;
    }
}
