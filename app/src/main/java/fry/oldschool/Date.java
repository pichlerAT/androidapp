package fry.oldschool;

/**
 * Created by Stefan on 29.04.2016.
 */
public class Date {

    protected short date;

    public Date(short date) {
        this.date = date;
    }

    public Date(String date) {
        String[] r = date.split("-");
        int year = Integer.parseInt(r[2]) - 2000;
        int month = Integer.parseInt(r[1]);
        int day = Integer.parseInt(r[0]);
        this.date = (short)( day + (month<<5) + (year<<9) );
    }

    public boolean leapYear() {
        int year = (date>>9)&127;
        return ( (year%4)== 0 );
    }

    public int getDaysOfMonth(int month) {
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
                if(leapYear()) {
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

    public int getDaysOfYear() {
        return (leapYear() ? 366 : 365);
    }

    public int getDaysUntilMonth(int month) {
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
            default: days = 0;
        }
        if(leapYear()) {
            ++days;
        }
        return days;
    }

    public int getDaysUntilYear(int year) {
        int y = year - 2000;
        int yp4 = y%4;
        return (y/4)*(4*365+1) + yp4*365 + ( yp4==0 ? 0 : 1 );
    }

    public int getTotalDaysUntil() {
        return getTotalDaysUntil(date);
    }

    public int getTotalDaysUntil(short date) {
        int day = date&31;
        int month = (date>>5)&15;
        int year = (date>>9)&127;
        return getDaysUntilYear(year+2000) + getDaysUntilMonth(month) + day + 4;
    }

    public int getDaysUntil(short date) {
        return ( getTotalDaysUntil(date) - getTotalDaysUntil(this.date) );
    }

    public int getDayOfWeek() {
        return (getTotalDaysUntil()%7);
    }

    public String getString() {
        int day = date&31;
        int month = (date>>5)&15;
        int year = (date>>9)&127;
        return day + "-" + month + "-" + (year+2000);
    }

    public String getMonth() {
        int month = (date>>5)&15;
        return getMonth(month);
    }

    public String getMonth(int month) {
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

    public String getWeekday() {
        return getWeekday(getDayOfWeek());
    }

    public String getWeekday(int weekday) {
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

    public Date copy() {
        return new Date(date);
    }
}