package com.frysoft.notifry.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.GridView;

import java.lang.reflect.Field;

import com.frysoft.notifry.R;
import com.frysoft.notifry.activity.MainActivity;
import com.frysoft.notifry.activity.TimetableCreateActivity;
import com.frysoft.notifry.adapter.TimetableMonthAdapter;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;

/**
 * Created by Edwin Pichler on 28.04.2016.
 */
public class TimetableFragment extends Fragment {

    protected TimetableMonthAdapter mMonthAdapter;
    protected static Date mCurrentDate = null;
    protected static int mLastPos = -1;
    boolean mFirstMonth = false;
    protected static Date mPassedDate;

    public static TimetableFragment create(int position, Date date){
        TimetableFragment fragment = new TimetableFragment();
        Bundle args = new Bundle();
        args.putInt("page", position);
        fragment.setArguments(args);
        mPassedDate = date;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);

        int current_position = getArguments().getInt("page");
        Date next_date = null;

        if (mCurrentDate == null) {
            mCurrentDate = mPassedDate;
            mFirstMonth = true;
        }
        else{
            next_date = mCurrentDate.copy();
            if ((current_position - mLastPos) > 0){
                mCurrentDate.addMonths(1);
                next_date.addMonths(2);
            }
            else {
                mCurrentDate.subtractMonths(1);
                next_date.subtractMonths(2);
            }
            mFirstMonth = false;
        }


        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        GridView timetable = (GridView) rootView.findViewById(R.id.gridview_timetable_dates);

        if (mFirstMonth)
            mMonthAdapter = new TimetableMonthAdapter(mCurrentDate.month, mCurrentDate.year, metrics);
        else
            mMonthAdapter = new TimetableMonthAdapter(next_date.month, next_date.year, metrics);
        timetable.setAdapter(mMonthAdapter);

        String month_name = mCurrentDate.getMonthName();
        MainActivity.TOOLBAR_TITLE.setText(month_name);

        mLastPos = current_position;
        return rootView;
    }

    public TimetableMonthAdapter getAdapter(){
        return this.mMonthAdapter;
    }

    public Date getCurrentDate(){
        return this.mCurrentDate;
    }

}
