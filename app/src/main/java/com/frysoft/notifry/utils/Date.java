package com.frysoft.notifry.utils;

import java.util.ArrayList;
import java.util.Calendar;

import com.frysoft.notifry.R;

public class Date {

    public static final byte INTERVAL_NO_INTERVAL  = 0;

    public static final byte INTERVAL_HOUR 		   = 1;

    public static final byte INTERVAL_HALF_HOUR    = 2;

    public static final byte INTERVAL_QUARTER_HOUR = 3;

    public int minute;

    public int hour;

    public int day;

    public int month;

    public int year;

    public Date(int minute, int hour, int day, int month, int year) {
        this.minute = minute;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Date(int date) {
        this((date & 0x3F),
            ((date >> 6) & 0x1F),
            ((date >> 11) & 0x1F),
            ((date >> 16) & 0xF),
            ((date >> 20) & 0xFFF));
    }

    public Date(char d1, char d2) {
        this((d1 & 0x3F),
            ((d1 >> 6) & 0x1F),
            ((d1 >> 11) & 0x1F),
            ((d2) & 0xF),
            ((d2 >> 4) & 0xFFF));
    }

    public Date(int minute, int hour) {
        this(minute, hour, 0, 0, 0);
    }

    public Date(int day, int month, int year) {
        this(0, 0, day, month, year);
    }

    public Date(Calendar calendar) {
        this(calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR));
    }

    public Date(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        this.minute = calendar.get(Calendar.MINUTE);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.year = calendar.get(Calendar.YEAR);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Date) {
            Date d = (Date) o;
            return (d.minute == minute
                    && d.hour == hour
                    && d.day == day
                    && d.month == month
                    && d.year == year);
        }
        return false;
    }

    public int getInt() {
        return ( minute |
                (hour << 6) |
                (day << 11) |
                (month << 16) |
                (year << 20));
    }

    public char[] getChars() {
        return new char[]{
                (char)( minute |
                       (hour << 6) |
                       (day << 11) ),
                (char)( month |
                       (year << 4) )
        };
    }

    public void setTime(Date date) {
        minute = date.minute;
        hour = date.hour;
    }

    public void addMinutes(int minutes) {
        if(minutes < 0) {
            subtractMinutes(-minutes);
            return;
        }

        minute += minutes;

        if(minute > 59) {
            int r = minute / 60;
            minute -= r * 60;
            hour += r;

            if(hour > 23) {
                r = hour / 24;
                hour -= r * 24;
                addDays(r);
            }
        }
    }

    public void subtractMinutes(int minutes) {
        if(minutes < 0) {
            addMinutes(-minutes);
            return;
        }

        minute -= minutes;

        if(minute < 0) {
            int r = 1 - minute / 60;
            minute += r * 60;
            hour -= r;

            if(hour < 0) {
                r = 1 - hour / 24;
                hour += r * 24;
                subtractDays(r);
            }
        }
    }

    public void addHours(int hours) {
        if(hours < 0) {
            subtractMinutes(-hours);
            return;
        }

        hour += hours;

        if(hour > 23) {
            int r = hour / 24;
            hour -= r * 24;
            addDays(r);
        }
    }

    public void subtractHours(int hours) {
        if(hours < 0) {
            addMinutes(-hours);
            return;
        }

        hour -= hours;

        if(hour < 0) {
            int r = 1 - hour / 24;
            hour += r * 24;
            subtractDays(r);
        }
    }

    public void setMinTime() {
        hour = 0;
        minute = 0;
    }

    public void setMaxTime() {
        hour = 23;
        minute = 59;
    }

    public void setDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public void addTime(int hours, int minutes) {
        addMinutes(hours * 60 + minutes);
    }

    public void subtractTime(int hours, int minutes) {
        subtractMinutes(hours * 60 + minutes);
    }

    public void addDays(int days) {
        if(days < 0) {
            subtractDays(-days);
        }

        day += days;

        int dom = getDaysOfMonth();
        while(day > dom) {

            day -= dom;
            month++;

            if(month > 12) {
                month = 1;
                year ++;
            }

            dom = getDaysOfMonth();
        }
    }

    public void subtractDays(int days) {
        if(days < 0) {
            addDays(-days);
        }

        day -= days;

        while(day < 1) {
            month--;
            if(month < 1) {
                month = 12;
                year--;
            }

            day += getDaysOfMonth();
        }
    }

    public void addMonths(int months) {
        if(months < 0) {
            subtractMonths(-months);
        }

        month += months;

        while(month > 12) {
            month -= 12;
            year++;
        }
    }

    public void subtractMonths(int months) {
        if(months < 0) {
            addMonths(-months);
        }

        month -= months;

        while(month < 1) {
            month += 12;
            year--;
        }
    }

    public void addYears(int years) {
        year += years;
    }

    public void subtractYears(int years) {
        year -= years;
    }

    public String getTimeString() {
        return ((hour < 10 ? "0" : "") + hour + ":" + (minute < 10 ? "0" : "") + minute);
    }

    public String getDateString() {
        return ((day < 10 ? "0" : "") + day + "." + (month < 10 ? "0" : "") + month + "." + year);
    }

    public String getString() {
        return (getDateString() + " , " + getTimeString());
    }

    public Date copy() {
        return new Date(minute, hour, day, month, year);
    }

    public boolean isBeforeTime(Date time) {
        if(hour < time.hour) {
            return true;
        }else if(hour > time.hour) {
            return false;
        }

        return (minute < time.minute);
    }

    public boolean isBeforeDate(Date date) {
        if(year < date.year) {
            return true;
        }else if(year > date.year) {
            return false;
        }

        if(month < date.month) {
            return true;
        }else if(month > date.month) {
            return false;
        }

        return (day < date.day);
    }

    public boolean isBefore(Date date) {
        if(year < date.year) {
            return true;
        }else if(year > date.year) {
            return false;
        }

        if(month < date.month) {
            return true;
        }else if(month > date.month) {
            return false;
        }

        if(day < date.day) {
            return true;
        }else if(day > date.day) {
            return false;
        }

        if(hour < date.hour) {
            return true;
        }else if(hour > date.hour) {
            return false;
        }

        return (minute < date.minute);
    }

    public boolean isAfterTime(Date time) {
        if(hour > time.hour) {
            return true;
        }else if(hour < time.hour) {
            return false;
        }

        return (minute > time.minute);
    }

    public boolean isAfterDate(Date date) {
        if(year > date.year) {
            return true;
        }else if(year < date.year) {
            return false;
        }

        if(month > date.month) {
            return true;
        }else if(month < date.month) {
            return false;
        }

        return (day > date.day);
    }

    public boolean isAfter(Date date) {
        if(year > date.year) {
            return true;
        }else if(year < date.year) {
            return false;
        }

        if(month > date.month) {
            return true;
        }else if(month < date.month) {
            return false;
        }

        if(day > date.day) {
            return true;
        }else if(day < date.day) {
            return false;
        }

        if(hour > date.hour) {
            return true;
        }else if(hour < date.hour) {
            return false;
        }

        return (minute > date.minute);
    }

    public boolean isBetweenTime(Date start, Date end) {
        return (isAfterTime(start) && isBeforeTime(end));
    }

    public boolean isBetweenDate(Date start, Date end) {
        return (isAfterDate(start) && isBeforeDate(end));
    }

    public boolean isBetween(Date start, Date end) {
        return (isAfter(start) && isBefore(end));
    }

    public boolean isInBetweenTime(Date start, Date end) {
        return !(isBeforeTime(start) && isAfterTime(end));
    }

    public boolean isInBetweenDate(Date start, Date end) {
        return !(isBeforeDate(start) && isAfterDate(end));
    }

    public boolean isInBetween(Date start, Date end) {
        return !(isBefore(start) && isAfter(end));
    }

    public Date getNextDay() {
        int dom = getDaysOfMonth();
        if(day == dom) {
            if(month == 12) {
                return new Date(1, 1, year + 1);
            }
            return new Date(1, month + 1, year);
        }
        return new Date(day + 1, month, year);
    }

    public boolean isLeapYear() {
        return isLeapYear(year);
    }

    public int getDaysOfMonth() {
        return getDaysOfMonth(month, year);
    }

    public int getDaysOfYear() {
        return getDaysOfYear(year);
    }

    public int getTotalDays() {
        return getTotalDaysUntil();
    }

    public int getDaysUntil(Date date) {
        return (date.getTotalDaysUntil() - getTotalDaysUntil());
    }

    public int getDayOfWeek() {
        return ((getTotalDaysUntil() + 4) % 7);
    }

        public String getMonthName() {
            return getMonthName(month);
        }

        public String getWeekdayName() {
            return getWeekdayName(getDayOfWeek());
        }

    public Date getFirstDayOfWeek() {
        Date date = new Date(day, month, year);
        date.subtractDays(getDayOfWeek());
        return date;
    }

    public int getWeekOfYear() {
        return (getFirstDayOfWeek().getDaysUntil(this) / 7 + 1);
    }

    public int getDayOfYear() {
        return (getDaysUntilMonth(month, year) + day);
    }

    public int getTotalDaysUntil() {
        return getDaysUntilYear(year) + getDaysUntilMonth(month, year) + day;
    }

    public String getISOString() {
        return (year + "" + (month < 10 ? "0" : "") + month + "" + (day < 10 ? "0" : "") + day + "T000000");
    }

    public static Date getDateFromISOString(String str) {
        return new Date(Integer.parseInt(str.substring(6, 8)), Integer.parseInt(str.substring(4, 6)), Integer.parseInt(str.substring(0, 4)));
    }

    public static boolean isLeapYear(int year) {
        return ((year % 4) == 0);
    }

    public static int getDaysOfYear(int year) {
        return (isLeapYear(year) ? 366 : 365);
    }

    public static int getDaysUntilYear(int year) {
        return (int)(year * 365.25);
    }

    public static int getDaysOfMonth(int month, int year) {
        switch(month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if(isLeapYear(year)) {
                    return 29;
                }else {
                    return 28;
                }
            case 4:
            case 6:
            case 9:
            case 11:
            default:
                return 30;
        }
    }

    public static int getDaysUntilMonth(int month, int year) {
        int days;
        switch(month) {
            case 1: return 0;
            case 2: return 31;
            case 3: days = 59; break;
            case 4: days = 90; break;
            case 5: days = 120; break;
            case 6: days = 151; break;
            case 7: days = 181; break;
            case 8: days = 212; break;
            case 9: days = 243; break;
            case 10: days = 273; break;
            case 11: days = 304; break;
            case 12: days = 334; break;
            default: return 0;
        }
        if(isLeapYear(year)) {
            ++days;
        }
        return days;
    }

    public static String getWeekdayName(int weekday) {
        switch(weekday) {
            case 0: return App.getContext().getResources().getString(R.string.weekday_mon);
            case 1: return App.getContext().getResources().getString(R.string.weekday_tue);
            case 2: return App.getContext().getResources().getString(R.string.weekday_wed);
            case 3: return App.getContext().getResources().getString(R.string.weekday_thu);
            case 4: return App.getContext().getResources().getString(R.string.weekday_fri);
            case 5: return App.getContext().getResources().getString(R.string.weekday_sat);
            case 6: return App.getContext().getResources().getString(R.string.weekday_sun);
            default: return "";
        }
    }

    public static String getMonthName(int month) {
        switch(month) {
            case 1: return App.getContext().getResources().getString(R.string.month_jan);
            case 2: return App.getContext().getResources().getString(R.string.month_feb);
            case 3: return App.getContext().getResources().getString(R.string.month_mar);
            case 4: return App.getContext().getResources().getString(R.string.month_apr);
            case 5: return App.getContext().getResources().getString(R.string.month_may);
            case 6: return App.getContext().getResources().getString(R.string.month_jun);
            case 7: return App.getContext().getResources().getString(R.string.month_jul);
            case 8: return App.getContext().getResources().getString(R.string.month_aug);
            case 9: return App.getContext().getResources().getString(R.string.month_sep);
            case 10: return App.getContext().getResources().getString(R.string.month_oct);
            case 11: return App.getContext().getResources().getString(R.string.month_nov);
            case 12: return App.getContext().getResources().getString(R.string.month_dec);
            default: return "";
        }
    }

    public static Date getCurrent() {
        return new Date(Calendar.getInstance());
    }

    public static short getTimezoneOffset() {
        return (short)(Calendar.getInstance().getTimeZone().getRawOffset() / 60000);
    }

    public static long getMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static Date getCurrent(int timeIntervall) {
        Date date = getCurrent();

        if(timeIntervall == INTERVAL_HOUR) {
            if(date.minute > 0) {
                date.addHours(1);
            }
            date.minute = 0;

        }else if(timeIntervall == INTERVAL_HALF_HOUR) {

            if(date.minute > 30) {
                date.minute = 0;
                date.addHours(1);

            }else if(date.minute > 0) {
                date.minute = 30;

            }else {
                date.minute = 0;

            }

        }else if(timeIntervall == INTERVAL_QUARTER_HOUR) {

            if(date.minute > 45) {
                date.minute = 0;
                date.addHours(1);

            }else if(date.minute > 30) {
                date.minute = 45;

            }else if(date.minute > 15) {
                date.minute = 30;

            }else if(date.minute > 0) {
                date.minute = 15;

            }else {
                date.minute = 0;

            }

        }

        return date;
    }

    public static ArrayList<Date> getWeek() {
        Cursor date = (Cursor) getCurrent().getFirstDayOfWeek();
        ArrayList<Date> week = new ArrayList<>(7);
        week.add(date.copy());

        for(int i=0; i<6; ++i) {
            date.goToNextDay();
            week.add(date.copy());
        }

        return week;
    }

    public static class Cursor extends Date {

        public Cursor(int minute, int hour, int day, int month, int year) {
            super(minute, hour, day, month, year);
        }

        public Cursor(int date) {
            super(date);
        }

        public Cursor(char d1, char d2) {
            super(d1, d2);
        }

        public Cursor(int minute, int hour) {
            super(minute, hour);
        }

        public Cursor(int day, int month, int year) {
            super(day, month, year);
        }

        public Cursor(Calendar calendar) {
            super(calendar);
        }

        public Cursor(Date date) {
            super(date.minute, date.hour, date.day, date.month, date.year);
        }

        public void goToNextDay() {
            ++day;
            if(day > getDaysOfMonth()) {
                day = 1;
                ++month;
                if(month > 12) {
                    month = 1;
                    ++year;
                }
            }
        }

        public void goToPreviousDay() {
            --day;
            if(day < 1) {
                --month;
                if(month < 1) {
                    --year;
                    month = 12;
                }
                day = getDaysOfMonth();
            }
        }

        public void goToNextMonth() {
            ++month;
            if(month > 12) {
                month = 1;
                ++year;
            }
        }

        public void goToPreviousMonth() {
            --month;
            if(month < 1) {
                month = 12;
                --year;
            }
        }

        public void goToNextYear() {
            ++year;
        }

        public void goToPreviousYear() {
            --year;
        }

        public boolean goToDate(int year, int month, int day) {
            if(month < 1 || month > 12) {
                return false;
            }
            if(day < 1 || day > getDaysOfMonth(month, year)) {
                return false;
            }
            this.month = month;
            this.day = day;
            return true;
        }

        public boolean goToYearDate(int month, int day) {
            if(month < 1 || month > 12) {
                return false;
            }
            if(day < 1 || day > getDaysOfMonth(month, year)) {
                return false;
            }
            this.month = month;
            this.day = day;
            return true;
        }

        public boolean goToMonthDay(int day) {
            if(day < 1 || day > getDaysOfMonth()) {
                return false;
            }
            this.day = day;
            return true;
        }

        public boolean goToYearDay(int day) {
            if(day < 1 || day > getDaysOfYear()) {
                return false;
            }
            this.day = day;
            this.month = day / 30;

            int dom = getDaysOfMonth();
            while(this.day > dom) {
                ++month;
                this.day -= dom;
                dom = getDaysOfMonth();
            }

            return true;
        }

        public boolean goToMonth(int month) {
            if(month < 1 || month > 12) {
                return false;
            }
            this.month = month;
            return true;
        }

        public void goToFirstDayOfWeek() {
            subtractDays(getDayOfWeek());
        }

    }

}
