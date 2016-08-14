package com.frysoft.notifry.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.Contact;
import com.frysoft.notifry.data.ContactList;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.data.Event;
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
                    System.out.println(" # "+e.getDate().getWeekdayName()+" the "+e.getDate().getString());
                }

            }
        });

        b = (Button) rootView.findViewById(R.id.b2);
        b.setText("create entry");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Data.create.TimetableEntry(null, "MO-WE-SA", null, new Date(14,8,2016), new Date(14,8,2016),
                        new Time(Time.MIN_TIME), new Time((short)(Time.MAX_TIME - 1)), 0, (short)5, (short)2,
                        TimetableEntry.REPEAT_MONTHLY);

            }
        });

        b = (Button) rootView.findViewById(R.id.b3);
        b.setText("logout");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new Thread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("# LOGOUT");
                        User.logout();
                    }

                })).start();

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
