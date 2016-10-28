package com.frysoft.notifry.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Date;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class Data {

    public static final char MOST_RECENT_SAVE_FILE_VERSION = 2;

    public static Manager<Category> Categories = new Manager<>();

    public static Manager<Tag> Tags = new Manager<>();

    public static Timetable Timetable = new Timetable();

    public static Manager<Tasklist> Tasklists = new Manager<>();

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

        public static TimetableEntry TimetableEntry(Category category, String title, String description, Date start, Date end, int color, RRule rRule) {
            Logger.Log("TimetableEntry", "create(byte,String,String,DateSpan,TimetableCategory)");

            if(title == null) {
                throw new IllegalArgumentException("title must not be null");
            }

            if(start == null) {
                throw new IllegalArgumentException("start must not be null");
            }

            if(description == null || description.length() == 0) {
                description = " ";
            }

            if(rRule == null) {
                rRule = new RRule();

                if(end != null && start.minute == 0 && start.hour == 0 && end.minute == 59 && end.hour == 23) {
                    rRule.setWholeDay(true);
                }
            }

            if(end == null) {
                end = start.copy();
                rRule.setWholeDay(true);
            }

            TimetableEntry ent = new TimetableEntry(0, 0, category, title, description, start.getInt(), end.getInt(), rRule, color, "E");

            Timetable.Entries.add(ent);
            ent.create();
            return ent;
        }

        public static Tag Tag(Category category, String title, String description, Date start, Date end, RRule rRule) {

            if(rRule == null) {
                rRule = new RRule();
            }

            Tag tag = new Tag(category, title, description, start, end, rRule, false, 0);

            Tags.add(tag);
            tag.create();
            return tag;
        }

        public static Tag Tag(Category category, String title, String description, Date start, Date end, RRule rRule, int color) {

            if(rRule == null) {
                rRule = new RRule();
            }

            Tag tag = new Tag(category, title, description, start, end, rRule, true, color);

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

    public static TasklistEntry getTasklistEntryById(int id) {
        for(Tasklist tl : Tasklists.getList()) {
            TasklistEntry ent = tl.getEntryById(id);
            if(ent != null) {
                return ent;
            }
        }
        return null;
    }

    public static Share getCategoryShareById(int id) {
        for(Category cat : Categories.getList()) {
            Share share = cat.shares.getById(id);
            if(share != null) {
                return share;
            }
        }
        return null;
    }

    public static Share getTimetableShareById(int id) {
        return Timetable.shares.getById(id);
    }

    public static Share getTimetableEntryShareById(int id) {
        for(TimetableEntry ent : Timetable.Entries.getList()) {
            Share share = ent.shares.getById(id);
            if(share != null) {
                return share;
            }
        }
        return null;
    }

    public static Share getTasklistShareById(int id) {
        for(Tasklist tl : Tasklists.getList()) {
            Share share = tl.shares.getById(id);
            if(share != null) {
                return share;
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
            ConnectionManager.setReady(true);
            return;
        }

        switch(fry.getVersion()) {
            case 1: load_v1(fry); break;
            case 2: load_v2(fry); break;
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

        save_v(fry);

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

        fry.setVersion(fry.readChar());

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

    protected static void load_v2(FryFile fry) {
        App.Settings.readFrom(fry);
        ContactList.readFrom(fry);
        Data.readFrom(fry);
        ConnectionManager.readFrom(fry);
    }

    protected static void save_v(FryFile fry) {
        App.Settings.writeTo(fry);
        ContactList.writeTo(fry);
        Data.writeTo(fry);
        ConnectionManager.writeTo(fry);
    }
/*
    protected static void save_v1(FryFile fry) {
        App.Settings.writeTo(fry);
        ContactList.writeTo(fry);
        Data.writeTo(fry);
    }
*/
    protected static FryFile getLocalFryFile() {
        try {
            FileInputStream inputStream = App.getFileInputStream(User.getFileName());

            FryFile fry = new FryFile.Compact();
            if (fry.loadFromStream(inputStream)) {

                fry.setVersion(fry.readChar());

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
        ConnectionManager.setReady(false);
        setOfflineIds();
        Categories.writeTo(fry);
        Tasklists.writeTo(fry);
        Timetable.Entries.writeTo(fry);
        Timetable.shares.writeTo(fry);
        Tags.writeTo(fry);
        removeOfflineIds();
        ConnectionManager.setReady(true);
    }

    protected static void resetData() {
        Categories = new Manager<>();
        Tasklists = new Manager<>();
        Timetable = new Timetable();
        Timetable.Entries = new Manager<>();
        Timetable.shares = new ShareList(Timetable);
        Tags = new Manager<>();
    }

    protected static void readFrom(FryFile fry) {
        int count;

        count = fry.readArrayLength();
        for(int i=0; i<count; ++i) {
            Categories.add(new Category(fry));
        }

        count = fry.readArrayLength();
        for (int i = 0; i < count; ++i) {
            Tasklists.add(new Tasklist(fry));
        }

        count = fry.readArrayLength();
        for(int i=0; i<count; ++i) {
            Timetable.Entries.add(new TimetableEntry(fry));
        }

        Timetable.shares.readFrom(fry);

        count = fry.readArrayLength();
        for(int i=0; i<count; ++i) {
            Tags.add(new Tag(fry));
        }

        /*
        Categories.optimizeIds();
        Tasklists.optimizeIds();
        Timetable.Entries.optimizeIds();
        Tags.optimizeIds();
        */

        removeOfflineIds();
        ConnectionManager.setReady(true);
    }

    protected static void removeOfflineIds() {
        for(int i=0; i<Categories.size(); ++i) {
            Category cat = Categories.get(i);
            if(cat.user_id == 0) {
                cat.id = 0;
            }
        }
    }

    protected static void setOfflineIds() {
        if(Categories.size() == 0) {
            return;
        }

        int nextId = 2;
        int minId = 0;
        for(int i=0; i<Categories.size(); ++i) {
            Category cat = Categories.get(i);
            if(minId == 0 || (cat.id != 0 && cat.id < minId)) {
                minId = cat.id;
            }
        }

        for(int i=0; i<Categories.size(); ++i) {
            Category cat = Categories.get(i);
            if(cat.id == 0) {

                if(nextId >= minId) {
                    for (int j = 0; j < Categories.size(); ++j) {
                        if(nextId == Categories.get(j).id) {
                            nextId++;
                            j = 0;
                        }
                    }
                }
                cat.id = nextId++;
            }
        }
    }

    protected static void synchronizeFromMySQL(FryFile fry) {
        Category[] catList = new Category[fry.readArrayLength()];
        for(int i=0; i<catList.length; ++i) {
            catList[i] = new Category(fry);
        }
        Categories.synchronizeWith(catList);

        Tasklist[] taskList = new Tasklist[fry.readArrayLength()];
        for(int i=0; i<taskList.length; ++i) {
            taskList[i] = new Tasklist(fry);
        }
        Tasklists.synchronizeWith(taskList);

        TimetableEntry[] entList = new TimetableEntry[fry.readArrayLength()];
        for(int i=0; i<entList.length; ++i) {
            entList[i] = new TimetableEntry(fry);
        }
        Timetable.Entries.synchronizeWith(entList);

        Timetable.shares = new ShareList(Timetable);
        Timetable.shares.readFrom(fry);

        Tag[] tagList = new Tag[fry.readArrayLength()];
        for(int i=0; i<tagList.length; ++i) {
            tagList[i] = new Tag(fry);
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
            Date d_start = new Date(cal);

            cal.setTimeInMillis(end);
            Date d_end = new Date(cal);

            if(!rRule.wholeDay) {
                int timeZoneOffset = (cal.getTimeZone().getRawOffset() / 60000);
                d_start.subtractMinutes(timeZoneOffset);
                d_end.subtractMinutes(timeZoneOffset);
            }

            if(title == null) {
                continue;
            }

            if(description == null || description.length() == 0) {
                description = " ";
            }

            TimetableEntry android = new TimetableEntry(0, 0, null, title, description, d_start.getInt(), d_end.getInt(), rRule, color, id);

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
                notifry.sync(android);
            }

        } while(cursor.moveToNext());

        cursor.close();

        int dt = (int)(Calendar.getInstance().getTimeInMillis() - time) / 1000;
        int s = dt % 60;
        dt /= 60;
        int m = dt % 60;
        int h = dt / 60;
        System.out.println("# ANDROID CALENDAR SYNC END, TOTAL TIME : "+h+"h "+m+"min "+s+"sec");
    }

}
