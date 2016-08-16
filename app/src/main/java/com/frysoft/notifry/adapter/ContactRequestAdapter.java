package com.frysoft.notifry.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.Contact;

import java.util.ArrayList;

/**
 * Created by Edwin Pichler on 21.05.2016.
 */
public class ContactRequestAdapter extends ArrayAdapter<Contact>{
    ArrayList<Contact> contactRequests;

    public ContactRequestAdapter(Context context, int resource, ArrayList<Contact> contactRequests) {
        super(context, resource, contactRequests);
        this.contactRequests = contactRequests;
    }

    @Override
    public Contact getItem(int position){
        return contactRequests.get(position);
    }

    @Override
    public int getCount(){
        return contactRequests.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View res;
        if (convertView == null){
            res = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_request_item, parent, false);
        }
        else{
            res = convertView;
        }

        final Contact sender = getItem(position);

        TextView senderIcon = (TextView) res.findViewById(R.id.textview_contact_request_icon);
        TextView senderName = (TextView) res.findViewById(R.id.textview_contact_request_name);
        TextView senderEmail = (TextView) res.findViewById(R.id.textview_contact_request_email);

        senderName.setText(sender.getName());
        senderEmail.setText(sender.getEmail());
        senderIcon.setText(String.valueOf(sender.getName().charAt(0)).toUpperCase());

        AppCompatImageButton accept = (AppCompatImageButton) res.findViewById(R.id.imagebutton_contact_request_accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sender.accept();
            }
        });

        AppCompatImageButton decline = (AppCompatImageButton) res.findViewById(R.id.imagebutton_contact_request_decline);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sender.decline();
            }
        });

        return res;
    }
}
