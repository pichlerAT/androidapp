package com.frysoft.notifry.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.frysoft.notifry.R;
import com.frysoft.notifry.activity.MainActivity;
import com.frysoft.notifry.activity.TimetableCreateActivity;
import com.frysoft.notifry.adapter.TimetableWeekAdapter;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.DateSpan;

import java.util.ArrayList;

/**
 * Created by Edwin Pichler on 04.08.2016.
 */
public class TimetableWeekFragment extends Fragment {
    private TimetableWeekAdapter mAdapter = null;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable_week, container, false);
        setHasOptionsMenu(true);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_timetable_week);

        calculateWeek();
        RecyclerView.LayoutManager layout_manager = new LinearLayoutManager(App.getContext());
        mRecyclerView.setLayoutManager(layout_manager);
        mRecyclerView.addItemDecoration(new LinearSpacingItemDecoration(App.pixelToDPScale(2), App.pixelToDPScale(5)));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        int position = mAdapter.getItemPosition(Date.getToday().getString());
        mRecyclerView.smoothScrollToPosition(position);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private void calculateWeek(){
        Date today = Date.getToday();
        Date first_day_of_week = today.getFirstDayOfWeek();
        Date week = first_day_of_week.copy();
        week.addDays(6);

        DateSpan span = new DateSpan(first_day_of_week, week);
        ArrayList<Date> dates = span.getDates();

        mAdapter = new TimetableWeekAdapter(dates);
    }

    public class LinearSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpacingTop;
        private int mSpacingSide;

        public LinearSpacingItemDecoration(int spacingTop, int spacingSide) {
            this.mSpacingTop = spacingTop;
            this.mSpacingSide = spacingSide;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = mSpacingSide;
            outRect.right = mSpacingSide;
            outRect.top = mSpacingTop;
            outRect.bottom = mSpacingTop;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.timetable_week_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_events:
                FragmentTransaction transaction = MainActivity.fm.beginTransaction();
                transaction.replace(R.id.frame_fragment_main, new TimetableEventsFragment()).commit();
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