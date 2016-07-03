package fry.oldschool.utils;

public class DateSpan implements Fryable {

    protected int duration;

    protected Date date_start;

    protected Date date_end;

    protected Time time_start;

    protected Time time_end;

    public DateSpan(FryFile fry) {
        this(fry.getShort(), fry.getShort(), fry.getInt());
    }

    public DateSpan(short date_start, short time_start, int duration) {
        this.date_start = new Date(date_start);
        this.date_end = new Date(date_start);

        this.time_start = new Time(time_start);
        this.time_end = new Time(time_start);

        this.duration = duration;

        date_end.addDays(time_end.add(duration));
    }

    public DateSpan(Date date_start, Date date_end) {
        this.date_start = date_start;
        this.date_end = date_end;

        this.time_start = new Time(Time.MIN_TIME);
        this.time_end = new Time(Time.MAX_TIME);

        duration = 1440 * (date_end.getTotalDays() - date_start.getTotalDays() + 1);
    }

    @Override
    public void writeTo(FryFile fry) {
        date_start.writeTo(fry);
        time_start.writeTo(fry);
        fry.write(duration);
    }

    public short getDateStart() {
        return date_start.getShort();
    }

    public short getTimeStart() {
        return time_start.time;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isInsideSpan(Date date) {
        return (date_start.isGreaterThen(date) && date_end.isSmallerThen(date));
    }

    public boolean isOverlapping(DateSpan span) {
        return (isInsideSpan(span.date_start) || isInsideSpan(span.date_end) || span.isInsideSpan(date_start));
    }

}
