package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.Time;

public abstract class Event {

    protected final Date date;

    protected TimetableEntry entry;

    protected Event(TimetableEntry entry, int day, int month, int year) {
        this.entry = entry;

        date = new Date(day, month, year);
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

    public Date getDate() {
        return date;
    }

    public int getColor() {
        return entry.color;
    }

    public boolean isWholeDay() {
        return false;//(entry.time_start == Time.MIN_TIME && ((entry.duration + 1) % Time.MAX_TIME) == 0);
    }

    /**
     * This is a class
     */
    public static class WholeDay extends Event {

        public WholeDay(TimetableEntry entry, int day, int month, int year) {
            super(entry, day, month, year);
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

        public Start(TimetableEntry entry, int day, int month, int year) {
            super(entry, day, month, year);

            time_start = entry.time_start;
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

        public End(TimetableEntry entry, int day, int month, int year) {
            super(entry, day, month, year);

            time_end = entry.time_end;
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

        public StartEnd(TimetableEntry entry, int day, int month, int year) {
            super(entry, day, month, year);

            time_start = entry.time_start;
            time_end = entry.time_end;
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
