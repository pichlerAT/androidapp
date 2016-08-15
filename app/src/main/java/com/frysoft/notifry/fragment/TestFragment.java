package com.frysoft.notifry.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.ConnectionManager;
import com.frysoft.notifry.data.Contact;
import com.frysoft.notifry.data.ContactList;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.data.Event;
import com.frysoft.notifry.data.RRule;
import com.frysoft.notifry.data.TimetableEntry;
import com.frysoft.notifry.data.User;
import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.Time;

import java.util.ArrayList;

public class TestFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        setHasOptionsMenu(true);

        Button b = (Button) rootView.findViewById(R.id.b1);
        b.setText("print timetableentry");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("# ----- Entries -----");
                System.out.println("# Number of Entries: "+Data.Timetable.Entries.size());
                for(TimetableEntry ent : Data.Timetable.Entries.getList()) {
                    System.out.println("# " + ent.getTitle() + ": " + ent.getDateStart().getString() + " , " + ent.getTimeStart().getString());
                }
                System.out.println("# ----- Events -----");
                ArrayList<Event> events = Data.Timetable.getEvents(new Date(10,8,2016), new Date(10,12,2017));
                System.out.println("# Number of Events: "+events.size());
                for(Event e : events) {
                    System.out.println(" # "+e.getDate().getString()+" is a "+e.getDate().getWeekdayName());
                }

            }
        });

        b = (Button) rootView.findViewById(R.id.b2);
        b.setText("sync android calendar");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Data.Timetable.synchronizeAndroidCalendarIntoNotifry();

            }
        });

        b = (Button) rootView.findViewById(R.id.b3);
        b.setText("create entry");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("# CREATE ENTRY");

                RRule rRule = new RRule();
                rRule.setWholeDay(true);
                rRule.setFrequencyDaily(true);
                rRule.setUntil(new Date(31,12,2099));
                rRule.setByDay(4, new int[]{0});
                rRule.setByMonthDay(new int[]{13});

                Data.create.TimetableEntry(null, "Fr. 13th", "Oooh myy goood.\nIt is Friday the thirteenth", new Date(1,1,2000), null, null, null, 0, rRule);

            }
        });

        b = (Button) rootView.findViewById(R.id.b4);
        b.setText("login");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new Thread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("# LOGIN");
                        int res = User.login("david.nguyen@rk.at", "1234");
                    }

                })).start();

            }
        });

        b = (Button) rootView.findViewById(R.id.b5);
        b.setText("print userdata");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("# User.email = " + User.getEmail());

            }
        });

        return rootView;
    }
}
