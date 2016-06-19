package fry.oldschool.data;

import java.util.ArrayList;

public class Timetable {

    protected static ArrayList<TimetableCategory> category = new ArrayList<>();

    protected static ArrayList<TimetableEntry> entry = new ArrayList<>();

    public static String getLocalSaveString() {
        return null;
    }

    public static void recieveLocalSaveString(String line) {

    }

    protected static TimetableEntry findEntryById(int id) {
        for(TimetableEntry e : entry) {
            if(e.id == id) {
                return e;
            }
        }
        return null;
    }

}