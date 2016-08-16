package com.frysoft.notifry.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.frysoft.notifry.R;
import com.frysoft.notifry.activity.TimetableCreateActivity;
import com.frysoft.notifry.data.Event;
import com.frysoft.notifry.data.TimetableEntry;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Edwin Pichler on 24.06.2016.
 */
public class TimetableEventsAdapter extends BaseExpandableListAdapter{
    ArrayList<String> mGroupData;
    HashMap<String, ArrayList<Event>> mChildData;

    public TimetableEventsAdapter(ArrayList<Event> events){
        mChildData = new HashMap<>();
        mGroupData = new ArrayList<>();
        for (Event event : events){
            Date event_date = event.getDate();

            if (!mGroupData.contains(event_date.getString())) {
                ArrayList<Event> event_list = new ArrayList<>();
                event_list.add(event);
                mGroupData.add(event_date.getString());
                mChildData.put(event_date.getString(), event_list);
            }

        }
    }

    @Override
    public int getGroupCount() {
        return mGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildData.get(mGroupData.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mGroupData.get(groupPosition);
    }

    @Override
    public Event getChild(int groupPosition, int childPosition) {
        return mChildData.get(mGroupData.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {
        String date_title = getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.fragment_timetable_events_list_header, null);
        }

        TextView header = (TextView) convertView.findViewById(R.id.textview_timetable_events_list_header);
        header.setText(date_title);

        ExpandableListView lv = (ExpandableListView) viewGroup;
        lv.expandGroup(groupPosition);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final Event event = getChild(groupPosition, childPosition);
        Date date = event.getDate();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.fragment_timetable_events_list_item, null);
        }

        TextView event_name = (TextView) convertView.findViewById(R.id.textview_timetable_events_list_item_template_name);
        event_name.setText(event.getTitle());

        TextView event_time = (TextView) convertView.findViewById(R.id.textview_timetable_events_list_item_template_time);
        event_time.setText(event.getTimeStart().getString() + " - " + event.getTimeEnd().getString());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.getContext(), TimetableCreateActivity.class);
                intent.putExtra("entry", event.getEntry().getId());
                App.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}

