package fry.oldschool.utils;

public class Time implements Fryable {

    public static final short MIN_TIME = 0;

    public static final short MAX_TIME = 1440;

    public short time;

    public Time(FryFile fry) {
        this(fry.getShort());
        Logger.Log("Time", "Time(FryFile)");
    }

    public Time(short time) {
        Logger.Log("Time", "Time(short)");
        this.time = time;
    }

    public Time(Time time) {
        this(time.time);
        Logger.Log("Time", "Time(Time)");
    }

    public Time(int hours, int minutes) {
        Logger.Log("Time", "Time(int,int)");
        time = (short)(60 * hours + minutes);
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("Time", "writeTo(FryFile)");
        fry.write(time);
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("Time", "equals(Object)");
        if(o instanceof Time) {
            Time t = (Time) o;
            return (t.time == time);
        }
        return false;
    }

    public int add(int time) {
        Logger.Log("Time", "add(int)");
        int t = this.time + time;
        this.time = (short)(t % MAX_TIME);
        return (t / MAX_TIME);
    }

    public int add(Time time) {
        Logger.Log("Time", "add(Time)");
        return add(time.time);
    }

    public int getHours() {
        Logger.Log("Time", "getHours()");
        return (time / 60);
    }

    public int getMinutes() {
        Logger.Log("Time", "getMinutes()");
        return (time % 60);
    }

    public String getString() {
        Logger.Log("Time", "getString()");
        return ( time/60 + ":" + time%60 );
    }

    public Time copy() {
        Logger.Log("Time", "copy()");
        return new Time(time);
    }

}