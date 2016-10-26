package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;

public class Event {

    public static final int TYPE_START_END  = 0 ;
    public static final int TYPE_START      = 1 ;
    public static final int TYPE_END        = 2 ;
    public static final int TYPE_WHOLE_DAY  = 3 ;

    protected final Date start;

    protected final Date end;

    protected TimetableEntry entry;

    protected Event(TimetableEntry entry, final Date date, int type) {
        this.entry = entry;
        start = date.copy();
        end = date.copy();

        switch(type) {

            case TYPE_START_END:
                start.setTime(entry.getStart());
                end.setTime(entry.getEnd());
                break;

            case TYPE_START:
                start.setTime(entry.getStart());
                end.minute = 59;
                end.hour = 23;
                break;

            case TYPE_END:
                start.minute = 0;
                start.hour = 0;
                end.setTime(entry.getEnd());
                break;

            case TYPE_WHOLE_DAY:
                start.minute = 0;
                start.hour = 0;
                end.minute = 59;
                end.hour = 23;
                break;

        }
    }

    public TimetableEntry getEntry() {
        return entry;
    }

    public String getTitle() {
        return entry.title.getValue();
    }

    public String getDescription() {
        return entry.description.getValue();
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public int getColor() {
        return entry.color.getValue();
    }

    public boolean isWholeDay() {
        return entry.rRule.getValue().isWholeDay();
    }

}
