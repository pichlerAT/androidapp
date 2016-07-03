package fry.oldschool.utils;

public class Time implements Fryable {

    public static final short MIN_TIME = 0;

    public static final short MAX_TIME = 1440;

    public short time;

    public Time(FryFile fry) {
        this(fry.getShort());
    }

    public Time(short time) {
        this.time = time;
    }

    public Time(Time time) {
        this(time.time);
    }

    public Time(int hours, int minutes) {
        time = (short)(60 * hours + minutes);
    }

    @Override
    public void writeTo(FryFile fry) {
        fry.write(time);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Time) {
            Time t = (Time) o;
            return (t.time == time);
        }
        return false;
    }

    public int add(int time) {
        int t = this.time + time;
        this.time = (short)(t % MAX_TIME);
        return (t / MAX_TIME);
    }

    public int add(Time time) {
        return add(time.time);
    }

    public int getHours() {
        return (time / 60);
    }

    public int getMinutes() {
        return (time % 60);
    }

    public String getString() {
        return ( time/60 + ":" + time%60 );
    }

    public Time copy() {
        return new Time(time);
    }

}