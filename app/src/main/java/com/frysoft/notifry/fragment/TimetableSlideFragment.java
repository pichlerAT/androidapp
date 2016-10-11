package com.frysoft.notifry.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.frysoft.notifry.R;
import com.frysoft.notifry.activity.MainActivity;
import com.frysoft.notifry.activity.TimetableCreateActivity;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;

import java.lang.reflect.Field;

/**
 * Created by Edwin Pichler on 14.08.2016.
 */

public class TimetableSlideFragment extends Fragment {

    private static final short NUM_PAGES = 1200;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable_slide, container, false);
        setHasOptionsMenu(true);

        TimetableFragment.mCurrentDate = null; //Reset current date everytime timetable is reopened, to deny false month display
        mPager = (ViewPager) rootView.findViewById(R.id.viewpager_timetable_slide);
        mPagerAdapter = new ScreenSlidePagerAdapter(MainActivity.fm, Date.getToday());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(NUM_PAGES/2);


        return rootView;
    }


    private DatePickerDialog createDialogWithoutDateField() {
        int day = Date.getToday().day;
        int month = Date.getToday().month-1;
        int year = Date.getToday().year;
        DatePickerDialog dialog = new DatePickerDialog(App.getContext(), mDateSetListener, day, month, year);//Because date picker returns month from 0-11
        dialog.getDatePicker().updateDate(year, month, day);

        try {
            Field[] datePickerDialogFields = dialog.getClass().getDeclaredFields();

            for (Field datePickerDialogField : datePickerDialogFields) {

                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dialog);
                    Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();

                    for (Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {}
        return dialog;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear+=1; //Because date picker returns month from 0-11
            Date date = new Date(dayOfMonth, monthOfYear, year);
            TimetableFragment.mCurrentDate = null;
            mPagerAdapter = new ScreenSlidePagerAdapter(MainActivity.fm, date);
            mPager.setAdapter(mPagerAdapter);
            mPager.setCurrentItem(NUM_PAGES/2);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        int stringId = App.getContext().getApplicationInfo().labelRes;
        MainActivity.TOOLBAR_TITLE.setText(App.getContext().getString(stringId));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.fab.isShown()){
            MainActivity.fab.hide();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.timetable_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_calendar:
                DatePickerDialog dialog = createDialogWithoutDateField();
                dialog.show();
                return true;

            case R.id.action_week:
                FragmentTransaction transaction = MainActivity.fm.beginTransaction();
                transaction.replace(R.id.frame_fragment_main, new TimetableWeekFragment()).commit();
                return true;

            case R.id.action_add:
                Intent intent = new Intent(App.getContext(), TimetableCreateActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private Date mDate;
        public ScreenSlidePagerAdapter(FragmentManager fm, Date date) {
            super(fm);
            this.mDate = date;
        }

        @Override
        public Fragment getItem(int position) {
            return TimetableFragment.create(position, mDate);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }


    }


}
