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
        String[] r = date.split("_");
        int year = Integer.parseInt(r[2]) - 2000;
        int month = Integer.parseInt(r[1]);
        int day = Integer.parseInt(r[0]);
        this.date = (short)( day + (month<<5) + (year<<9) );
    }

    public String getString() {
        int day = date&31;
        int month = (date>>5)&15;
        int year = (date>>9)&127;
        return day + "_" + month + "_" + (year+2000);
    }

}