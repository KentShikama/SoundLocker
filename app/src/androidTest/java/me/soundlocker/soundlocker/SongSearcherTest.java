package me.soundlocker.soundlocker;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SongSearcherTest {

    private static final String NATIVE_RESULTING_SONG_NAME = "Counting Stars";
    private static final String NATIVE_PREVIEW_URL_STRING =
            "https://p.scdn.co/mp3-preview/c58cd3ffb9e61ad38cde7658f436b4245cfd2666";
    private static final String NATIVE_IMAGE_URL_STRING =
            "https://i.scdn.co/image/45b8542038b3b21e392ffead938153448c68ab1d";

    @Test
    public void readNativeByOneRepublicSong() {
        SongSearcher task = new SongSearcher(null);
        task.execute("native ");
        ArrayList<ImmutableTriple<String, URL, URL>> results = getSongUrls(task);
        assertEquals(results.get(0).getLeft().toString(), NATIVE_RESULTING_SONG_NAME);
        assertEquals(results.get(0).getMiddle().toString(), NATIVE_PREVIEW_URL_STRING);
        assertEquals(results.get(0).getRight().toString(), NATIVE_IMAGE_URL_STRING);
    }

    private ArrayList<ImmutableTriple<String, URL, URL>> getSongUrls(SongSearcher task) {
        ArrayList<ImmutableTriple<String, URL, URL>> results = null;
        try {
            results = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return results;
    }
}
