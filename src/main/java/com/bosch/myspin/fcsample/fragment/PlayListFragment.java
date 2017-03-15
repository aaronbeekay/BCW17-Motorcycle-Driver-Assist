package com.bosch.myspin.fcsample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bosch.myspin.fcsample.activity.MainActivity;
import com.bosch.myspin.fcsample.R;

/**
 * Fragment that a ListView.
 *
 * @author Muhannad Fakhouri
 * @since 2016-11-14
 */
public class PlayListFragment extends android.app.Fragment implements AdapterView.OnItemClickListener {
    private View rootView;
    private ListView playlistListView;
    private ArrayAdapter<String> adapter;
    private String[] tracksTitles;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_playlist,container,false);
        playlistListView = (ListView) rootView.findViewById(R.id.playlist_listview);
        tracksTitles = new String[]{
                "Welcome to New York",
                "Blank space",
                "Style",
                "Out of the woods",
                "All you had to do was stay",
                "Shake it off",
                "I wish you would",
                "Bad blood",
                "Wildest dreams",
                "How you get the girl",
                "This love",
                "I know Places",
                "Clean"
        };
        adapter = new ArrayAdapter<>(getActivity(),R.layout.track_list_item, tracksTitles);
        playlistListView.setAdapter(adapter);
        playlistListView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO play the track
    }

}
