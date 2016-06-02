package fry.oldschool.utils;

public class DateTime {

    protected Date date;

    protected Time.Short time;

    protected DateTime(short date,short time) {
        this.date = new Date(date);
        this.time = new Time.Short(time);
    }

    protected DateTime(Date date,Time.Short time) {
        this.date = date.copy();
        this.time = time.copy();
    }

    protected void add(int time) {
        date.add(this.time.add(time));
    }

    protected String getString() {
        return date.getString() + ", " +time.getString();
    }

    public DateTime copy() {
        return new DateTime(date,time);
    }
}
