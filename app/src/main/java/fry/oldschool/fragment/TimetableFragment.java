package fry.oldschool.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.GridView;

import java.lang.reflect.Field;
import java.util.Calendar;

import fry.oldschool.R;
import fry.oldschool.activity.MainActivity;
import fry.oldschool.adapter.MonthAdapter;
import fry.oldschool.adapter.TimetableEventsAdapter;
import fry.oldschool.data.ContactGroup;
import fry.oldschool.utils.App;

/**
 * Created by Edwin Pichler on 28.04.2016.
 */
public class TimetableFragment extends Fragment {

    protected MonthAdapter mMonthAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
        setHasOptionsMenu(true);

        Calendar calendar = Calendar.getInstance();
        int[] mToday = new int[3];

        mToday[0] = calendar.get(Calendar.DAY_OF_MONTH);
        mToday[1] = calendar.get(Calendar.MONTH); // 0 - 11
        mToday[2] = calendar.get(Calendar.YEAR);

        getActivity().setTitle(App.getMonthName(mToday[1], getResources()));

        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        GridView timetable = (GridView) rootView.findViewById(R.id.gridview_timetable_dates);

        mMonthAdapter = new MonthAdapter(mToday[1], mToday[2], metrics);
        timetable.setAdapter(mMonthAdapter);

        timetable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i<6) // This prevents from clicking on the weekdays (first line in gridview)
                    return;
                View events_view = View.inflate(App.getContext(), R.layout.fragment_timetable_events_daily, null);
                ExpandableListView events_listview = (ExpandableListView) events_view.findViewById(R.id.listview_timetable_events_daily);
                TimetableEventsAdapter events_adapter = new TimetableEventsAdapter();

                events_listview.setAdapter(events_adapter);
                AlertDialog.Builder builder = new AlertDialog.Builder(App.getContext());
                builder.setView(events_listview)

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                return;
            }
        });

        return rootView;
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

            case R.id.action_add:
                FragmentTransaction transaction = MainActivity.fm.beginTransaction();
                transaction.replace(R.id.frame_fragment_main, new TimetableCreateFragment()).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private DatePickerDialog createDialogWithoutDateField() {
        Calendar today = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(App.getContext(), mDateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

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
            mMonthAdapter.setDate(monthOfYear, year);
            getActivity().setTitle(App.getMonthName(monthOfYear, getResources()));
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        int stringId = App.getContext().getApplicationInfo().labelRes;
        getActivity().setTitle(App.getContext().getString(stringId));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.fab.isShown()){
            MainActivity.fab.hide();
        }
    }
}
