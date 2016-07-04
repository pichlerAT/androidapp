package fry.oldschool.adapter;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fry.oldschool.R;
import fry.oldschool.data.Contact;
import fry.oldschool.data.ContactGroup;
import fry.oldschool.data.Share;
import fry.oldschool.utils.App;

/**
 * Created by Edwin Pichler on 13.05.2016.
 */

public class TaskShareAdapter extends BaseExpandableListAdapter {

    protected ArrayList<ContactGroup> mSharedGroups;

    public TaskShareAdapter(ArrayList<ContactGroup> shared_groups){
        mSharedGroups = shared_groups;
    }

    @Override
    public int getGroupCount() {
        return mSharedGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mSharedGroups.get(groupPosition).getNoContacts();
    }

    @Override
    public ContactGroup getGroup(int groupPosition) {
        return mSharedGroups.get(groupPosition);
    }

    @Override
    public Contact getChild(int groupPosition, int childPosition) {
        return mSharedGroups.get(groupPosition).getContact(childPosition);
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
        String headerString = getGroup(groupPosition).getName();

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_sharelist_header, parent, false);
        }

        String tag = (String) convertView.getTag();
        if (tag != null){
            TextView permissionHeader = (TextView) convertView.findViewById(R.id.textview_task_sharelist_header_permission);
            permissionHeader.setText(tag);
        }

        TextView headerName = (TextView) convertView.findViewById(R.id.textview_task_sharelist_header);
        headerName.setTypeface(null, Typeface.BOLD);
        headerName.setText(headerString);

        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Contact contact = getChild(groupPosition, childPosition);
        String childString = contact.getName();

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_sharelist_item, parent, false);
        }

        if (((Share) contact).hasPermissionView()){
            convertView.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.colorPermission1));
        }
        else if (((Share) contact).hasPermissionEdit()){
            convertView.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.colorPermission2));
        }
        else if (((Share) contact).hasPermissionMore()){
            convertView.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.colorPermission3));
        }

        TextView contactName = (TextView) convertView.findViewById(R.id.textview_task_sharelist_item_name);
        TextView contactEmail = (TextView) convertView.findViewById(R.id.textview_task_sharelist_item_email);
        TextView firstLetter = (TextView) convertView.findViewById(R.id.textview_task_sharelist_item_icon);
        contactName.setText(childString);
        contactEmail.setText(contact.getEmail());
        firstLetter.setText(String.valueOf(childString.charAt(0)).toUpperCase());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.colorPermission1));
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }



}
