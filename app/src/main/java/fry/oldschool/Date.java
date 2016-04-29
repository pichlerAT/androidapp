package fry.oldschool;

/**
 * Created by Stefan on 29.04.2016.
 */
public class Date {

    //  1.1.2000 = SATURDAY(6)

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

    public String getString() {
        int day = date&31;
        int month = (date>>5)&15;
        int year = (date>>9)&127;
        return day + "-" + month + "-" + (year+2000);
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

    public String getWeekday(int weekday) {
        switch(weekday) {
            case 1: return App.getContext().getResources().getString(R.string.weekday_mon);
            case 2: return App.getContext().getResources().getString(R.string.weekday_tue);
            case 3: return App.getContext().getResources().getString(R.string.weekday_wed);
            case 4: return App.getContext().getResources().getString(R.string.weekday_thu);
            case 5: return App.getContext().getResources().getString(R.string.weekday_fri);
            case 6: return App.getContext().getResources().getString(R.string.weekday_sat);
            case 7: return App.getContext().getResources().getString(R.string.weekday_sun);
            default: return "";
        }
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
                return 28;
            case 4:
            case 6:
            case 9:
            case 11:
            default:
                return 30;
        }
    }

    public int getDaysOfMonth_LeapYear(int month) {
        if(month==2) {
            return 29;
        }
        return getDaysOfMonth(month);
    }

    public int getDaysUntilMonth(int month) {
        int days;
        switch(month) {
            case 1: return 0;
            case 2: return 31;
            case 3: days = 59;
            case 4: days = 90;
            case 5: days = 120;
            case 6: days = 151;
            case 7: days = 181;
            case 8: days = 212;
            case 9: days = 243;
            case 10: days = 273;
            case 11: days = 304;
            case 12: days = 334;
            default: days = 0;
        }
        if(leapYear()) {
            ++days;
        }
        return days;
    }

    public int getDaysOfYear() {
        return (leapYear() ? 366 : 365);
    }

    public int getDayOfWeek() {
        int day = date&31;
        int month = (date>>5)&15;
        int year = (date>>9)&127;

        int yd = (year/4)*(3*365+366) + (year%4)*365;
        int md = getDaysUntilMonth(month);

        int days = yd + md + day;
        return (days%7);
    }
}