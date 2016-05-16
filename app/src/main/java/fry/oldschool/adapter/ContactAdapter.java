package fry.oldschool.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fry.oldschool.R;

/**
 * Created by Edwin Pichler on 13.05.2016.
 */

public class ContactAdapter extends BaseExpandableListAdapter {

    protected Context mContext;
    protected List<String> mHeaderList;
    protected HashMap<String, List<String>> mContactList;
    protected HashMap<String, List<String>> mContactOriginalList;

    public ContactAdapter(Context context, List<String> headerList, HashMap<String, List<String>> contactList){
        this.mContactOriginalList = new HashMap<String, List<String>>(contactList);
        this.mContext = context;
        this.mHeaderList = headerList;
        this.mContactList = contactList;
    }

    @Override
    public int getGroupCount() {
        return mHeaderList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mContactList.get(mHeaderList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mHeaderList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mContactList.get(mHeaderList.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerString = (String) getGroup(groupPosition);

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_list_header, parent, false);
        }

        TextView headerName = (TextView) convertView.findViewById(R.id.textview_contact_header);
        headerName.setTypeface(null, Typeface.BOLD);
        headerName.setText(headerString);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childString = (String) getChild(groupPosition, childPosition);

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_list_item, parent, false);
        }

        TextView contactName = (TextView) convertView.findViewById(R.id.textview_contact_name);
        TextView firstLetter = (TextView) convertView.findViewById(R.id.textview_contact_icon);
        contactName.setText(childString);
        firstLetter.setText(String.valueOf(childString.charAt(0)));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public List<String> getGroupList(){
        return mHeaderList;
    }

    public void addGroup(String groupName){
        mHeaderList.add(0, groupName);
        mContactOriginalList.put(mHeaderList.get(0), new ArrayList<String>());
        mContactList.put(mHeaderList.get(0), new ArrayList<String>());
        notifyDataSetChanged();
        //Add group to database
    }

    public void assignChildToGroups(List<String> groupList, int childIndex){

    }

    public void filterContacts(String search){
        if(!search.isEmpty()) {
            search = search.toLowerCase();
            for (Map.Entry<String, List<String>> entry : mContactOriginalList.entrySet()) {
                List<String> newContacts = new ArrayList<String>();
                for (String contact : entry.getValue()) {
                    if (contact.toLowerCase().contains(search)) {
                        newContacts.add(contact);
                    }
                }
                if (newContacts.size() > 0)
                    mContactList.put(entry.getKey(), newContacts);

                else
                    mContactList.put(entry.getKey(), new ArrayList<String>());
            }
        }
        else{
            mContactList = new HashMap<>(mContactOriginalList);
        }
        notifyDataSetChanged();
    }
}
