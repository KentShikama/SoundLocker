package me.soundlocker.soundlocker.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;

import me.soundlocker.soundlocker.R;

/**
 * Represents one song in the {@link me.soundlocker.soundlocker.ui.SongPicker}'s list view.
 * It consists of an album image and the song name.
 */
class SongItemAdapter extends ArrayAdapter<ImmutablePair<String, Drawable>> {

    private final Activity context;

    SongItemAdapter(Activity context, ArrayList<ImmutablePair<String, Drawable>> songs) {
        super(context, R.layout.activity_song_picker_screen, songs);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.song_item, null, true);
        TextView songNameField = (TextView) rowView.findViewById(R.id.songName);
        ImageView albumImageView = (ImageView) rowView.findViewById(R.id.songImage);
        ImmutablePair<String, Drawable> song = getItem(position);
        songNameField.setText(song.getLeft());
        albumImageView.setImageDrawable(song.getRight());
        return rowView;
    }
}