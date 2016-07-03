package fry.oldschool.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fry.oldschool.R;
import fry.oldschool.data.Contact;
import fry.oldschool.data.ContactGroup;
import fry.oldschool.data.ContactList;
import fry.oldschool.utils.App;

/**
 * Created by Edwin Pichler on 13.05.2016.
 */

public class ContactAdapter extends BaseExpandableListAdapter {

    protected Context mContext;
    protected ArrayList<ContactGroup> contactGroupList;
    protected boolean isTask;

    public ContactAdapter(Context context, ArrayList<ContactGroup> contactGroupList, boolean isTask){
        this.mContext = context;
        this.contactGroupList = new ArrayList<>(contactGroupList);
        this.isTask = isTask;
    }

    @Override
    public int getGroupCount() {
        return contactGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return contactGroupList.get(groupPosition).getNoContacts();
    }

    @Override
    public ContactGroup getGroup(int groupPosition) {
        return contactGroupList.get(groupPosition);
    }

    @Override
    public Contact getChild(int groupPosition, int childPosition) {
        return contactGroupList.get(groupPosition).getContact(childPosition);
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
        Button deleteGroup = (Button) convertView.findViewById(R.id.button_contact_group_delete);
        if (isTask){
            ((LinearLayout) convertView.findViewById(R.id.linearlayout_contact_list_header)).removeView(deleteGroup);
        }
        else {
            deleteGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactList.getGroup(groupPosition).delete();
                    contactGroupList = new ArrayList<>(ContactList.getGroups());
                    notifyDataSetChanged();
                }
            });

            int lastPosition = getGroupCount() - 1;
            //Has to be if / else, otherwise it doesn't work
            if (lastPosition != groupPosition) {
                deleteGroup.setAlpha(1);
                deleteGroup.setEnabled(true);
            } else {
                deleteGroup.setAlpha(0);
                deleteGroup.setEnabled(false);
            }
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


    public ArrayList<ContactGroup> getGroupList(){
        return contactGroupList;
    }

    public void addGroup(String groupName){
        //Add group
        ContactList.createContactGroup(groupName);
        contactGroupList = new ArrayList<>(ContactList.getGroups());
        notifyDataSetChanged();
    }

    public void assignChildToGroups(List<String> groupList, int childIndex){

    }

    public void removeChilds(ArrayList<Contact> contacts){
        for (Contact contact : contacts){
            ContactList.deleteContact(contact);
            contactGroupList = new ArrayList<ContactGroup>(ContactList.getGroups());
        }
        notifyDataSetChanged();
    }

    public void filterContacts(String search){
        /*
        if(!search.isEmpty()) {
            search = search.toLowerCase();

            for (int i=0; i<ContactList.getNoGroups(); i++){
                ArrayList<Contact> newContacts = new ArrayList<Contact>();
                for (Contact contact : ContactList.groups.get(i).contacts) {
                    if (contact.name.toLowerCase().contains(search) || contact.email.toLowerCase().contains(search)) {
                        newContacts.add(contact);
                    }
                }

                if (newContacts.size() > 0)
                    contactGroupList.get(i).contacts = newContacts;
                else{
                    contactGroupList.get(i).contacts = new ArrayList<>();
                }

            }
        }
        else{
            contactGroupList = new ArrayList<>(ContactList.groups);
        }
        notifyDataSetChanged();
        */
    }
}
