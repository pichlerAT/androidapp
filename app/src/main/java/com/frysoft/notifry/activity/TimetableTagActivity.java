package com.frysoft.notifry.activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.frysoft.notifry.R;
import com.frysoft.notifry.adapter.TaskShareAdapter;
import com.frysoft.notifry.data.Contact;
import com.frysoft.notifry.data.ContactGroup;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Time;

import java.util.ArrayList;

/**
 * Created by Edwin Pichler on 26.06.2016.
 */
public class TimetableTagActivity extends mAppCompatActivity {

    protected TextView time_from_text;
    protected TextView time_to_text;
    protected String time_type;
    protected Switch whole_day;

    protected byte repeat;
    protected EditText title;
    protected EditText description;
    protected Time time_start;
    protected Time time_end;

    protected ArrayList<Contact> mSharedContactsView = new ArrayList<>();
    protected ArrayList<Contact> mSharedContactsEdit = new ArrayList<>();
    protected ArrayList<Contact> mSharedContactsMore = new ArrayList<>();

    protected Context mContext = this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_tag);

        //Initialize toolbar and set the back button within it
        Toolbar toolbar = (Toolbar) findViewById(R.id.timetable_tag_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Time time = Time.getCurrentTime(2); //2 means it's rounded half an hour

        //Set start
        String time_text_start = time.getString();
        time_start = time.copy();

        //Set end
        time.addMinutes(60);
        String time_text_end = time.getString();
        time_end = time;

        repeat = 0;

        time_from_text = (TextView) findViewById(R.id.textview_timetable_tag_time_from);
        time_from_text.setText(time_text_start);
        time_from_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_type = "time_from";
                prepareTimeDialog(time_start);
            }
        });

        time_to_text = (TextView) findViewById(R.id.textview_timetable_tag_time_to);
        time_to_text.setText(time_text_end);
        time_to_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_type = "time_to";
                prepareTimeDialog(time_end);
            }
        });

        title = (EditText) findViewById(R.id.edittext_timetable_tag_title);
        description = (EditText) findViewById(R.id.edittext_timetable_tag_description);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_timetable_tag_repeat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.repeat_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        whole_day = (Switch) findViewById(R.id.switch_timetable_tag_whole_day);
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

        final TextView participants = (TextView) findViewById(R.id.textview_timetable_tag_participants);
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
                                        //((Share) contact).setPermission((byte) 1);
                                        participants.append(contact.getName() + ": " + mContext.getResources().getString(R.string.permission_read) + "\n");
                                        mSharedContactsView.add(contact);
                                    }
                                    for (Contact contact : adapter.getSharedContactsEdit()){
                                        //((Share) contact).setPermission((byte) 2);
                                        participants.append(contact.getName() + ": " + mContext.getResources().getString(R.string.permission_write) + "\n");
                                        mSharedContactsEdit.add(contact);
                                    }
                                    for (Contact contact : adapter.getSharedContactsMore()){
                                        //((Share) contact).setPermission((byte) 3);
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

        TimePickerDialog dialog = new TimePickerDialog(mContext, mTimeSetListener, hours, minutes, true); //true for 24 hours view
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timetable_tag_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}