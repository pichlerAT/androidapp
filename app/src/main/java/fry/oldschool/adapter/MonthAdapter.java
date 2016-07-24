package fry.oldschool.adapter;

/**
 * Created by Edwin Pichler on 23.06.2016.
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import fry.oldschool.R;
import fry.oldschool.data.Timetable;
import fry.oldschool.data.TimetableEntry;
import fry.oldschool.utils.App;

public class MonthAdapter extends BaseAdapter {
    private GregorianCalendar mCalendar;
    private Calendar mCalendarToday;
    private DisplayMetrics mDisplayMetrics;
    private List<String> mItems;
    private int mMonth;
    private int mYear;
    private int mDaysShown;
    private int mDaysLastMonth;
    private int mDaysNextMonth;
    private int mTitleHeight, mDayHeight;
    private final String[] mDays = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
    private final int[] mDaysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    ArrayList<TimetableEntry> mEntries;

    public MonthAdapter(int month, int year, DisplayMetrics metrics) {
        mMonth = month;
        mYear = year;
        mCalendar = new GregorianCalendar(mYear, mMonth, 1);
        mCalendarToday = Calendar.getInstance();
        mDisplayMetrics = metrics;
        populateMonth();
    }

    public void setDate(int month, int year){
        this.mMonth = month;
        this.mYear = year;
        this.mDaysShown = 0;
        this.mDaysLastMonth = 0;
        this.mDaysNextMonth = 0;
        populateMonth();
        notifyDataSetChanged();
    }

    private void populateMonth() {
        mItems = new ArrayList<String>();
        for (String day : mDays) {
            mItems.add(day);
            mDaysShown++;
        }

        int firstDay = getDay(mCalendar.get(Calendar.DAY_OF_WEEK));
        int prevDay;
        if (mMonth == 0)
            prevDay = daysInMonth(11) - firstDay + 1;
        else
            prevDay = daysInMonth(mMonth - 1) - firstDay + 1;
        for (int i = 0; i < firstDay; i++) {
            mItems.add(String.valueOf(prevDay + i));
            mDaysLastMonth++;
            mDaysShown++;
        }

        int daysInMonth = daysInMonth(mMonth);
        for (int i = 1; i <= daysInMonth; i++) {
            mItems.add(String.valueOf(i));
            mDaysShown++;
        }

        mDaysNextMonth = 1;
        while (mDaysShown % 7 != 0) {
            mItems.add(String.valueOf(mDaysNextMonth));
            mDaysShown++;
            mDaysNextMonth++;
        }

        mTitleHeight = App.pixelToDPScale(30);
        int rows = (mDaysShown / 7);

        mDayHeight = (mDisplayMetrics.heightPixels - mTitleHeight
                - (rows * 8) - getBarHeight()) / (rows - 1);
    }

    private int daysInMonth(int month) {
        int daysInMonth = mDaysInMonth[month];
        if (month == 1 && mCalendar.isLeapYear(mYear))
            daysInMonth++;
        return daysInMonth;
    }

    private int getBarHeight() {
        switch (mDisplayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_HIGH:
                return 48;
            case DisplayMetrics.DENSITY_MEDIUM:
                return 32;
            case DisplayMetrics.DENSITY_LOW:
                return 24;
            default:
                return 48;
        }
    }

    private int getDay(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            case Calendar.SUNDAY:
                return 6;
            default:
                return 0;
        }
    }

    private boolean isToday(int day, int month, int year) {
        if (mCalendarToday.get(Calendar.MONTH) == month
                && mCalendarToday.get(Calendar.YEAR) == year
                && mCalendarToday.get(Calendar.DAY_OF_MONTH) == day) {
            return true;
        }
        return false;
    }

    private int[] getDate(int position) {
        int date[] = new int[3];
        if (position <= 6) {
            return null; // day names
        } else if (position <= mDaysLastMonth + 6) {
            // previous month
            date[0] = Integer.parseInt(mItems.get(position));
            if (mMonth == 0) {
                date[1] = 11;
                date[2] = mYear - 1;
            } else {
                date[1] = mMonth - 1;
                date[2] = mYear;
            }
        } else if (position <= mDaysShown - mDaysNextMonth  ) {
            // current month
            date[0] = position - (mDaysLastMonth + 6);
            date[1] = mMonth;
            date[2] = mYear;
        } else {
            // next month
            date[0] = Integer.parseInt(mItems.get(position));
            if (mMonth == 11) {
                date[1] = 0;
                date[2] = mYear + 1;
            } else {
                date[1] = mMonth + 1;
                date[2] = mYear;
            }
        }
        return date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView gridItemText;
        int[] date = getDate(position);

        // If item is a date
        if (date != null) {
            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_timetable_grid_date, parent, false);
            convertView.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.colorPrimary));
            gridItemText = (TextView) convertView.findViewById(R.id.textview_timetable_grid_date);
            gridItemText.setHeight(mDayHeight);
            if (date[1] != mMonth) {
                convertView.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.colorPreviousMonth));
            } else {
                // current month
                if (isToday(date[0], date[1], date[2] )) {
                    convertView.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.colorToday));
                }
            }
            ArrayList<TimetableEntry> entries = Timetable.getEntries(date[0], date[1], date[2]);
            LinearLayout events = (LinearLayout) convertView.findViewById(R.id.linearlayout_timetable_grid_events);
            events.removeAllViews();
            for (TimetableEntry entry : entries){
                TextView event = new TextView(App.getContext());
                event.setBackgroundColor(Color.RED);
                event.setText(entry.getTitle());
                events.addView(event);
            }
        }

        // If item is a weekday
        else {
            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_timetable_grid_day, parent, false);
            gridItemText = (TextView) convertView.findViewById(R.id.textview_timetable_grid_day);
            gridItemText.setHeight(mTitleHeight);
            gridItemText.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        }

        gridItemText.setText(mItems.get(position));
        return convertView;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}