package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.Time;

public abstract class Event {

    protected final Date date;

    protected TimetableEntry entry;

    protected Event(TimetableEntry entry, final Date date) {
        this.entry = entry;
        this.date = date.copy();
    }

    public Time getTimeStart() {
        return Time.TIME_MIN;
    }

    public Time getTimeEnd() {
        return Time.TIME_MAX;
    }

    public TimetableEntry getEntry() {
        return entry;
    }

    public String getTitle() {
        return entry.title;
    }

    public String getDescription() {
        return entry.description;
    }

    public Date getDate() {
        return date;
    }

    public int getColor() {
        return entry.color;
    }

    public boolean isWholeDay() {
        return false;
    }

    /**
     * This is a class
     */
    public static class WholeDay extends Event {

        public WholeDay(TimetableEntry entry, Date date) {
            super(entry, date);
        }

        @Override
        public boolean isWholeDay() {
            return true;
        }

    }

    /**
     * This is a class
     */
    public static class Start extends Event {

        protected short time_start;

        public Start(TimetableEntry entry, Date date) {
            super(entry, date);
            time_start = entry.getTimeStart().time;
        }

        @Override
        public Time getTimeStart() {
            return new Time(time_start);
        }

    }

    /**
     * This is a class
     */
    public static class End extends Event {

        protected short time_end;

        public End(TimetableEntry entry, Date date) {
            super(entry, date);
            time_end = entry.getTimeEnd().time;
        }

        @Override
        public Time getTimeEnd() {
            return new Time(time_end);
        }

    }

    /**
     * This is a class
     */
    public static class StartEnd extends Event {

        protected short time_start;

        protected short time_end;

        public StartEnd(TimetableEntry entry, Date date) {
            super(entry, date);
            time_start = entry.getTimeStart().time;
            time_end = entry.getTimeEnd().time;
        }

        @Override
        public Time getTimeStart() {
            return new Time(time_start);
        }

        @Override
        public Time getTimeEnd() {
            return new Time(time_end);
        }

    }

}
