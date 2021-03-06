package com.frysoft.notifry.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.Contact;
import com.frysoft.notifry.data.ContactGroup;
import com.frysoft.notifry.data.ContactList;
import com.frysoft.notifry.utils.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edwin Pichler on 13.05.2016.
 */

public class ContactAdapter extends BaseExpandableListAdapter {

    @Override
    public int getGroupCount() {
        return ContactList.getNoGroups();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ContactList.getGroup(groupPosition).getNoContacts();
    }

    @Override
    public ContactGroup getGroup(int groupPosition) {
        return ContactList.getGroup(groupPosition);
    }

    @Override
    public Contact getChild(int groupPosition, int childPosition) {
        return ContactList.getGroup(groupPosition).getContact(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerString = (String) getGroup(groupPosition).getName();

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_list_header, parent, false);
        }

        TextView headerName = (TextView) convertView.findViewById(R.id.textview_contact_header);
        headerName.setTypeface(null, Typeface.BOLD);
        headerName.setText(headerString);
        AppCompatButton deleteGroup = (AppCompatButton) convertView.findViewById(R.id.button_contact_group_delete);
        deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(App.getContext());
                deleteBuilder.setTitle(R.string.warning)
                    .setMessage(R.string.delete_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ContactList.getGroup(groupPosition).delete();
                            notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            }
        });

        int lastPosition = getGroupCount() - 1;
        //Has to be if / else, otherwise it doesn't work
        if (lastPosition != groupPosition) {
            deleteGroup.setAlpha(.54f);
            deleteGroup.setEnabled(true);
        } else {
            deleteGroup.setAlpha(0);
            deleteGroup.setEnabled(false);
        }


        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Contact contact = getChild(groupPosition, childPosition);
        String childString = contact.getName();

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_list_item, parent, false);
        }

        TextView contactName = (TextView) convertView.findViewById(R.id.textview_contact_name);
        TextView contactEmail = (TextView) convertView.findViewById(R.id.textview_contact_email);
        TextView firstLetter = (TextView) convertView.findViewById(R.id.textview_contact_icon);
        contactName.setText(childString);
        contactEmail.setText(contact.getEmail());
        firstLetter.setText(String.valueOf(childString.charAt(0)).toUpperCase());


        convertView.setTag(groupPosition + ";" + childPosition);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }



    public void addGroup(String groupName){
        //Add group
        ContactList.createContactGroup(groupName);
        notifyDataSetChanged();
    }

    public void assignChildToGroups(List<String> groupList, int childIndex){

    }

    public void removeChilds(ArrayList<Contact> contacts){
        for (Contact contact : contacts){
            ContactList.deleteContact(contact);
        }
        notifyDataSetChanged();
    }

    public void filterContacts(String search){
        ContactList.search(search);
        notifyDataSetChanged();
    }
}
