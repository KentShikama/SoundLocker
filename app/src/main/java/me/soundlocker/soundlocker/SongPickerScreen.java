package me.soundlocker.soundlocker;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SongPickerScreen extends ListActivity {
    private static final String TAG = "SongPickerScreen";
    private static final String SONG_NAME = "song_name";
    private static final String PREVIEW_URL = "preview_url";
    private static final String DEFAULT_SONG = "Native"; // the song album Native by One Republic
    private static final String NO_UTF8 = "Device does not support UTF-8";
    private static final String UTF8 = "UTF-8";

    private ArrayList<ImmutablePair<String, Drawable>> songItemList = new ArrayList<>();
    private SongItemAdapter songsAdapter;
    private ArrayList<ImmutableTriple<String, URL, URL>> currentResults;
    private String appName;
    private String masterId;
    private boolean preregistered;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_song_picker_screen);
        saveIntentExtras();
        setUpSongListAdapter();
        setUpSongQueryEditor();
    }

    private void saveIntentExtras() {
        Intent intent = getIntent();
        appName = intent.getStringExtra(ApplicationConstants.APP_NAME);
        masterId = intent.getStringExtra(ApplicationConstants.MASTER_ID);
        preregistered = intent.getBooleanExtra(ApplicationConstants.PREREGISTERED, false);
    }

    private void setUpSongListAdapter() {
        songsAdapter = new SongItemAdapter(this, songItemList);
        setListAdapter(songsAdapter);
    }

    private void setUpSongQueryEditor() {
        EditText songQueryEditor = (EditText) findViewById(R.id.song_query);
        songQueryEditor.addTextChangedListener(new SongQueryWatcher());
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
            return URLEncoder.encode(songName, UTF8);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, NO_UTF8);
        }
        return DEFAULT_SONG; // Returns the default song if URL safe song name cannot be created
    }

    public void updateSongList(ArrayList<ImmutableTriple<String, URL, URL>> results) {
        songItemList.clear();
        if (results != null && !results.isEmpty()) {
            currentResults = results;
            for (ImmutableTriple<String, URL, URL> song : results) {
                ImmutablePair<String, Drawable> songItem = buildSongItem(song);
                songItemList.add(songItem);
            }
            songsAdapter.notifyDataSetChanged();
        }
    }

    private ImmutablePair<String, Drawable> buildSongItem(ImmutableTriple<String, URL, URL> song) {
        String songName = song.getLeft();
        URL imageUrl = song.getRight();
        SongImageDownloader imageDownloader = new SongImageDownloader(this);
        imageDownloader.execute(imageUrl);
        Drawable drawable = buildDrawable(imageDownloader);
        return new ImmutablePair<>(songName, drawable);
    }

    private Drawable buildDrawable(SongImageDownloader imageDownloader) {
        Drawable drawable = null;
        try {
            drawable = imageDownloader.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    private void clearList() {
        songItemList.clear();
        songsAdapter.notifyDataSetChanged();
    }

    protected void onListItemClick(ListView list, View view, int position, long id) {
        ImmutableTriple<String, URL, URL> song = currentResults.get(position);
        String songName = song.getLeft();
        String previewUrl = song.getMiddle().toString();
        goToPasswordScreen(songName, previewUrl);
    }

    private void goToPasswordScreen(String songName, String previewUrl) {
        Intent intent = new Intent(this, PasswordScreen.class);
        intent.putExtra(ApplicationConstants.APP_NAME, appName);
        intent.putExtra(ApplicationConstants.MASTER_ID, masterId);
        intent.putExtra(ApplicationConstants.PREREGISTERED, preregistered);
        intent.putExtra(SONG_NAME, songName);
        intent.putExtra(PREVIEW_URL, previewUrl);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sendOnBackData();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        sendOnBackData();
    }

    private void sendOnBackData() {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.APP_NAME, appName);
        intent.putExtra(ApplicationConstants.PREREGISTERED, preregistered);
        intent.putExtra(ApplicationConstants.MASTER_ID, masterId);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private class SongQueryWatcher implements TextWatcher {
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
                searchForSongWithCurrentValue(currentValue);
            } else {
                clearList();
            }
        }

        private void searchForSongWithCurrentValue(String currentValue) {
            String songName = buildURLSafeSongName(currentValue);
            SongSearcher task = new SongSearcher(SongPickerScreen.this);
            task.execute(songName);
            ArrayList<ImmutableTriple<String, URL, URL>> results = getSongUrls(task);
            updateSongList(results);
        }
    }
}
