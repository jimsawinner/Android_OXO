package com.teamtechuk.app.android_oxo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamtechuk.app.android_oxo.R;

/**
 * Created by jimdixon on 16/05/2017.
 */

public class ScoreBoardFragment extends Fragment {
    private View mContentView = null;
    private  boolean isServer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.scoreboard, null);
        return mContentView;
    }
}
