package fry.oldschool.utils;

public class DateTime {

    public Date date;

    public Time time;

    public DateTime(short date,short time) {
        this.date = new Date(date);
        this.time = new Time(time);
    }

    public DateTime(Date date,Time time) {
        this.date = date.copy();
        this.time = time.copy();
    }

    public String getString() {
        return date.getString() + ", " +time.getString();
    }

    public DateTime copy() {
        return new DateTime(date,time);
    }

    public boolean smallerThen(DateTime dt) {
        return (!date.greaterThen(dt.date) && time.time < dt.time.time);
    }

    public boolean greaterThen(DateTime dt) {
        return (!date.smallerThen(dt.date) && time.time > dt.time.time);
    }

}
