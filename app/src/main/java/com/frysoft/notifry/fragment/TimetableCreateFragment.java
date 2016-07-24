package com.frysoft.notifry.fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.TimetableEntry;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.DateSpan;
import com.frysoft.notifry.utils.Time;

/**
 * Created by Edwin Pichler on 26.06.2016.
 */
public class TimetableCreateFragment extends Fragment {

    protected TextView date_from_text;
    protected TextView date_to_text;
    protected TextView time_from_text;
    protected TextView time_to_text;
    protected String time_type;

    protected byte repeat;
    protected EditText title;
    protected EditText description;
    protected Time time_start;
    protected Time time_end;
    protected Date date_start;
    protected Date date_end;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable_create, container, false);
        setHasOptionsMenu(true);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 1);
        int hours_start = c.get(Calendar.HOUR);
        int day_start = c.get(Calendar.DAY_OF_MONTH);
        int month_start = c.get(Calendar.MONTH);
        int year_start = c.get(Calendar.YEAR);

        c.add(Calendar.HOUR, 1);
        int hours_end = c.get(Calendar.HOUR);
        int day_end = c.get(Calendar.DAY_OF_MONTH);
        int month_end = c.get(Calendar.MONTH);
        int year_end = c.get(Calendar.YEAR);

        String date_text_start = App.formatDate(day_start, month_start, year_start);
        String date_text_end = App.formatDate(day_end, month_end, year_end);
        String time_text_start = App.formatTime(hours_start, 0);
        String time_text_end = App.formatTime(hours_end, 0);

        date_start = new Date(day_start, month_start, year_start);
        date_end = new Date(day_end, month_end, year_end);
        time_start = new Time(hours_start, 0);
        time_end = new Time(hours_end, 0);

        repeat = 0;

        date_from_text = (TextView) rootView.findViewById(R.id.textview_timetable_create_date_from);
        date_from_text.setText(date_text_start);
        date_from_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareDateDialog("date_from");
            }
        });

        date_to_text = (TextView) rootView.findViewById(R.id.textview_timetable_create_date_to);
        date_to_text.setText(date_text_end);
        date_to_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareDateDialog("date_to");
            }
        });

        time_from_text = (TextView) rootView.findViewById(R.id.textview_timetable_create_time_from);
        time_from_text.setText(time_text_start);
        time_from_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_type = "time_from";
                prepareTimeDialog();
            }
        });

        time_to_text = (TextView) rootView.findViewById(R.id.textview_timetable_create_time_to);
        time_to_text.setText(time_text_end);
        time_to_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_type = "time_to";
                prepareTimeDialog();
            }
        });

        title = (EditText) rootView.findViewById(R.id.edittext_timetable_create_title);
        description = (EditText) rootView.findViewById(R.id.edittext_timetable_create_description);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_timetable_create_repeat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(App.getContext(),
                R.array.repeat_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return rootView;
    }

    private void prepareTimeDialog() {
        Calendar cal = Calendar.getInstance();
        int hours = cal.get(Calendar.HOUR);
        int minutes = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(App.getContext(), mTimeSetListener, hours, minutes, true); //true for 24 hours view
        dialog.show();
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker view, int hours, int minutes) {
            String time_text = App.formatTime(hours, minutes);
            if (time_type.equals("time_from")) {
                time_from_text.setText(time_text);
                time_start = new Time(hours, minutes);
            }
            else if (time_type.equals("time_to")) {
                time_to_text.setText(time_text);
                time_end = new Time(hours, minutes);
            }
        }
    };

    private void prepareDateDialog(String type){
        Calendar cal = Calendar.getInstance();
        int maxYear = cal.get(Calendar.YEAR);
        int maxMonth = cal.get(Calendar.MONTH);
        int maxDay = cal.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dialog =
                new DatePickerDialog(App.getContext(), mDateSetListener, maxYear , maxMonth , maxDay);

        DatePicker picker = dialog.getDatePicker();
        picker.setCalendarViewShown(true);
        picker.setSpinnersShown(false);
        picker.setTag(type);
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date_text = App.formatDate(dayOfMonth, monthOfYear, year);
            if (view.getTag().equals("date_from")) {
                date_from_text.setText(date_text);
                date_start = new Date(dayOfMonth, monthOfYear, year);
            }
            else if (view.getTag().equals("date_to")) {
                date_to_text.setText(date_text);
                date_end = new Date(dayOfMonth, monthOfYear, year);
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.timetable_create_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_add:
                return true;
            case R.id.action_done:
                //byte addition, String title, String description, DateSpan span, TimetableCategory category
                DateSpan span = new DateSpan(date_start, time_start, date_end, time_end);
                TimetableEntry.create(repeat, title.getText().toString(), description.getText().toString(), span, null);
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}