package me.soundlocker.soundlocker.ui;

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

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import me.soundlocker.soundlocker.R;
import me.soundlocker.soundlocker.SoundLockerConstants;
import me.soundlocker.soundlocker.models.Song;
import me.soundlocker.soundlocker.tasks.SongImageDownloader;
import me.soundlocker.soundlocker.tasks.SongSearcher;

/**
 * Screen in which the user can type in a song name in the query field.
 * Results that are queried from the Spotify API are then shown in the list view.
 */
public class SongPicker extends ListActivity {
    private static final String TAG = "SongPicker";
    private static final String DEFAULT_SONG = "Native"; // the song album Native by One Republic
    private static final String UTF8 = "UTF-8";
    private static final String NO_UTF8_MESSAGE = "Device does not support UTF-8";

    private final ArrayList<ImmutablePair<String, Drawable>> songItemList = new ArrayList<>();
    private SongItemAdapter songsAdapter;
    private ArrayList<Song> currentResults;
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

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        Song song = currentResults.get(position);
        String songName = song.getSongName();
        String previewUrl = song.getPreviewUrl().toString();
        goToPasswordScreen(songName, previewUrl);
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

    private void saveIntentExtras() {
        Intent intent = getIntent();
        appName = intent.getStringExtra(SoundLockerConstants.APP_NAME);
        masterId = intent.getStringExtra(SoundLockerConstants.MASTER_ID);
        preregistered = intent.getBooleanExtra(SoundLockerConstants.PREREGISTERED, false);
    }

    private void setUpSongListAdapter() {
        songsAdapter = new SongItemAdapter(this, songItemList);
        setListAdapter(songsAdapter);
    }

    private void setUpSongQueryEditor() {
        EditText songQueryEditor = (EditText) findViewById(R.id.songQuery);
        songQueryEditor.addTextChangedListener(new SongQueryWatcher());
    }

    private ArrayList<Song> getSongs(SongSearcher task) {
        ArrayList<Song> songs = null;
        try {
            songs = task.get();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
        return songs;
    }

    private String buildURLSafeSongName(String songName) {
        try {
            return URLEncoder.encode(songName, UTF8);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, NO_UTF8_MESSAGE);
        }
        return DEFAULT_SONG; // Returns the default song if URL safe song name cannot be created
    }

    private void updateSongList(ArrayList<Song> songs) {
        songItemList.clear();
        if (songs != null && !songs.isEmpty()) {
            currentResults = songs;
            for (Song song : songs) {
                ImmutablePair<String, Drawable> songItem = buildSongItem(song);
                songItemList.add(songItem);
            }
            songsAdapter.notifyDataSetChanged();
        }
    }

    private ImmutablePair<String, Drawable> buildSongItem(Song song) {
        String songName = song.getSongName();
        URL imageUrl = song.getImageUrl();
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

    private void goToPasswordScreen(String songName, String previewUrl) {
        Intent intent = new Intent(this, PasswordGenerationSettings.class);
        intent.putExtra(SoundLockerConstants.APP_NAME, appName);
        intent.putExtra(SoundLockerConstants.MASTER_ID, masterId);
        intent.putExtra(SoundLockerConstants.PREREGISTERED, preregistered);
        intent.putExtra(SoundLockerConstants.SONG_NAME, songName);
        intent.putExtra(SoundLockerConstants.PREVIEW_URL, previewUrl);
        startActivity(intent);
    }

    private void sendOnBackData() {
        Intent intent = new Intent();
        intent.putExtra(SoundLockerConstants.APP_NAME, appName);
        intent.putExtra(SoundLockerConstants.PREREGISTERED, preregistered);
        intent.putExtra(SoundLockerConstants.MASTER_ID, masterId);
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
            SongSearcher task = new SongSearcher(SongPicker.this);
            task.execute(songName);
            ArrayList<Song> songs = getSongs(task);
            updateSongList(songs);
        }
    }
}
