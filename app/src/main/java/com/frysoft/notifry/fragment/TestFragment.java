package com.frysoft.notifry.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.Category;
import com.frysoft.notifry.data.Contact;
import com.frysoft.notifry.data.ContactGroup;
import com.frysoft.notifry.data.ContactList;
import com.frysoft.notifry.data.ContactRequest;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.data.Event;
import com.frysoft.notifry.data.RRule;
import com.frysoft.notifry.data.Share;
import com.frysoft.notifry.data.Tag;
import com.frysoft.notifry.data.Tasklist;
import com.frysoft.notifry.data.TasklistEntry;
import com.frysoft.notifry.data.TimetableEntry;
import com.frysoft.notifry.data.User;
import com.frysoft.notifry.utils.Date;

import java.util.ArrayList;

public class TestFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        setHasOptionsMenu(true);

        Button b = (Button) rootView.findViewById(R.id.b1);
        b.setText("delete contact");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContactList.getAllContacts().get(0).delete();

            }
        });

        b = (Button) rootView.findViewById(R.id.b2);
        b.setText("nixxi");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        b = (Button) rootView.findViewById(R.id.b3);
        b.setText("nixxi");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        b = (Button) rootView.findViewById(R.id.b4);
        b.setText("nixxi");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        b = (Button) rootView.findViewById(R.id.b5);
        b.setText("nixxi");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        b = (Button) rootView.findViewById(R.id.b6);
        b.setText("nixxi");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        b = (Button) rootView.findViewById(R.id.b7);
        b.setText("nixxi");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        b = (Button) rootView.findViewById(R.id.b8);
        b.setText("nixxi");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        b = (Button) rootView.findViewById(R.id.b9);
        b.setText("nixxi");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        b = (Button) rootView.findViewById(R.id.b10);
        b.setText("print data");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("# PRINT DATA");
                System.out.println("user_id = "+User.getId());
                System.out.println("calendar entries: "+Data.Timetable.Entries.size());
                for(TimetableEntry ent : Data.Timetable.Entries.getList()) {
                    System.out.println("-"+ent.getTitle());
                    System.out.println(" shares: "+ent.shares.shares.size());
                    for(Share s : ent.shares.shares) {
                        System.out.println(" -"+s.getEmail());
                    }
                }
                System.out.println("calendar shares: "+Data.Timetable.shares.shares.size());
                for(Share s : Data.Timetable.shares.shares) {
                    System.out.println("-"+s.getEmail());
                }
                System.out.println("categories: "+Data.Categories.size());
                for(Category cat : Data.Categories.getList()) {
                    System.out.println("-"+cat.getName());
                    System.out.println(" shares: "+cat.shares.shares.size());
                    for(Share s : cat.shares.shares) {
                        System.out.println(" -"+s.getEmail());
                    }
                }
                System.out.println("contact groups: "+ContactList.getNoGroups());
                for(ContactGroup grp : ContactList.getAllGroups()) {
                    System.out.println("-"+grp.getName());
                    System.out.println(" contacts: "+grp.getNoContacts());
                    for(Contact cont : grp.getContacts()) {
                        System.out.println(" -"+cont.getEmail());
                    }
                }
                System.out.println("contact requests: "+ContactList.getNoRequests());
                for(Contact req : ContactList.getRequests()) {
                    System.out.println("-"+req.getEmail());
                }
                System.out.println("tags: "+Data.Tags.size());
                for(Tag tag : Data.Tags.getList()) {
                    System.out.println("-"+tag.getTitle());
                }
                System.out.println("tasklists: "+Data.Tasklists.size());
                for(Tasklist tl : Data.Tasklists.getList()) {
                    System.out.println("-"+tl.getName());
                    System.out.println(" tasklist entries: "+tl.getNoEntries());
                    for(TasklistEntry tle : tl.getEntries()) {
                        System.out.println(" -"+tle.getDescription());
                    }
                }


            }
        });

        return rootView;
    }
}
