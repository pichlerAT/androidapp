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
import com.frysoft.notifry.data.TimetableEntry;
import com.frysoft.notifry.data.User;
import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.Time;

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

                for(TimetableEntry ent : Data.Timetable.Entries.getList()) {
                    System.out.println("# " + ent.getTitle() + ": " + ent.getDateStart().getString() + " , " + ent.getTimeStart().getString());
                }

            }
        });

        b = (Button) rootView.findViewById(R.id.b2);
        b.setText("create entry");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Data.create.TimetableEntry(null, "testily", null, new Date(14,8,2016), new Date(15,8,2016), new Time(20, 41), new Time(23,49), 0, (short)0, (short)0);

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
                        User.login("edwin.pichler@rk.at", "1234");
                    }

                })).start();

            }
        });

        return rootView;
    }
}
