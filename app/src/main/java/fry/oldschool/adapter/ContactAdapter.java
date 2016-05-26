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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fry.oldschool.R;
import fry.oldschool.utils.App;
import fry.oldschool.utils.Contact;
import fry.oldschool.utils.ContactGroup;

/**
 * Created by Edwin Pichler on 13.05.2016.
 */

public class ContactAdapter extends BaseExpandableListAdapter {

    protected Context mContext;
    protected ArrayList<ContactGroup> contactGroupList;

    public ContactAdapter(Context context, ArrayList<ContactGroup> contactGroupList){
        this.mContext = context;
        this.contactGroupList = contactGroupList;
    }

    @Override
    public int getGroupCount() {
        return contactGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return contactGroupList.get(groupPosition).contacts.size();
    }

    @Override
    public ContactGroup getGroup(int groupPosition) {
        return contactGroupList.get(groupPosition);
    }

    @Override
    public Contact getChild(int groupPosition, int childPosition) {
        return contactGroupList.get(groupPosition).contacts.get(childPosition);
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
        String headerString = (String) getGroup(groupPosition).name;

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_list_header, parent, false);
        }

        TextView headerName = (TextView) convertView.findViewById(R.id.textview_contact_header);
        headerName.setTypeface(null, Typeface.BOLD);
        headerName.setText(headerString);

        Button deleteGroup = (Button) convertView.findViewById(R.id.button_contact_group_delete);
        deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGroup(groupPosition).delete();
            }
        });

        int lastPosition = contactGroupList.size()-1;
        //Has to be if / else, otherwise it doesn't work
        if (lastPosition != groupPosition) {
            deleteGroup.setAlpha(1);
            deleteGroup.setEnabled(true);
        }
        else{
            deleteGroup.setAlpha(0);
            deleteGroup.setEnabled(false);
        }

        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Contact contact = getChild(groupPosition, childPosition);
        String childString = contact.name;

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_list_item, parent, false);
        }

        TextView contactName = (TextView) convertView.findViewById(R.id.textview_contact_name);
        TextView contactEmail = (TextView) convertView.findViewById(R.id.textview_contact_email);
        TextView firstLetter = (TextView) convertView.findViewById(R.id.textview_contact_icon);
        contactName.setText(childString);
        contactEmail.setText(contact.email);
        firstLetter.setText(String.valueOf(childString.charAt(0)).toUpperCase());

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
        notifyDataSetChanged();
    }

    public void assignChildToGroups(List<String> groupList, int childIndex){

    }

    public void removeChilds(ArrayList<Contact> contacts){
        for (Contact contact : contacts){
            App.conLis.deleteContact(contact);
            contactGroupList = new ArrayList<ContactGroup>(App.conLis.groups);
        }
        notifyDataSetChanged();
    }

    public void filterContacts(String search){
        if(!search.isEmpty()) {
            search = search.toLowerCase();
            for (ContactGroup group : contactGroupList) {
                ArrayList<Contact> newContacts = new ArrayList<Contact>();
                for (Contact contact : group.contacts) {
                    if (contact.name.toLowerCase().contains(search) || contact.email.toLowerCase().contains(search)) {
                        newContacts.add(contact);
                    }
                }
                if (newContacts.size() <= 0)
                    group.setContacts(newContacts);
                else
                    group.setContacts(new ArrayList<Contact>());
            }
        }
        else{
            contactGroupList = new ArrayList<ContactGroup>(App.conLis.groups);
        }
        notifyDataSetChanged();
    }
}
