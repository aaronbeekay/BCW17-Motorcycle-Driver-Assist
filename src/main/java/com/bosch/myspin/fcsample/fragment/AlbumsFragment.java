package com.bosch.myspin.fcsample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.bosch.myspin.fcsample.R;
import com.bosch.myspin.fcsample.adapter.AlbumListAdapter;


/**
 * Fragment that shows albums in a GridView.
 *
 * @author Muhannad Fakhouri
 * @since 2016-11-14
 */
public class AlbumsFragment extends android.app.Fragment implements AdapterView.OnItemClickListener  {
    private View rootView;
    private GridView albumsGridView;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_albums,container,false);
        albumsGridView = (GridView) rootView.findViewById(R.id.albums_gv);
        String[] albumsTitles = new String[] {
                "1989",
                "Lemonade",
                "Illuminate",
                "Thriller",
                "Blurryface",
                "Rumours",
                "25",
                "Purpose",
                "Revival",
                "Oh My My",
                "1000 forms of fear",
                "Tapestry",
        };
        adapter = new AlbumListAdapter(getActivity(),albumsTitles);
        albumsGridView.setNumColumns(GridView.AUTO_FIT);
        albumsGridView.setColumnWidth((int) getResources().getDimension(R.dimen.albumItemWidth));
        albumsGridView.setOnItemClickListener(this);
        albumsGridView.setAdapter(adapter);
        albumsGridView.post(new Runnable() {
            @Override
            public void run() {
                if (albumsGridView.getChildCount() > 0)
                {
                    albumsGridView.requestFocusFromTouch();
                    albumsGridView.setSelection(0);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO select album and show it's corresponding tracks
    }

}