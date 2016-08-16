package com.frysoft.notifry.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Time;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

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
            EventIterator it;
            Event e;
            for(TimetableEntry ent : Timetable.Entries.getList()) {
                it = new EventIterator(ent, start, end);
                while((e = it.next()) != null) {
                    list.add(e);
                }
            }
            return list;
        }

        public static void synchronizeAndroidCalendarIntoNotifry() {
            ConnectionManager.synchronizeAndroidCalendar();
        }

        protected static TimetableEntry getByGoogleId(String google_id) {
            for(TimetableEntry ent : Entries.getList()) {
                if(ent.google_id.equals(google_id)) {
                    return ent;
                }
            }
            return null;
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
                                                    Time time_start, Time time_end, int color, RRule rRule) {
            Logger.Log("TimetableEntry", "create(byte,String,String,DateSpan,TimetableCategory)");

            if(title == null) {
                throw new IllegalArgumentException("title must not be null");
            }

            if(date_start == null) {
                throw new IllegalArgumentException("date_start must not be null");
            }

            if(description == null || description.length() == 0) {
                description = " ";
            }

            if(rRule == null) {
                rRule = new RRule();

                if(time_start == null && time_end == null) {
                    rRule.setWholeDay(true);
                }
            }

            if(time_start == null) {
                time_start = new Time(Time.MIN_TIME);
            }

            if(date_end == null) {
                date_end = new Date(date_start);
            }

            if(time_end == null) {
                time_end = new Time(Time.MAX_TIME - 1);
            }

            TimetableEntry ent = new TimetableEntry(0, 0, category, title, description, date_start.getShort(), time_start.time, date_end.getShort(), time_end.time, rRule, color, "E");

            Timetable.Entries.add(ent);
            ent.create();
            return ent;
        }

        public static Tag Tag(Category category, String title, String description, Date date_start, Time time_start, Date date_end, Time time_end, RRule rRule) {

            if(rRule == null) {
                rRule = new RRule();
            }

            Tag tag = new Tag(category, title, description, date_start, time_start, date_end, time_end, rRule, false, 0);

            Tags.add(tag);
            tag.create();
            return tag;
        }

        public static Tag Tag(Category category, String title, String description, Date date_start, Time time_start, Date date_end, Time time_end, RRule rRule, int color) {

            if(rRule == null) {
                rRule = new RRule();
            }

            Tag tag = new Tag(category, title, description, date_start, time_start, date_end, time_end, rRule, true, color);

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
        for (int i = 0; i < count; ++i) {
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
            Timetable.Shares.addStorage(fry.getUnsignedByte(), fry.getUnsignedInt(), fry.getUnsignedInt());
        }

        ArrayList<Tag> tagList = new ArrayList<>();
        count = fry.getArrayLength();
        for(int i=0; i<count; ++i) {
            tagList.add(new Tag(fry));
        }
        Tags.synchronizeWith(tagList);
    }

    protected static void synchronizeAndroidCalendar() {
        System.out.println("# ANDROID CALENDAR SYNC START");
        long time = Calendar.getInstance().getTimeInMillis();

        String[] FIELDS = {
                CalendarContract.Events._SYNC_ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DISPLAY_COLOR,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.EVENT_TIMEZONE,
                CalendarContract.Events.RRULE
        };

        Uri uri = Uri.parse("content://com.android.calendar/events");

        Cursor cursor = App.getContext().getContentResolver().query(uri, FIELDS, null, null, null);

        if(cursor == null) {
            System.out.println("# CURSOR IS NULL");
            return;
        }

        if(cursor.getCount() == 0) {
            System.out.println("# CURSOR COUNT IS 0");
            return;
        }

        cursor.moveToFirst();
        ArrayList<TimetableEntry> list = new ArrayList<>();

        do {
            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            int color = cursor.getInt(3);
            long start = cursor.getLong(4);
            long end = cursor.getLong(5);
            boolean allDay = (cursor.getInt(6) == 1);
            String tz = cursor.getString(7);
            String rrule = cursor.getString(8);


            RRule rRule;
            if(rrule == null) {
                rRule = new RRule();
            }else {
                rRule = RRule.convertIOracleToFrySoft("RRULE:" + rrule);
            }

            Calendar cal = Calendar.getInstance();

            cal.setTimeInMillis(start);
            Date date_start = new Date(cal);
            Time time_start = new Time(cal);
            if(!rRule.wholeDay) {
                int timeZoneOffset = (cal.getTimeZone().getRawOffset() / 60000);
                date_start.addDays(time_start.subtractMinutes(timeZoneOffset));
            }

            cal.setTimeInMillis(end);
            Date date_end = new Date(cal);
            Time time_end = new Time(cal);
            if(!rRule.wholeDay) {
                int timeZoneOffset = (cal.getTimeZone().getRawOffset() / 60000);
                date_end.addDays(time_end.subtractMinutes(timeZoneOffset));
            }

            if(title == null) {
                continue;
            }

            if(description == null || description.length() == 0) {
                description = " ";
            }

            TimetableEntry android = new TimetableEntry(0, 0, null, title, description, date_start.getShort(), time_start.time, date_end.getShort(), time_end.time, rRule, color, id);

            if(android.google_id == null) {
                System.out.println("# GOOGLE_ID == NULL : "+android.title);
                android.google_id = "E";
                Timetable.Entries.add(android);
                android.create();
                continue;
            }

            TimetableEntry notifry = Timetable.getByGoogleId(android.google_id);

            if (notifry == null) {
                Timetable.Entries.add(android);
                android.create();

            } else if (!android.equals(notifry)) {
                notifry.synchronize(android);
            }

        } while(cursor.moveToNext());

        cursor.close();

        Timetable.Entries.list.recreateBackup();

        int dt = (int)(Calendar.getInstance().getTimeInMillis() - time) / 1000;
        int s = dt % 60;
        dt /= 60;
        int m = dt % 60;
        int h = dt / 60;
        System.out.println("# ANDROID CALENDAR SYNC END, TOTAL TIME : "+h+"h "+m+"min "+s+"sec");
    }

}
