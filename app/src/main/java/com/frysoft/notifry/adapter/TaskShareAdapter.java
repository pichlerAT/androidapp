package com.frysoft.notifry.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.Contact;
import com.frysoft.notifry.data.ContactGroup;
import com.frysoft.notifry.data.Share;
import com.frysoft.notifry.utils.App;

/**
 * Created by Edwin Pichler on 13.05.2016.
 */

public class TaskShareAdapter extends BaseExpandableListAdapter {

    protected ArrayList<ContactGroup> mSharedGroups;
    protected ArrayList<Contact> mSharedContactsView = new ArrayList<>();
    protected ArrayList<Contact> mSharedContactsEdit = new ArrayList<>();
    protected ArrayList<Contact> mSharedContactsMore = new ArrayList<>();

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
        final Contact contact = getChild(groupPosition, childPosition);
        String childString = contact.getName();

        final int color_permission_view = App.getColorFromID(R.color.colorPermission1);
        final int color_permission_edit = App.getColorFromID(R.color.colorPermission2);
        final int color_permission_more = App.getColorFromID(R.color.colorPermission3);

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_sharelist_item, parent, false);
        }

        if (((Share) contact).hasPermissionView()){
            convertView.setBackgroundColor(color_permission_view);
        }
        else if (((Share) contact).hasPermissionEdit()){
            convertView.setBackgroundColor(color_permission_edit);
        }
        else if (((Share) contact).hasPermissionMore()){
            convertView.setBackgroundColor(color_permission_more);
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
                int view_color = App.getColorFromDrawable(view.getBackground());
                if (view_color == color_permission_view){
                    view.setBackgroundColor(color_permission_edit);
                    mSharedContactsEdit.add(contact);
                    mSharedContactsView.remove(contact);
                }
                else if (view_color == color_permission_edit){
                    view.setBackgroundColor(color_permission_more);
                    mSharedContactsMore.add(contact);
                    mSharedContactsEdit.remove(contact);
                }
                else if (view_color == color_permission_more){
                    view.setBackgroundColor(App.getColorFromID(R.color.colorPrimary));
                    mSharedContactsMore.remove(contact);
                }
                else{
                    view.setBackgroundColor(color_permission_view);
                    mSharedContactsView.add(contact);
                    if (mSharedContactsMore.contains(contact))
                        mSharedContactsMore.remove(contact);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public ArrayList<Contact> getSharedContactsView(){
        return this.mSharedContactsView;
    }

    public ArrayList<Contact> getSharedContactsEdit(){
        return this.mSharedContactsEdit;
    }

        public ArrayList<Contact> getSharedContactsMore(){
        return this.mSharedContactsMore;
    }



}
