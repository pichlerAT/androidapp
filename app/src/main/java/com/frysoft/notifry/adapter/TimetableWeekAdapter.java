package com.frysoft.notifry.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frysoft.notifry.R;
import com.frysoft.notifry.activity.TimetableCreateActivity;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.data.Event;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;

import java.util.ArrayList;

/**
 * Created by Edwin Pichler on 03.08.2016.
 */
public class TimetableWeekAdapter extends RecyclerView.Adapter<TimetableWeekAdapter.TimetableViewHolder>{

    private ArrayList<Date> mDays;
    public TimetableWeekAdapter(ArrayList<Date> days){
        mDays = days;
    }
    private int mPosToday;

    @Override
    public TimetableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_timetable_week_item, parent, false);
        return new TimetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimetableViewHolder holder, int position) {
        final Date date = mDays.get(position);
        holder.mDate.setText(date.getString());

        if (date.getString().equals(Date.getToday().getString())){
            holder.mHeader.setBackgroundColor(App.getColorFromID(R.color.colorPrimary));
            holder.mDay.setText(date.getWeekdayName() + " (" + App.getContext().getString(R.string.today) + ")");
            mPosToday = position;
        }
        else
            holder.mDay.setText(date.getWeekdayName());

        ArrayList<Event> events = Data.Timetable.getEvents(date, date);

        LayoutInflater inflater = (LayoutInflater) App.getContext().getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        if (events.size() > 0) {
            holder.mEvents.removeView(holder.mEvents.findViewById(R.id.textview_timetable_week_item_no_events));
        }
        holder.mEvents.removeAllViews();
        for (final Event event : events){
            View view = inflater.inflate(R.layout.timetable_week_item_template, null);
            TextView time_view = (TextView) view.findViewById(R.id.timetable_week_item_template_time);
            TextView name_view = (TextView) view.findViewById(R.id.timetable_week_item_template_name);

            time_view.setText(event.getTimeStart().getString() + " - " + event.getTimeEnd().getString());
            name_view.setText(event.getTitle());

            holder.mEvents.addView(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(App.getContext(), TimetableCreateActivity.class);
                    intent.putExtra("entry", event.getEntry().getId());
                    App.getContext().startActivity(intent);
                }
            });

            AppCompatButton delete = (AppCompatButton) view.findViewById(R.id.button_timetable_week_item_template_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    event.getEntry().delete();
                    notifyDataSetChanged();
                }
            });

        }
        holder.mAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.getContext(), TimetableCreateActivity.class);
                intent.putExtra("day", date.day);
                intent.putExtra("month", date.month);
                intent.putExtra("year", date.year);
                App.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public class TimetableViewHolder extends RecyclerView.ViewHolder{
        public TextView mDay, mDate;
        public LinearLayout mEvents;
        public RelativeLayout mHeader;
        public AppCompatButton mAddEntry;

        public TimetableViewHolder(View view){
            super(view);
            mDay = (TextView) view.findViewById(R.id.textview_timetable_week_item_day);
            mDate = (TextView) view.findViewById(R.id.textview_timetable_week_item_date);
            mHeader = (RelativeLayout) view.findViewById(R.id.relativelayout_timetable_week_item_header);
            mEvents = (LinearLayout) view.findViewById(R.id.linearlayout_timetable_week_item_events);
            mAddEntry = (AppCompatButton) view.findViewById(R.id.button_timetable_week_item_add);

        }
    }

    public int getItemPosition(String today)
    {
        for (int position=0; position<mDays.size(); position++)
            if (mDays.get(position).getString().equals(today))
                return position;
        return 0;
    }
}
