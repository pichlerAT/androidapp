package fry.oldschool.utils;

public class Time {

    public short time;

    public Time(short time) {
        this.time = time;
    }

    public void add(short time) {
        this.time += time;
    }

    public void add(Time time) {
        this.time += time.time;
    }

    public String getString() {
        return time/60 + ":" + time%60;
    }

}