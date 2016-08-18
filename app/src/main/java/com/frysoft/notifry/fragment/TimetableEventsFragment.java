package com.frysoft.notifry.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.frysoft.notifry.R;
import com.frysoft.notifry.activity.MainActivity;
import com.frysoft.notifry.activity.TimetableCreateActivity;
import com.frysoft.notifry.adapter.TimetableEventsAdapter;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;

/**
 * Created by Edwin Pichler on 24.06.2016.
 */
public class TimetableEventsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable_events, container, false);
        setHasOptionsMenu(true);

        Date date_end = Date.getToday();
        date_end.addDays(30);
        TimetableEventsAdapter adapter = new TimetableEventsAdapter(Data.Timetable.getEvents(Date.getToday(), date_end));
        ExpandableListView lv = (ExpandableListView) rootView.findViewById(R.id.listview_timetable_events);
        lv.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.timetable_events_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_month:
                FragmentTransaction transaction = MainActivity.fm.beginTransaction();
                transaction.replace(R.id.frame_fragment_main, new TimetableSlideFragment()).commit();
                return true;
            case R.id.action_add:
                Intent intent = new Intent(App.getContext(), TimetableCreateActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}