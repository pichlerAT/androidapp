package fry.oldschool.fragment;

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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import fry.oldschool.R;
import fry.oldschool.utils.App;

/**
 * Created by Edwin Pichler on 26.06.2016.
 */
public class TimetableCreateFragment extends Fragment {

    protected TextView date_from;
    protected TextView date_to;
    protected TextView time_from;
    protected TextView time_to;
    protected String time_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable_create, container, false);
        setHasOptionsMenu(true);

        date_from = (TextView) rootView.findViewById(R.id.textview_timetable_create_date_from);
        date_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareDateDialog("date_from");
            }
        });

        date_to = (TextView) rootView.findViewById(R.id.textview_timetable_create_date_to);
        date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareDateDialog("date_to");
            }
        });

        time_from = (TextView) rootView.findViewById(R.id.textview_timetable_create_time_from);
        time_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_type = "time_from";
                prepareTimeDialog();
            }
        });

        time_to = (TextView) rootView.findViewById(R.id.textview_timetable_create_time_to);
        time_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_type = "time_to";
                prepareTimeDialog();
            }
        });

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
            String time_text = Integer.toString(hours) + ":" + Integer.toString(minutes);
            if (time_type.equals("time_from"))
                time_from.setText(time_text);
            else if (time_type.equals("time_to"))
                time_to.setText(time_text);
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
            String date_text = Integer.toString(dayOfMonth) + "." + Integer.toString(monthOfYear) + "." + Integer.toString(year);
            if (view.getTag().equals("date_from"))
                date_from.setText(date_text);
            else if (view.getTag().equals("date_to"))
                date_to.setText(date_text);
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}