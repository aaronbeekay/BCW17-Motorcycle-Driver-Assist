package com.bosch.myspin.fcsample.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bosch.myspin.fcsample.R;

/**
 * Simple GridView adapter.
 *
 * @author Muhannad Fakhouri
 * @since 2016-11-14
 */
public class AlbumListAdapter extends ArrayAdapter<String> {
    private int[] albumsArtsResIds = {
            R.drawable.album_1,
            R.drawable.album_2,
            R.drawable.album_3,
            R.drawable.album_4,
            R.drawable.album_5,
            R.drawable.album_6,
            R.drawable.album_7,
            R.drawable.album_8,
            R.drawable.album_9,
            R.drawable.album_10,
            R.drawable.album_11,
            R.drawable.album_12
    };

    public AlbumListAdapter(Context context, String[] values) {
        super(context, R.layout.album_list_item, android.R.id.text1, values);
    }

    @Override
    public int getCount() {
        return Math.min(super.getCount(), 12);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ((ImageView) view.findViewById(R.id.album_art_iv)).setImageResource(albumsArtsResIds[position]);
        return view;
    }


}
