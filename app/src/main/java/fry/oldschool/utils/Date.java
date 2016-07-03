package fry.oldschool.utils;

import java.util.Calendar;

import fry.oldschool.R;

public class Date implements Fryable {

    public static final int YEAR_OFFSET = 2000;

    public static Date getToday() {
        return new Date(Calendar.getInstance());
    }

    protected int day;

    protected int month;

    protected int year;

    public Date(FryFile fry) {
        this(fry.getShort());
    }

    public Date(Calendar cal) {
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
    }

    public Date(short date) {
        day = date & 31;
        month = (date >> 5) & 15;
        year = (date >> 9) & 127 + YEAR_OFFSET;
    }

    public Date(int day,int month,int year) {
        this.day   = day;
        this.month = month;
        this.year  = year;
    }

    public Date(String date) {
        String[] r = date.split("-");
        day   = Integer.parseInt(r[0]);
        month = Integer.parseInt(r[1]);
        year  = Integer.parseInt(r[2]);
    }

    @Override
    public void writeTo(FryFile fry) {
        fry.write(getShort());
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Date) {
            Date d = (Date) o;
            return (d.day == day && d.month == month && d.year == year);
        }
        return false;
    }

    public boolean isSmallerThen(Date date) {
        return (year < date.year && month < date.month && day < date.day);
    }

    public boolean isGreaterThen(Date date) {
        return (year > date.year && month > date.month && day > date.day);
    }

    public short getShort() {
        return (short)(day + (month << 5) + ((year - YEAR_OFFSET) << 9));
    }

    public void addDays(int days) {
        day += days;
        int dom = getDaysOfMonth();
        while( day > dom || month > 12) {
            if(month > 12) {
                day -= 31;
                month = 1;
                year++;
                dom = getDaysOfMonth();
            }
            if(day > dom) {
                day -= dom;
                month++;
                dom = getDaysOfMonth();
            }
        }
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
        return getDaysUntilYear(year) + getDaysUntilMonth(month, year) + day + 4;
    }

    public int getDaysUntil(Date date) {
        return ( getTotalDaysUntil(date) - getTotalDaysUntil(this) );
    }

    public int getDayOfWeek() {
        return (getTotalDaysUntil(this) % 7);
    }

    public String getString() {
        return (day + "-" + month + "-" + year);
    }

    public String getMonthName() {
        return getMonthName(month);
    }

    public String getWeekdayName() {
        return getWeekdayName(getDayOfWeek());
    }

    public Date copy() {
        return new Date(day, month, year);
    }

    public static boolean isLeapYear(int year) {
        return ( (year%4)== 0 );
    }

    public static int getDaysOfYear(int year) {
        return (isLeapYear(year) ? 366 : 365);
    }

    public static int getDaysUntilYear(int year) {
        int y = year - 2000;
        int yp4 = y%4;
        return (y/4)*(4*365+1) + yp4*365 + ( yp4==0 ? 0 : 1 );
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

    public static int getTotalDaysUntil(Date date) {
        return getDaysUntilYear(date.year) + getDaysUntilMonth(date.year, date.month) + date.day + 4;
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
            case 0: return App.mContext.getResources().getString(R.string.weekday_mon);
            case 1: return App.mContext.getResources().getString(R.string.weekday_tue);
            case 2: return App.mContext.getResources().getString(R.string.weekday_wed);
            case 3: return App.mContext.getResources().getString(R.string.weekday_thu);
            case 4: return App.mContext.getResources().getString(R.string.weekday_fri);
            case 5: return App.mContext.getResources().getString(R.string.weekday_sat);
            case 6: return App.mContext.getResources().getString(R.string.weekday_sun);
            default: return "";
        }
    }

    public static String getMonthName(int month) {
        switch(month) {
            case 1: return App.mContext.getResources().getString(R.string.month_jan);
            case 2: return App.mContext.getResources().getString(R.string.month_feb);
            case 3: return App.mContext.getResources().getString(R.string.month_mar);
            case 4: return App.mContext.getResources().getString(R.string.month_apr);
            case 5: return App.mContext.getResources().getString(R.string.month_may);
            case 6: return App.mContext.getResources().getString(R.string.month_jun);
            case 7: return App.mContext.getResources().getString(R.string.month_jul);
            case 8: return App.mContext.getResources().getString(R.string.month_aug);
            case 9: return App.mContext.getResources().getString(R.string.month_sep);
            case 10: return App.mContext.getResources().getString(R.string.month_oct);
            case 11: return App.mContext.getResources().getString(R.string.month_nov);
            case 12: return App.mContext.getResources().getString(R.string.month_dec);
            default: return "";
        }
    }

}