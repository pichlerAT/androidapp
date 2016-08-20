package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;

import java.util.ArrayList;

public class Timetable extends MySQL {

    protected Timetable() {
        super(0, User.getId());
    }

    public Manager<TimetableEntry> Entries = new Manager<>();

    public ShareList Shares = new ShareList(this);

    public ArrayList<Event> getEvents(final Date start, final Date end) {
        ArrayList<Event> list = new ArrayList<>();
        EventIterator it;
        Event e;
        for(TimetableEntry ent : Entries.getList()) {
            it = new EventIterator(ent, start, end);
            while((e = it.next()) != null) {
                list.add(e);
            }
        }
        return list;
    }

    public void synchronizeAndroidCalendarIntoNotifry() {
        ConnectionManager.synchronizeAndroidCalendar();
    }

    protected TimetableEntry getByGoogleId(String google_id) {
        for(TimetableEntry ent : Entries.getList()) {
            if(ent.google_id.equals(google_id)) {
                return ent;
            }
        }
        return null;
    }

    @Override
    protected void remove() {
    }

    @Override
    protected boolean mysql_create() {
        return true;
    }

    @Override
    protected boolean mysql_update() {
        return true;
    }

    @Override
    protected byte getType() {
        return TYPE_CALENDAR;
    }

    @Override
    protected String getPath() {
        return DIR_CALENDAR;
    }
}
