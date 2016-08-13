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
import com.frysoft.notifry.data.User;

public class TestFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        setHasOptionsMenu(true);

        Button b = (Button) rootView.findViewById(R.id.b1);
        b.setText("print contacts");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(Contact c : ContactList.getAllContacts()) {
                    System.out.println("# " + c.getEmail());
                }

            }
        });

        b = (Button) rootView.findViewById(R.id.b2);
        b.setText("does nothing");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



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
                        System.out.println("#LOGOUT");
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
                        System.out.println("#LOGIN");
                        User.login("stefan.fragner@rk.at", "1234");
                    }

                })).start();

            }
        });

        return rootView;
    }
}
