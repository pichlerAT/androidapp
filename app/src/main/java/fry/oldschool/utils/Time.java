package fry.oldschool.utils;

public class Time {

    public int time;

    public Time(int time) {
        this.time = time;
    }

    public Time(Time time) {
        this(time.time);
    }

    public Time(int hours, int minutes) {
        time = 60 * hours + minutes;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Time) {
            Time t = (Time) o;
            return (t.time == time);
        }
        return false;
    }

    public void add(int time) {
        this.time += time;
    }

    public void add(Time time) {
        add(time.time);
    }

    public short getShort() {
        return (short)time;
    }

    public int getHours() {
        return (time / 60);
    }

    public int getMinutes() {
        return (time % 60);
    }

    public String getString() {
        if(time > 1440) {
            int days = time/1440;
            int t = time%1440;
            return ( days + "days, " + (t/60) + "hours, " + (t%60) + "minutes" );
        }
        return ( time/60 + ":" + time%60 );
    }

    public Time copy() {
            return new Time(time);
        }

}