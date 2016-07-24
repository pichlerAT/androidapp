package com.frysoft.notifry.utils;

public class DateSpan implements Fryable {

    protected int duration;

    protected Date date_start;

    protected Date date_end;

    protected Time time_start;

    protected Time time_end;

    public DateSpan(FryFile fry) {
        this(fry.getShort(), fry.getShort(), fry.getInt());
        Logger.Log("DateSpan", "DateSpan(FryFile)");
    }

    public DateSpan(Date date_start, Time time_start, int duration) {
        Logger.Log("DateSpan", "DateSpan(Date,Time,int)");
        this.date_start = new Date(date_start);
        this.date_end = new Date(date_start);

        this.time_start = new Time(time_start);
        this.time_end = new Time(time_start);

        this.duration = duration;

        date_end.addDays(time_end.add(duration));
    }

    public DateSpan(short date_start, short time_start, int duration) {
        this(new Date(date_start), new Time(time_start), duration);
        Logger.Log("DateSpan", "DateSpan(short,short,int)");
    }

    public DateSpan(Date date_start, Date date_end) {
        Logger.Log("DateSpan", "DateSpan(Date,Date)");
        this.date_start = date_start.copy();
        this.date_end = date_end.copy();

        this.time_start = new Time(Time.MIN_TIME);
        this.time_end = new Time(Time.MAX_TIME);

        duration = 1440 * (date_end.getTotalDays() - date_start.getTotalDays() + 1);
    }

    public DateSpan(Date date_start, Time time_start, Date date_end, Time time_end) {
        Logger.Log("DateSpan", "DateSpan(Date,Time,Date,Time)");
        this.date_start = date_start.copy();
        this.time_start = time_start.copy();
        this.time_end = time_start.copy();
        this.date_end = date_end.copy();
        duration = date_start.getDaysUntil(date_end) * Time.MAX_TIME - time_start.time + time_end.time;
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("DateSpan", "writeTo(FryFile)");
        date_start.writeTo(fry);
        time_start.writeTo(fry);
        fry.write(duration);
    }

    public Date getDateStart() {
        Logger.Log("DateSpan", "getDateStart()");
        return date_start;
    }

    public Date getDateEnd() {
        Logger.Log("DateSpan", "getDateEnd()");
        return date_end;
    }

    public Time getTimeStart() {
        Logger.Log("DateSpan", "getTimeStart()");
        return time_start;
    }

    public Time getTimeEnd() {
        Logger.Log("DateSpan", "getTimeEnd()");
        return time_end;
    }

    public int getDuration() {
        Logger.Log("DateSpan", "getDuration()");
        return duration;
    }

    public boolean isInsideSpan(Date date) {
        Logger.Log("DateSpan", "isInsideSpan(Date)");
        return (date_start.isGreaterThen(date) && date_end.isSmallerThen(date));
    }

    public boolean isOverlapping(DateSpan span) {
        Logger.Log("DateSpan", "isOverlapping(DateSpan)");
        return (isInsideSpan(span.date_start) || isInsideSpan(span.date_end) || span.isInsideSpan(date_start));
    }

}
