package com.frysoft.notifry.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.Timetable;
import com.frysoft.notifry.data.TimetableEntry;
import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.DateSpan;

/**
 * Created by Edwin Pichler on 24.07.2016.
 */
public class TestFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        setHasOptionsMenu(true);

        Button b = (Button) rootView.findViewById(R.id.b1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateSpan ds = new DateSpan(new Date(25,7,2016), new Date(27,7,2016));
                TimetableEntry ent = TimetableEntry.create("Test-Entry-1", "This is the Description", ds, null, (byte)0);

            }
        });

        b = (Button) rootView.findViewById(R.id.b2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(TimetableEntry ent : Timetable.getEntries()) {
                    System.out.println("::::: id="+ent.id+", date="+ ent.getDateSpan().getDateStart().getString());
                }

            }
        });

        b = (Button) rootView.findViewById(R.id.b3);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("asdf: "+Timetable.getEntries(25,7,2016).size());

            }
        });

        b = (Button) rootView.findViewById(R.id.b4);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("asdf: "+Timetable.getEntries(7,2016).size());

            }
        });

        return rootView;
    }
}
