package com.frysoft.notifry.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frysoft.notifry.R;
import com.frysoft.notifry.adapter.TestAdapter;
import com.frysoft.notifry.utils.App;

/**
 * Created by Edwin Pichler on 08.08.2016.
 */

public class TestFragmentEdwin extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_edwin, container, false);
        setHasOptionsMenu(true);

        GridLayoutManager manager = new GridLayoutManager(App.getContext(), 7);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1; //Get adapter item from position and set the column span
            }
        });
        RecyclerView recycler_view = (RecyclerView) rootView.findViewById(R.id.recyclerview_test);

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(manager);
        recycler_view.setAdapter(new TestAdapter(30, 2));



        return rootView;
    }
}