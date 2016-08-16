package com.frysoft.notifry.adapter;

/**
 * Created by Edwin Pichler on 23.06.2016.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.frysoft.notifry.R;
import com.frysoft.notifry.activity.TimetableCreateActivity;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.data.Event;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;

public class TimetableMonthAdapter extends BaseAdapter {
    private DisplayMetrics mDisplayMetrics;
    private List<String> mItems;
    private int mMonth;
    private int mYear;
    private int mDaysShown;
    private int mDaysLastMonth;
    private int mDaysNextMonth;
    private int mTitleHeight, mDayHeight;
    private final String[] mDays = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
    private Date mFirstOfMonth;

    public TimetableMonthAdapter(int month, int year, DisplayMetrics metrics) {
        mFirstOfMonth = new Date(1, month, year);
        mMonth = month;
        mYear = year;
        mDisplayMetrics = metrics;
        populateMonth();
    }

    public void setDate(int month, int year){
        mFirstOfMonth = new Date(1, month, year);
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

        int firstDay = mFirstOfMonth.getDayOfWeek();
        int prevDay;
        if (mMonth == 1)
            prevDay = Date.getDaysOfMonth(12, mYear) - firstDay + 1;
        else
            prevDay = Date.getDaysOfMonth(mMonth - 1, mYear) - firstDay + 1;
        for (int i = 0; i < firstDay; i++) {
            mItems.add(String.valueOf(prevDay + i));
            mDaysLastMonth++;
            mDaysShown++;
        }

        int daysInMonth = Date.getDaysOfMonth(mMonth, mYear);
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
        final int[] date = getDate(position);
        final int colorBackground = App.getColorFromID(R.color.colorBackground);


        // If item is a date
        if (date != null) {
            final Date current_date = new Date(date[0], date[1], date[2]);
            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_timetable_grid_date, parent, false);
            convertView.setBackgroundColor(colorBackground);
            gridItemText = (TextView) convertView.findViewById(R.id.textview_timetable_grid_date);
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mDayHeight));
            if (date[1] != mMonth) {
                convertView.setBackgroundColor(App.getColorFromID(R.color.colorPreviousMonth));
            } else {
                // If date is today
                if (current_date.getString().equals(Date.getToday().getString())) {
                    convertView.setBackgroundColor(App.getColorFromID(R.color.colorHighlight));
                }
            }
            final ArrayList<Event> events = Data.Timetable.getEvents(current_date, current_date);
            LinearLayout events_layout = (LinearLayout) convertView.findViewById(R.id.linearlayout_timetable_grid_events);
            if (events.size() > 0 && events_layout != null) {
                events_layout.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                /***
                 *
                 * LOGIK FÜR DIE ANZEIGE
                 * Wenn die getEntry Methode alle entries nach Datum sortiert bekommt,
                 * kann die Logik wie folgt aussehen:
                 * Es werden die entries vom Vortag abgefragt.
                 * Es wird überprüft ob der Entry von heute auch am Vortrag vorhanden ist
                 * Wenn ja, wird überprüft, an welcher Position in der Liste der Eintrag ist.
                 *
                 * Somit kann bei der addView Methode der Index mitgegeben werden.
                 *
                 */
                for (final Event event : events) {
                    final TextView event_textview = (TextView) inflater.inflate(R.layout.timetable_month_event_template, null);
/*
                    if (entry.month_pos > 0){
                        for (int i = 0; i<entry.month_pos; i++) {
                            TextView placeholder = (TextView) inflater.inflate(R.layout.timetable_month_event_template, null);
                            params.setMargins(5, 5, 5, 5);
                            placeholder.setLayoutParams(params);
                            placeholder.setText("Placeholder");
                            placeholder.setAlpha(0);
                            events.addView(placeholder);
                        }
                    }*/
                    event_textview.setText(event.getTitle());
                    event_textview.setLayoutParams(params);
                    /*
                    event_textview.post(new Runnable() {
                        @Override
                        public void run() {
                            int event_width  = event_textview.getMeasuredWidth();
                            int num_of_days = event.getDateSpan().getDateStart().getDaysUntil(current_date);
                            int margin = (-event_width) * num_of_days + 5;

                            LinearLayout.LayoutParams params2;

                            //If date field is in between start date and end date
                            if (!event.getDateSpan().getDateStart().getString().equals(current_date.getString()) && !event.getDateSpan().getDateEnd().getString().equals(current_date.getString())){
                                params2 = new LinearLayout.LayoutParams((event_width * (num_of_days+1) + 300), LinearLayout.LayoutParams.WRAP_CONTENT);
                                event_textview.setBackground(App.getDrawableFromID(R.drawable.month_event_between_style));
                                params2.setMargins(margin,5,0,5);
                            }
                            //If if date field is last date field
                            else if (!event.getDateSpan().getDateStart().getString().equals(current_date.getString())){
                                params2 = new LinearLayout.LayoutParams((event_width * (num_of_days+1) - 10), LinearLayout.LayoutParams.WRAP_CONTENT); // - 10 because of the 2*5 margin
                                event_textview.setBackground(App.getDrawableFromID(R.drawable.month_event_before_style));
                                params2.setMargins(margin,5,5,5);
                            }
                            //If date field is first date field
                            else if (!event.getDateSpan().getDateEnd().getString().equals(current_date.getString())){
                                params2 = new LinearLayout.LayoutParams((event_width * (num_of_days+1) + 300), LinearLayout.LayoutParams.WRAP_CONTENT);
                                event_textview.setBackground(App.getDrawableFromID(R.drawable.month_event_after_style));
                                params2.setMargins(5,5,0,5);
                            }
                            //If there's no date span
                            else{
                                params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params2.setMargins(5,5,5,5); //Setting margin with the XML template didn't work
                            }
                            event_textview.setLayoutParams(params2);
                        }
                    });
*/
                    events_layout.addView(event_textview);
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View events_view = View.inflate(App.getContext(), R.layout.fragment_timetable_events_daily, null);
                    LinearLayout linearlayout_events = (LinearLayout) events_view.findViewById(R.id.linearlayout_timetable_events_daily);
                    final LinearLayout linearlayout_events_entries = (LinearLayout) events_view.findViewById(R.id.linearlayout_timetable_events_daily_entries);

                    TextView date_header = (TextView) events_view.findViewById(R.id.textview_timetable_events_daily_date);
                    date_header.setText(current_date.getString());

                    //Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(App.getContext());
                    builder.setView(linearlayout_events)

                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    final AlertDialog dialog = builder.create();

                    //Add each entry to the list
                    for (final Event event_in_dialog : events){
                        final RelativeLayout entry_template = (RelativeLayout) View.inflate(App.getContext(), R.layout.timetable_event_template, null);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 10, 0, 10); //Setting margin with the XML template didn't work
                        entry_template.setLayoutParams(params);

                        TextView entry_title = (TextView) entry_template.findViewById(R.id.textview_timetable_event_title);
                        entry_title.setText(event_in_dialog.getTitle());

                        TextView entry_duration = (TextView) entry_template.findViewById(R.id.textview_timetable_event_time);
                        String duration_text = event_in_dialog.getTimeStart().getString() + " - " + event_in_dialog.getTimeEnd().getString();
                        entry_duration.setText(duration_text);

                        TextView entry_description =(TextView) entry_template.findViewById(R.id.textview_timetable_event_description);
                        entry_description.setText(event_in_dialog.getDescription());

                        linearlayout_events_entries.addView(entry_template);

                        TextView delete = (TextView) entry_template.findViewById(R.id.textview_timetable_event_delete);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                event_in_dialog.getEntry().delete();
                                linearlayout_events_entries.removeView(entry_template);
                            }
                        });

                        entry_template.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(App.getContext(), TimetableCreateActivity.class);
                                intent.putExtra("entry", event_in_dialog.getEntry().getId());
                                App.getContext().startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                    }
                    dialog.show();
                }
            });
        }

        // If item is a weekday
        else {
            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_timetable_grid_day, parent, false);
            gridItemText = (TextView) convertView.findViewById(R.id.textview_timetable_grid_day);
            gridItemText.setHeight(mTitleHeight);
            gridItemText.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        }

        if (gridItemText != null)
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