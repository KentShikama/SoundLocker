package me.soundlocker.soundlocker.tasks;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import me.soundlocker.soundlocker.models.Song;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SongSearcherTest {

    private static final String NATIVE_RESULTING_SONG_NAME = "Counting Stars";
    private static final String NATIVE_PREVIEW_URL_STRING =
            "https://p.scdn.co/mp3-preview/c58cd3ffb9e61ad38cde7658f436b4245cfd2666";
    private static final String NATIVE_IMAGE_URL_STRING =
            "https://i.scdn.co/image/81fd93b405773dffa494ead6fdf9c5a13d52eed9";
    private static final String ALBUM_NAME_WITH_SPACE = "native ";
    private static final String TAG = "SongSearcherTest";

    @Test
    public void readNativeByOneRepublicSong() {
        SongSearcher task = new SongSearcher(null);
        task.execute(ALBUM_NAME_WITH_SPACE);
        ArrayList<Song> results = getSongUrls(task);
        assertEquals(results.get(0).getSongName(), NATIVE_RESULTING_SONG_NAME);
        assertEquals(results.get(0).getPreviewUrl().toString(), NATIVE_PREVIEW_URL_STRING);
        assertEquals(results.get(0).getImageUrl().toString(), NATIVE_IMAGE_URL_STRING);
    }

    private ArrayList<Song> getSongUrls(SongSearcher task) {
        ArrayList<Song> results = null;
        try {
            results = task.get();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
        return results;
    }
}
