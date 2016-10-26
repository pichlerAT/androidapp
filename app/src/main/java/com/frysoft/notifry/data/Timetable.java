package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;

import java.util.ArrayList;

public class Timetable extends MySQLEntry {

    protected Timetable() {
        super(0, 0, 0);
    }

    public Manager<TimetableEntry> Entries = new Manager<>();

    public ShareList shares = new ShareList(this);

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
    protected void addData(MySQL mysql) {
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public int getShareId() {
        return 0;
    }

    @Override
    protected void sync(MySQLEntry entry) {
    }

    @Override
    protected char getType() {
        return TYPE_TIMETABLE;
    }

}
