package me.soundlocker.soundlocker;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SongPickerScreen extends ListActivity {
    private static final String TAG = "SongPickerScreen";
    private static final String SONG_NAME = "song_name";
    private static final String PREVIEW_URL = "preview_url";
    private static final String DEFAULT_SONG = "Native"; // To promote Native by One Republic

    private ArrayList<ImmutablePair<String, Drawable>> songs = new ArrayList<>();
    private SongItemAdapter songsAdapter;
    private ArrayList<ImmutableTriple<String, URL, URL>> currentResults;
    private String appName;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        appName = getIntent().getStringExtra("app_name");
        setContentView(R.layout.activity_song_picker_screen);
        songsAdapter = new SongItemAdapter(this, songs);
        setListAdapter(songsAdapter);
        EditText songQueryEditor = (EditText) findViewById(R.id.song_query);
        songQueryEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String currentValue = editable.toString();
                if (currentValue.length() >= 2) {
                    String songName = buildURLSafeSongName(currentValue);
                    SongSearcher task = new SongSearcher(SongPickerScreen.this);
                    task.execute(songName);
                    ArrayList<ImmutableTriple<String, URL, URL>> results = getSongUrls(task);
                    updateList(results);
                } else {
                    clearList();
                }
            }
        });
    }
    private ArrayList<ImmutableTriple<String, URL, URL>> getSongUrls(SongSearcher task) {
        ArrayList<ImmutableTriple<String, URL, URL>> urls = null;
        try {
            urls = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return urls;
    }

    private String buildURLSafeSongName(String songName) {
        try {
            return URLEncoder.encode(songName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Device does not support UTF-8");
        }
        return DEFAULT_SONG;
    }

    public void updateList(ArrayList<ImmutableTriple<String, URL, URL>> results) {
        songs.clear();
        if (results != null && !results.isEmpty()) {
            currentResults = results;
            for (ImmutableTriple<String, URL, URL> song : results) {
                String songName = song.getLeft();
                URL imageUrl = song.getRight();
                SongImageDownloader imageDownloader = new SongImageDownloader(this);
                imageDownloader.execute(imageUrl);
                Drawable drawable = null;
                try {
                    drawable = imageDownloader.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                songs.add(new ImmutablePair<String, Drawable>(songName, drawable));
            }
            songsAdapter.notifyDataSetChanged();
        }
    }

    private void clearList() {
        songs.clear();
        songsAdapter.notifyDataSetChanged();
    }

    protected void onListItemClick(ListView list, View view, int position, long id) {
        ImmutableTriple<String, URL, URL> song = currentResults.get(position);
        String songName = song.getLeft();
        String previewUrl = song.getMiddle().toString();
        Intent intent = new Intent(this, PasswordScreen.class);
        intent.putExtra(SONG_NAME, songName);
        intent.putExtra(PREVIEW_URL, previewUrl);
        intent.putExtra("app_name", appName);
        startActivity(intent);
    }
}
