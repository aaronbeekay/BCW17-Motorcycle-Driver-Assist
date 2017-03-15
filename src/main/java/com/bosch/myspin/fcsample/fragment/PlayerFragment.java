package com.bosch.myspin.fcsample.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bosch.myspin.fcsample.R;

/**
 * Fragment that shows music player.
 *
 * @author Muhannad Fakhouri
 * @since 2016-11-14
 */
public class PlayerFragment extends Fragment{
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player,container,false);
        return rootView;
    }
}
