package com.frysoft.notifry.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.frysoft.notifry.R;
import com.frysoft.notifry.adapter.TaskShareAdapter;
import com.frysoft.notifry.data.Category;
import com.frysoft.notifry.data.Contact;
import com.frysoft.notifry.data.ContactGroup;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.data.Share;
import com.frysoft.notifry.data.TimetableEntry;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.DateSpan;
import com.frysoft.notifry.utils.InstantAutoComplete;
import com.frysoft.notifry.utils.SpinnerSelectable;
import com.frysoft.notifry.utils.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Edwin Pichler on 26.06.2016.
 */
public class TimetableCreateActivity extends mAppCompatActivity {

    protected TextView date_from_text;
    protected TextView date_to_text;
    protected TextView time_from_text;
    protected TextView time_to_text;
    protected String time_type;
    protected SwitchCompat whole_day;
    protected InstantAutoComplete mCategory;
    protected SpinnerSelectable mRepeat;
    protected Date mRepeatUntil;
    protected TextView mRepeatUntilText;

    protected boolean mRepeatMonday = false;
    protected boolean mRepeatTuesday = false;
    protected boolean mRepeatWednesday = false;
    protected boolean mRepeatThursday = false;
    protected boolean mRepeatFriday = false;
    protected boolean mRepeatSaturday = false;
    protected boolean mRepeatSunday = false;
    protected AppCompatEditText title;
    protected EditText description;
    protected Time time_start;
    protected Time time_end;
    protected Date date_start;
    protected Date date_end;
    protected int color;
    protected Date mToday;

    protected ArrayList<Contact> mSharedContactsView = new ArrayList<>();
    protected ArrayList<Contact> mSharedContactsEdit = new ArrayList<>();
    protected ArrayList<Contact> mSharedContactsMore = new ArrayList<>();
    protected short mAdditions = 0;
    protected ArrayList<String> mAdditionsTemp = new ArrayList<>();

    protected TimetableEntry passed_entry = null;

    List<String> test_tags;

    protected Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_create);
        App.setContext(this);

        mToday = Date.getToday();
        test_tags = Arrays.asList("Work", "Work", "Training", "Party");
        //Initialize toolbar and set the back button within it
        Toolbar toolbar = (Toolbar) findViewById(R.id.timetable_create_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ArrayList<Category> category_list = Data.Categories.getList();
        final ArrayList<String> category_names = new ArrayList<>();
        for (Category category : category_list){
            category_names.add(category.getName());
        }

        //Set category adapter
        ArrayAdapter<String> category_adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, category_names);
        mCategory = (InstantAutoComplete) findViewById(R.id.autotext_timetable_create_categories);
        //Set the number of characters the user must type before the drop down list is shown
        mCategory.setAdapter(category_adapter);
        mCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String category_text = mCategory.getText().toString().toLowerCase();
                    if (!category_text.isEmpty() && !App.containsCaseInsensitive(category_text, category_names)) {
                        mCategory.setText("");
                        Snackbar.make(v, getResources().getString(R.string.wrong_category), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        });

        //Set tag adapter
        ArrayAdapter<String> test_adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, test_tags);
        final InstantAutoComplete tag = (InstantAutoComplete) findViewById(R.id.autotext_timetable_create_tag);
        //Set the number of characters the user must type before the drop down list is shown
        tag.setAdapter(test_adapter);
        tag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String tag_text = tag.getText().toString().toLowerCase();
                    if (!tag_text.isEmpty() && !App.containsCaseInsensitive(tag_text, test_tags)) {
                        tag.setText("");
                        Snackbar.make(v, getResources().getString(R.string.wrong_tag), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        });

        title = (AppCompatEditText) findViewById(R.id.edittext_timetable_create_title);
        description = (EditText) findViewById(R.id.edittext_timetable_create_description);
        whole_day = (SwitchCompat) findViewById(R.id.switch_timetable_create_whole_day);
        date_from_text = (TextView) findViewById(R.id.textview_timetable_create_date_from);
        date_to_text = (TextView) findViewById(R.id.textview_timetable_create_date_to);
        time_from_text = (TextView) findViewById(R.id.textview_timetable_create_time_from);
        time_to_text = (TextView) findViewById(R.id.textview_timetable_create_time_to);
        mRepeat = (SpinnerSelectable) findViewById(R.id.spinner_timetable_create_repeat);
        final TextView participants = (TextView) findViewById(R.id.textview_timetable_create_participants);

        Bundle data = getIntent().getExtras();
        int entry_id = 0;
        int passed_day = 0;
        int passed_month = 0;
        int passed_year = 0;

        if ( data != null ) {
            entry_id = data.getInt("entry");
            passed_day = data.getInt("day");
            passed_month = data.getInt("month");
            passed_year = data.getInt("year");
        }
/*
        if (entry_id != 0) {
            passed_entry = Data.Timetable.Entries.getById(entry_id);

            date_start = passed_entry.getDateStart();
            date_end = passed_entry.getDateEnd();
            time_start = passed_entry.getTimeStart();
            time_end = passed_entry.getTimeEnd();

            date_from_text.setText(date_start.getString());
            date_to_text.setText(date_end.getString());
            time_from_text.setText(time_start.getString());
            time_to_text.setText(time_end.getString());

            title.setText(passed_entry.getTitle());
            description.setText(passed_entry.getDescription());

            RRule rrule = passed_entry.getRRule();
            is_daily = rrule.isFrequencyDaily();
            is_weekly = rrule.isFrequencyWeekly();
            is_monthly = rrule.isFrequencyMonthly();
            is_yearly = rrule.isFrequencyYearly();
            count = rrule.getCount();
            until = rrule.getUntil();
            interval = rrule.getInterval();
            is_whole_day = rrule.isWholeDay();

            if (is_daily){

            }
            else if (is_weekly){

            }
            else if (is_monthly){

            }
            else if (is_yearly){

            }

        }
*/
        else{
            Date date = null;
            if (passed_day != 0)
                date = new Date(passed_day, passed_month, passed_year);

            else
                date = Date.getToday();
            Time time = Time.getCurrentTime(2); //2 means it's rounded half an hour

            //Set start
            String date_text_start = date.getString();
            final String time_text_start = time.getString();
            date_start = date.copy();
            time_start = time.copy();

            //Set end
            int days_to_add = time.addMinutes(60);
            date.addDays(days_to_add);
            String date_text_end = date.getString();
            String time_text_end = time.getString();
            date_end = date;
            time_end = time;

            date_from_text.setText(date_text_start);
            date_to_text.setText(date_text_end);
            time_from_text.setText(time_text_start);
            time_to_text.setText(time_text_end);
        }

        date_from_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareDateDialog("date_from", date_start);
            }
        });
        date_to_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareDateDialog("date_to", date_end);
            }
        });
        time_from_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_type = "time_from";
                prepareTimeDialog(time_start);
            }
        });
        time_to_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_type = "time_to";
                prepareTimeDialog(time_end);
            }
        });

        ArrayAdapter<CharSequence> repeat_adapter = ArrayAdapter.createFromResource(this, R.array.repeat_array, R.layout.spinner_layout);
        repeat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRepeat.setAdapter(repeat_adapter);

        mRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 5){ //if last position (Specific)
                    final View repeat_dialog_header = View.inflate(App.getContext(), R.layout.repeat_dialog_header, null);
                    AppCompatSpinner repeat_spinner = (AppCompatSpinner) repeat_dialog_header.findViewById(R.id.spinner_repeat_dialog_header);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(App.getContext(),
                            R.array.repeat_array_specific, R.layout.spinner_layout);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    repeat_spinner.setAdapter(adapter);

                    repeat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            LinearLayout header_layout = (LinearLayout) repeat_dialog_header.findViewById(R.id.linearlayout_repeat_dialog_header);
                            if (position == 1 || position == 2){
                                View repeat_dialog_specific = View.inflate(mContext, R.layout.repeat_dialog_specific, null);
                                header_layout.addView(repeat_dialog_specific);
                                TextView repeat_monday = (TextView) repeat_dialog_specific.findViewById(R.id.repeat_monday);
                                repeat_monday.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!mRepeatMonday) {
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_selected_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextPrimary));
                                            mRepeatMonday = true;
                                        }
                                        else{
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextDark));
                                            mRepeatMonday = false;
                                        }
                                    }
                                });

                                TextView repeat_tuesday = (TextView) repeat_dialog_specific.findViewById(R.id.repeat_tuesday);
                                repeat_tuesday.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!mRepeatTuesday) {
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_selected_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextPrimary));
                                            mRepeatTuesday = true;
                                        }
                                        else{
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextDark));
                                            mRepeatTuesday = false;
                                        }
                                    }
                                });

                                TextView repeat_wednesday = (TextView) repeat_dialog_specific.findViewById(R.id.repeat_wednesday);
                                repeat_wednesday.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!mRepeatWednesday) {
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_selected_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextPrimary));
                                            mRepeatWednesday = true;
                                        }
                                        else{
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextDark));
                                            mRepeatWednesday = false;
                                        }
                                    }
                                });

                                TextView repeat_thursday = (TextView) repeat_dialog_specific.findViewById(R.id.repeat_thursday);
                                repeat_thursday.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!mRepeatThursday) {
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_selected_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextPrimary));
                                            mRepeatThursday = true;
                                        }
                                        else{
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextDark));
                                            mRepeatThursday = false;
                                        }
                                    }
                                });

                                TextView repeat_friday = (TextView) repeat_dialog_specific.findViewById(R.id.repeat_friday);
                                repeat_friday.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!mRepeatFriday) {
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_selected_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextPrimary));
                                            mRepeatFriday = true;
                                        }
                                        else{
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextDark));
                                            mRepeatFriday = false;
                                        }
                                    }
                                });

                                TextView repeat_saturday = (TextView) repeat_dialog_specific.findViewById(R.id.repeat_saturday);
                                repeat_saturday.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!mRepeatSaturday) {
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_selected_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextPrimary));
                                            mRepeatSaturday = true;
                                        }
                                        else{
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextDark));
                                            mRepeatSaturday = false;
                                        }
                                    }
                                });

                                TextView repeat_sunday = (TextView) repeat_dialog_specific.findViewById(R.id.repeat_sunday);
                                repeat_sunday.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!mRepeatSunday) {
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_selected_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextPrimary));
                                            mRepeatSunday = true;
                                        }
                                        else{
                                            view.setBackground(App.getDrawableFromID(R.drawable.weekday_style));
                                            ((TextView)view).setTextColor(App.getColorFromID(R.color.colorTextDark));
                                            mRepeatSunday = false;
                                        }
                                    }
                                });
                                
                                

                            }
                            else{
                                if(header_layout.getChildCount() > 2)
                                    header_layout.removeViewAt(2);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    mRepeatUntilText = (TextView) repeat_dialog_header.findViewById(R.id.textview_repeat_dialog_date);
                    mRepeatUntilText.setText(mToday.getString());
                    mRepeatUntilText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatePickerDialog dialog = new DatePickerDialog(App.getContext(), mDateSetListener, mToday.day, mToday.month-1, mToday.year);// -1 Because date picker returns month from 0-11
                            dialog.show();
                            dialog.getDatePicker().setTag("date_until");
                        }

                    });

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setView(repeat_dialog_header)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (String addition : mAdditionsTemp){
                                        mAdditions |= Short.parseShort(addition);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        whole_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    time_from_text.setEnabled(false);
                    time_from_text.setAlpha(0);
                    time_to_text.setEnabled(false);
                    time_to_text.setAlpha(0);
                }
                else{
                    time_from_text.setEnabled(true);
                    time_from_text.setAlpha(1);
                    time_to_text.setEnabled(true);
                    time_to_text.setAlpha(1);
                }
            }
        });

        if (participants != null) {
            participants.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View taskSharelist = View.inflate(mContext, R.layout.sharelist, null);
                    ExpandableListView lv = (ExpandableListView) taskSharelist.findViewById(R.id.sharelist);

                    ArrayList<ContactGroup> shared_groups = Data.Timetable.Shares.getList();
                    final TaskShareAdapter adapter = new TaskShareAdapter(shared_groups);
                    lv.setAdapter(adapter);

                    for (int i=0; i<adapter.getGroupCount(); i++){
                        lv.expandGroup(i);
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setView(lv)
                            .setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    participants.setText("");
                                    for (Contact contact : adapter.getSharedContactsView()){
                                        ((Share) contact).setPermission((byte) 1);
                                        participants.append(contact.getName() + ": " + mContext.getResources().getString(R.string.permission_read) + "\n");
                                        mSharedContactsView.add(contact);
                                    }
                                    for (Contact contact : adapter.getSharedContactsEdit()){
                                        ((Share) contact).setPermission((byte) 2);
                                        participants.append(contact.getName() + ": " + mContext.getResources().getString(R.string.permission_write) + "\n");
                                        mSharedContactsEdit.add(contact);
                                    }
                                    for (Contact contact : adapter.getSharedContactsMore()){
                                        ((Share) contact).setPermission((byte) 3);
                                        participants.append(contact.getName() + ": " + mContext.getResources().getString(R.string.permission_more) + "\n");
                                        mSharedContactsMore.add(contact);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

    }

    private void prepareTimeDialog(Time time) {
        int hours = time.getHours();
        int minutes = time.getMinutes();

        TimePickerDialog dialog = new TimePickerDialog(this, mTimeSetListener, hours, minutes, true); //true for 24 hours view
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

    private void prepareDateDialog(String type, Date date){
        DatePickerDialog dialog =
                new DatePickerDialog(this, mDateSetListener, date.year , date.month-1 , date.day);

        DatePicker picker = dialog.getDatePicker();
        picker.setCalendarViewShown(true);
        picker.setSpinnersShown(false);
        picker.setTag(type);
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear+=1;
            String date_text = new Date(dayOfMonth, monthOfYear, year).getString();
            if (view.getTag().equals("date_from")) {
                date_from_text.setText(date_text);
                date_start = new Date(dayOfMonth, monthOfYear, year);//+1 Because the picker returns month from 0-11
            }
            else if (view.getTag().equals("date_to")) {
                date_to_text.setText(date_text);
                date_end = new Date(dayOfMonth, monthOfYear, year);//+1 Because the picker returns month from 0-11
            }

            else if (view.getTag().equals("date_until")){
                mRepeatUntilText.setText(date_text);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timetable_create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add_tag:
                Intent intent = new Intent(this, TimetableTagActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_add_category:
                Intent intent_category = new Intent(this, TimetableCategoryActivity.class);
                startActivity(intent_category);
                return true;

            case R.id.action_done:
                //byte addition, String title, String description, DateSpan span, TimetableCategory category
                DateSpan span;
                String title_string = title.getText().toString();
                String description_string = description.getText().toString();
                if (whole_day.isChecked()){
                    time_start = new Time(23, 59);
                    time_end = new Time(23, 59);
                }

                String category_name = mCategory.getText().toString();
                Category category = null;
                if (category_name != "")
                    category = Data.getCategoryByName(category_name);
                if (passed_entry != null){
                    passed_entry.set(category, title_string, description_string, date_start, date_end, time_start, time_end, null, 0); // TODO: add color, repeat_until and interval
                }
                else {
                    TimetableEntry entry = Data.create.TimetableEntry(category, title_string, description_string, date_start, date_end, time_start, time_end, 0, null);
                }
                /*
                if (mSharedContactsView.size() > 0)
                    entry.Shares.addShare(mSharedContactsView, (byte) 1);
                if (mSharedContactsEdit.size() > 0)
                    entry.sharedContacts.addShare(mSharedContactsEdit, (byte) 2);
                if (mSharedContactsMore.size() > 0)
                    entry.sharedContacts.addShare(mSharedContactsMore, (byte) 3);*/

                TextView notification_view = (TextView) findViewById(R.id.textview_timetable_create_notification);
                if (!notification_view.getText().toString().equals(getResources().getString(R.string.none))){
                    NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_notification_dark)
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText(title.getText().toString());

                    Intent resultIntent = new Intent(this, MainActivity.class);
                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    this,
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    notification_builder.setContentIntent(resultPendingIntent);

                    int mNotificationId = 001;
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(mNotificationId, notification_builder.build());
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }
}