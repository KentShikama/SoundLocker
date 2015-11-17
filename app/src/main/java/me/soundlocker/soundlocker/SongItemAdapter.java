package me.soundlocker.soundlocker;

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

public class SongItemAdapter extends ArrayAdapter<ImmutablePair<String, Drawable>> {

    private final Activity context;

    public SongItemAdapter(Activity context, ArrayList<ImmutablePair<String, Drawable>> songs) {
        super(context, R.layout.activity_song_picker_screen, songs);
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.song_item, null, true);
        TextView songNameField = (TextView) rowView.findViewById(R.id.song_name);
        ImageView albumImageView = (ImageView) rowView.findViewById(R.id.song_img);
        ImmutablePair<String, Drawable> song = getItem(position);
        songNameField.setText(song.getLeft());
        albumImageView.setImageDrawable(song.getRight());
        return rowView;
    }
}