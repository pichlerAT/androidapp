package com.frysoft.notifry.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frysoft.notifry.R;

/**
 * Created by Edwin Pichler on 24.06.2016.
 */
public class TimetableEventsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable_events, container, false);
        setHasOptionsMenu(true);


        return rootView;
    }
}