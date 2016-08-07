package com.frysoft.notifry.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.TimeZone;

import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.DateSpan;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.User;

public class Timetable {

    protected static BackupList<Category> categories = new BackupList<>();

    protected static BackupList<TimetableEntry> entries = new BackupList<>();

    public static ShareList shares = new ShareList(MySQL.TYPE_CALENDAR, User.getId());

    public static void writeTo(FryFile file) {
        Logger.Log("Timetable", "writeTo(FryFile)");
        file.write(categories.getList());
        file.write(categories.getBackupList());
        file.write(entries.getList());
        file.write(entries.getBackupList());
    }

    public static void readFrom(FryFile fry) {
        Logger.Log("Timetable", "readFrom(FryFile)");

        categories = new BackupList<>();

        int NoCategories = fry.getChar();
        for(int i=0; i<NoCategories; ++i) {
            categories.add(new Category(fry));
        }

        NoCategories = fry.getChar();
        for(int i=0; i<NoCategories; ++i) {
            categories.addBackup(new Category(fry));
        }

        entries = new BackupList<>();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            entries.add(new TimetableEntry(fry));
        }

        NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            entries.addBackup(new TimetableEntry(fry));
        }
    }

    public static void synchronizeSharesFromMySQL(FryFile fry) {
        Logger.Log("Timetable", "synchronizeSharesFromMySQL(String[])");

        int NoShares = fry.getArrayLength();
        for(int i=0; i<NoShares; ++i) {
            shares.addStorage(fry.getByte(), fry.getInt(), fry.getInt());
        }
    }

    public static void synchronizeCategoriesFromMySQL(FryFile fry) {
        Logger.Log("Timetable", "synchronizeCategoriesFromMySQL(String[])");

        ArrayList<Category> catList = new ArrayList<>();

        int NoCategories = fry.getArrayLength();
        for(int i=0; i<NoCategories; ++i) {
            Category cat = new Category(fry.getInt(), fry.getInt(), fry.getString(), fry.getInt());

            int NoShares = fry.getArrayLength();
            for(int k=0; k<NoShares; ++k) {
                cat.shares.addStorage(fry.getByte(), fry.getInt(), fry.getInt());
            }

            catList.add(cat);
        }
        categories.synchronizeWith(catList);
    }

    public static void synchronizeEntriesFromMySQL(FryFile fry) {
        Logger.Log("Timetable", "synchronizeEntriesFromMySQL(String[])");

        ArrayList<TimetableEntry> entList = new ArrayList<>();

        int NoEntries = fry.getArrayLength();
        for(int i=0; i<NoEntries; ++i) {
            TimetableEntry ent = new TimetableEntry(fry.getInt(), fry.getInt(), fry.getInt(),
                    fry.getString(),fry.getString(), fry.getShort(), fry.getShort(), fry.getInt(),
                    fry.getShort(), fry.getInt(), fry.getInt(), fry.getShort(), fry.getByte());

            int NoShares = fry.getArrayLength();
            for(int k=0; k<NoShares; ++k) {
                ent.shares.addStorage(fry.getByte(), fry.getInt(), fry.getInt());
            }

            entList.add(ent);
        }
        entries.synchronizeWith(entList);
    }

    public static ArrayList<TimetableEntry> getEntries() {
        Logger.Log("Timetable", "getEntries()");
        ArrayList<TimetableEntry> list = new ArrayList<>(entries.getList());
        for(Category cat : categories.getList()) {
            for(TimetableEntry ent : cat.offline_entries) {
                list.add(ent);
            }
        }
        return list;
    }

    public static ArrayList<Event> getEvents(final Date start, final Date end) {
        ArrayList<Event> list = new ArrayList<>();

        for(TimetableEntry ent : entries.getList()) {
            ent.addEventsToList(list, start.getShort(), end.getShort());
        }
        for(Category cat : categories.getList()) {
            for(TimetableEntry ent : cat.offline_entries) {
                ent.addEventsToList(list, start.getShort(), end.getShort());
            }
        }

        return list;
    }

    /*
    public static ArrayList<TimetableEntry> getEntries(int month, int year) {
        Logger.Log("Timetable", "getEntries(int,int)");
        ArrayList<TimetableEntry> list = new ArrayList<>();
        DateSpan span = new DateSpan(new Date(1, month, year), new Date(Date.getDaysOfMonth(year, month), month, year));

        for(TimetableEntry ent : entries.getList()) {
            if(ent.isSpanOverlapping(span)) {
                list.add(ent);
            }
        }
        for(Category cat : categories.getList()) {
            for(TimetableEntry ent : cat.offline_entries) {
                if(ent.isSpanOverlapping(span)) {
                    list.add(ent);
                }
            }
        }

        return list;
    }
    */

    /*
    public static ArrayList<TimetableEntry> getEntries(int day, int month, int year) {
        Logger.Log("Timetable", "getEntries(int,int,int)");
        ArrayList<TimetableEntry> list = new ArrayList<>();
        Date date = new Date(day, month, year);

        for(TimetableEntry ent : entries.getList()) {
            if(ent.isDateInsideSpan(date)) {
                list.add(ent);
            }
        }
        for(Category cat : categories.getList()) {
            for(TimetableEntry ent : cat.offline_entries) {
                if(ent.isDateInsideSpan(date)) {
                    list.add(ent);
                }
            }
        }

        return list;
    }
    */

    public static TimetableEntry getEntryById(int id) {
        Logger.Log("Timetable", "getEntryById(int)");
        for(TimetableEntry e : entries.getList()) {
            if(e.id == id) {
                return e;
            }
        }
        return null;
    }

    public static TimetableEntry getEntryByGoogleId(int id) {
        for(TimetableEntry e : entries.getList()) {
            if(e.google_id == id) {
                return e;
            }
        }
        return null;
    }

    public static ArrayList<Category> getCategories() {
        Logger.Log("Timetable", "getCategories()");
        return categories.getList();
    }

    protected static Category getCategoryById(int id) {
        Logger.Log("Timetable", "getCategoryById(int)");
        for(Category cat : categories.getList()) {
            if(cat.id == id) {
                return cat;
            }
        }
        return null;
    }

    public static Category getCategoryByName(String name) {
        Logger.Log("Timetable", "getCategoryByName(String)");
        for(Category cat : categories.getList()) {
            if(cat.name.equals(name)) {
                return cat;
            }
        }
        return null;
    }

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
        System.out.println(TimeZone.getDefault().getDisplayName() + " = " + TimeZone.getDefault().getRawOffset());

        cursor.close();
    }

}