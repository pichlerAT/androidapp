package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Time;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Data {

    public static final char MOST_RECENT_SAVE_FILE_VERSION = 1;

    public static Manager<Category> Categories = new Manager<>();

    public static Manager<Tag> Tags = new Manager<>();

    public static Manager<Tasklist> Tasklists = new Manager<>();

    public static class Timetable {

        public static Manager<TimetableEntry> Entries = new Manager<>();

        public static ShareList Shares = new ShareList(MySQL.TYPE_CALENDAR, User.getId());

        public static ArrayList<Event> getEvents(final Date start, final Date end) {
            ArrayList<Event> list = new ArrayList<>();
            for(TimetableEntry ent : Timetable.Entries.getList()) {
                ent.addEventsToList(list, start, end);
            }
            return list;
        }

    }

    public static class create {

        public static Category Category(String name, int color) {
            Logger.Log("TimetableCategory", "create(String)");
            Category cat = new Category(0, 0, name, color);
            Categories.add(cat);
            cat.create();
            return cat;
        }

        public static Tasklist Tasklist(Category category, String name, int color) {
            Logger.Log("Tasklist", "create(String)");
            Tasklist tl = new Tasklist(0, 0, category, name, (byte) 0, color);
            Tasklists.add(tl);
            tl.create();
            return tl;
        }

        public static TimetableEntry TimetableEntry(Category category, String title, String description, Date date_start, Date date_end,
                                                          Time time_start, Time time_end, int color, Date date_repeat_until, short intervall, short... additions) {

            if(date_repeat_until == null) {
                return TimetableEntry(category, title, description, date_start, date_end, time_start, time_end, color, (short)0, intervall, additions);
            }

            short addition = TimetableEntry.REPEAT_UNTIL;
            for (short a : additions) {
                addition |= a;
            }

            return TimetableEntry(category, title, description, date_start, date_end, time_start, time_end, color, date_repeat_until.getShort(), intervall, addition);
        }

        public static TimetableEntry TimetableEntry(Category category, String title, String description, Date date_start, Date date_end,
                                                          Time time_start, Time time_end, int color, short repetitions, short intervall, short... additions) {
            Logger.Log("TimetableEntry", "create(byte,String,String,DateSpan,TimetableCategory)");

            short addition = 0;

            for (short a : additions) {
                addition |= a;
            }

            int duration = time_end.time - time_start.time + Time.MAX_TIME * date_start.getDaysUntil(date_end);

            TimetableEntry ent = new TimetableEntry(0, 0, category, title, description, date_start.getShort(), time_start.time, duration, addition, repetitions, intervall, color, 0);

            Timetable.Entries.add(ent);
            ent.create();
            return ent;
        }

        public static Tag Tag(Category category, String title, String description, Date date_start, Time time_start, int duration, Date date_end, short intervall, short... additions) {
            short addition = TimetableEntry.REPEAT_UNTIL;
            for (short a : additions) {
                addition |= a;
            }

            return Tag(category, title, description, date_start, time_start, duration, date_end.getShort(), intervall, addition);
        }

        public static Tag Tag(Category category, String title, String description, Date date_start, Time time_start, int duration, Date date_end, short intervall, int color, short... additions) {
            short addition = TimetableEntry.REPEAT_UNTIL;
            for (short a : additions) {
                addition |= a;
            }

            return Tag(category, title, description, date_start, time_start, duration, date_end.getShort(), intervall, color, addition);
        }

        public static Tag Tag(Category category, String title, String description, Date date_start, Time time_start, int duration, short repetitions, short intervall, short... additions) {
            short addition = 0;
            for (short a : additions) {
                addition |= a;
            }

            Tag tag = new Tag(category, title, description, date_start, time_start, duration, addition, repetitions, intervall, false, 0);

            Tags.add(tag);
            tag.create();
            return tag;
        }

        public static Tag Tag(Category category, String title, String description, Date date_start, Time time_start, int duration, short repetitions, short intervall, int color, short... additions) {
            short addition = 0;
            for (short a : additions) {
                addition |= a;
            }

            Tag tag = new Tag(category, title, description, date_start, time_start, duration, addition, repetitions, intervall, true, color);

            Tags.add(tag);
            tag.create();
            return tag;
        }

    }

    public static Category getCategoryByName(String name) {
        for(Category cat : Categories.getList()) {
            if(cat.name.equals(name)) {
                return cat;
            }
        }
        return null;
    }

    public static void load() {
        User.loadLogin();
        FryFile fry = getFryFile();
        ContactList.resetData();
        resetData();

        if(fry == null || fry.size() <= 0) {
            return;
        }

        switch(fry.getVersion()) {
            case 1: load_v1(fry); break;
        }
    }

    public static void save() {
        Logger.Log("App", "saveData()");
        FryFile fry = new FryFile.Compact();

        fry.writeChar(MOST_RECENT_SAVE_FILE_VERSION);

        if(User.isLocal()) {
            User.deleteLogin();

        }else {
            User.saveLogin();
            User.encode(fry);
        }

        save_v1(fry);

        try {
            FileOutputStream outputStream = App.getFileOutputStream(User.getFileName());

            if (!fry.saveToStream(outputStream)) {
                Logger.Log("App#save()", "Could not save local file: FryFile.save() = false");
                System.out.println("# Could not SAVE local file");
            }

        }catch (FileNotFoundException ex) {
            //ex.printStackTrace();
            Logger.Log("App#save()", "Could not save local file: file not found");
            System.out.println("# Could not SAVE local file");
        }
    }

    protected static FryFile getFryFile() {
        Logger.Log("App", "getFryFile()");
        if(User.isLocal()) {
            return getLocalFryFile();
        }

        FileInputStream inputStream;
        try {
            inputStream = App.getFileInputStream(User.getFileName());
        }catch(FileNotFoundException ex) {
            System.out.println("# NO USER FILE: " + User.getEmail());
            //ex.printStackTrace();
            return getLocalFryFile();
        }

        FryFile fry = new FryFile.Compact();
        if(!fry.loadFromStream(inputStream)) {
            Logger.Log("App#load()","Could not load local file");
            System.out.println("# Could not LOAD local file");
            return getLocalFryFile();
        }

        fry.setVersion(fry.getChar());

        if(User.decode(fry)) {
            System.out.println("# SUCCESSFULLY LOADED USER DATA");
            return fry;

        }else if(User.isOnline()) {
            System.out.println("# SUCCESSFULLY LOADED USER DATA");
            return fry;
        }

        return getLocalFryFile();
    }

    protected static void load_v1(FryFile fry) {
        App.Settings.readFrom(fry);
        ContactList.readFrom(fry);
        Data.readFrom(fry);
    }

    protected static void save_v1(FryFile fry) {
        App.Settings.writeTo(fry);
        ContactList.writeTo(fry);
        Data.writeTo(fry);
    }

    protected static FryFile getLocalFryFile() {
        try {
            FileInputStream inputStream = App.getFileInputStream(User.getFileName());

            FryFile fry = new FryFile.Compact();
            if (fry.loadFromStream(inputStream)) {

                fry.setVersion(fry.getChar());

                System.out.println("# SUCCESSFULLY LOADED LOCAL DATA");

                return fry;
            }
        }catch (FileNotFoundException ex) {
            System.out.println("# NO LOCAL FILE");
            //ex.printStackTrace();
        }
        return null;
    }

    protected static void writeTo(FryFile fry) {
        Categories.writeTo(fry);
        Tasklists.writeTo(fry);
        Timetable.Entries.writeTo(fry);
        Timetable.Shares.writeTo(fry);
        Tags.writeTo(fry);
    }

    protected static void resetData() {
        Categories = new Manager<>();
        Tasklists = new Manager<>();
        Timetable.Entries = new Manager<>();
        Timetable.Shares = new ShareList(MySQL.TYPE_CALENDAR, User.getId());
        Tags = new Manager<>();
    }

    protected static void readFrom(FryFile fry) {
        int count;


        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            Categories.add(new Category(fry));
        }

        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            Categories.addBackup(new Category(fry));
        }


        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            Tasklists.add(new Tasklist(fry));
        }

        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            Tasklists.addBackup(new Tasklist(fry));
        }


        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            Timetable.Entries.add(new TimetableEntry(fry));
        }

        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            Timetable.Entries.addBackup(new TimetableEntry(fry));
        }

        Timetable.Shares.readFrom(fry);


        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            Tags.add(new Tag(fry));
        }

        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            Tags.addBackup(new Tag(fry));
        }

        Categories.optimizeIds();
        Tasklists.optimizeIds();
        Timetable.Entries.optimizeIds();
        Tags.optimizeIds();
    }

    protected static void synchronizeFromMySQL(FryFile fry) {
        ArrayList<Category> catList = new ArrayList<>();
        int count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            catList.add(new Category(fry));
        }
        Categories.synchronizeWith(catList);

        ArrayList<Tasklist> taskList = new ArrayList<>();
        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            taskList.add(new Tasklist(fry));
        }
        Tasklists.synchronizeWith(taskList);

        ArrayList<TimetableEntry> entList = new ArrayList<>();
        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            entList.add(new TimetableEntry(fry));
        }
        Timetable.Entries.synchronizeWith(entList);

        Timetable.Shares = new ShareList(MySQL.TYPE_CALENDAR, User.getId());
        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            Timetable.Shares.addStorage(fry.getByte(), fry.getUnsignedInt(), fry.getUnsignedInt());
        }

        ArrayList<Tag> tagList = new ArrayList<>();
        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            tagList.add(new Tag(fry));
        }
        Tags.synchronizeWith(tagList);
    }

    /*


    public static void synchronizeWithGoogleCalendar() {
        String[] FIELDS = {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DISPLAY_COLOR,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DURATION,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.EVENT_TIMEZONE
        };
    }

    public static void printAndroidCalendar() {
        String[] FIELDS = {
                CalendarContract.Events._SYNC_ID,
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DISPLAY_COLOR,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DURATION,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.EVENT_TIMEZONE
        };

        Uri uri = Uri.parse("content://com.android.calendar/events");

        Cursor cursor = App.getContext().getContentResolver().query(uri, FIELDS, null, null, null);

        if(cursor == null) {
            System.out.println("CURSOR IS NULL");
            return;
        }

        int columns = cursor.getColumnCount();

        System.out.print(cursor.getCount());

        for(int i=0; i<columns; ++i) {
            System.out.print(" | " + cursor.getColumnName(i));
        }
        System.out.println();

        System.out.println("---------------------------------------------------------------------");
        cursor.moveToFirst();

        do {
            System.out.print(cursor.getPosition());

            for(int i=0; i<columns; ++i) {
                System.out.print(" | " + cursor.getString(i));
            }

            System.out.println();
        } while(cursor.moveToNext());

        /*
        TimeZone tz = TimeZone.getTimeZone("Europe/Amsterdam");
        System.out.println(tz.getDisplayName() + " = " + tz.getRawOffset());
        tz = TimeZone.getTimeZone("UTC+1");
        System.out.println(tz.getDisplayName() + " = " + tz.getRawOffset());
        tz = TimeZone.getTimeZone("GMT+1");
        System.out.println(tz.getDisplayName() + " = " + tz.getRawOffset());
        */
    /*
        System.out.println(TimeZone.getDefault().getDisplayName() + " = " + TimeZone.getDefault().getRawOffset());

        cursor.close();
    }

     */




}
