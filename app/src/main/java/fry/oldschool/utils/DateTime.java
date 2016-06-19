package fry.oldschool.utils;

public class DateTime {

    public Date date;

    public Time.Short time;

    public DateTime(short date,short time) {
        this.date = new Date(date);
        this.time = new Time.Short(time);
    }

    public DateTime(Date date,Time.Short time) {
        this.date = date.copy();
        this.time = time.copy();
    }

    public void add(int time) {
        date.add(this.time.add(time));
    }

    public String getString() {
        return date.getString() + ", " +time.getString();
    }

    public DateTime copy() {
        return new DateTime(date,time);
    }
}
