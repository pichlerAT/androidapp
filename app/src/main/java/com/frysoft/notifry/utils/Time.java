package com.frysoft.notifry.utils;

import java.util.Calendar;

public class Time implements Fryable {

    public static final byte INTERVALL_HOUR = 1;

    public static final byte INTERVALL_HALF_HOUR = 2;

    public static final byte INTERVALL_QUARTER_HOUR = 3;

    public static final short MIN_TIME = 0;

    public static final short MAX_TIME = 1440;

    public static final Time TIME_MIN = new Time(MIN_TIME);

    public static final Time TIME_MAX = new Time((short)(MAX_TIME - 1));


    public short time;

    public static short getTimezoneOffset() {
        return (short)(Calendar.getInstance().getTimeZone().getRawOffset() / 60000);
    }

    public static Time getCurrentTime() {
        return new Time(Calendar.getInstance());
    }

    public static Time getCurrentTime(int intervall) {
        Time time = getCurrentTime();

        if(intervall == INTERVALL_HOUR) {
            time.addMinutes(-time.getMinutes());

        }else if(intervall == INTERVALL_HALF_HOUR) {
            int m = time.getMinutes();

            if(m > 30) {
                time.addMinutes(60 - m);

            }else if(m > 0) {
                time.addMinutes(30 - m);

            }else {
                time.addMinutes(-m);
            }

        }else if(intervall == INTERVALL_QUARTER_HOUR) {
            int m = time.getMinutes();

            if(m > 45) {
                time.addMinutes(60 - m);

            }else if(m > 30) {
                time.addMinutes(45 - m);

            }else if(m > 15) {
                time.addMinutes(30 - m);

            }else if(m > 0) {
                time.addMinutes(15 - m);

            }else {
                time.addMinutes(-m);
            }
        }

        return time;
    }

    public Time(Calendar calendar) {
        time = (short)(calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE));
    }

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
        fry.writeShort(time);
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

    public int addMinutes(int minutes) {
        Logger.Log("Time", "add(int)");
        if(minutes < 0) {
            return subtractMinutes(-minutes);
        }

        int t = time + minutes;
        time = (short)(t % MAX_TIME);

        if(time == MAX_TIME) {
            time = MIN_TIME;
            return (t / MAX_TIME + 1);

        }else if(time < MIN_TIME) {
            time += MAX_TIME + 1;
            return (t / MAX_TIME - 1);
        }

        return (t / MAX_TIME);
    }

    public int subtractMinutes(int minutes) {
        Logger.Log("Time", "add(int)");
        if(minutes < 0) {
            return addMinutes(-minutes);
        }

        int t = time - minutes;
        time = (short)(t % MAX_TIME);

        if(time == MAX_TIME) {
            time = MIN_TIME;
            return (t / MAX_TIME + 1);

        }else if(time < MIN_TIME) {
            time += MAX_TIME + 1;
            return (t / MAX_TIME - 1);
        }

        return (t / MAX_TIME);
    }

    public int addTime(int hours, int minutes) {
        Logger.Log("Time", "add(int,int)");
        return addMinutes(hours*60 + minutes);
    }

    public int addTime(Time time) {
        Logger.Log("Time", "add(Time)");
        return addMinutes(time.time);
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
        int m = time % 60;
        int h = time / 60;
        return ((h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m);
    }

    public Time copy() {
        Logger.Log("Time", "copy()");
        return new Time(time);
    }

}