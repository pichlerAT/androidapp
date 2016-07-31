package com.frysoft.notifry.data;

import java.util.ArrayList;

import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.DateSpan;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;

public class Timetable {

    protected static BackupList<TimetableCategory> categories = new BackupList<>();

    protected static BackupList<TimetableEntry> entries = new BackupList<>();

    public static ShareList shares = new ShareList(MySQL.TYPE_CALENDAR, MySQL.USER_ID);

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
            categories.add(new TimetableCategory(fry));
        }

        NoCategories = fry.getChar();
        for(int i=0; i<NoCategories; ++i) {
            categories.addBackup(new TimetableCategory(fry));
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

        ArrayList<TimetableCategory> catList = new ArrayList<>();

        int NoCategories = fry.getArrayLength();
        for(int i=0; i<NoCategories; ++i) {
            TimetableCategory cat = new TimetableCategory(fry.getInt(), fry.getInt(), fry.getString(), fry.getInt());

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
            TimetableEntry ent = new TimetableEntry(fry.getInt(), fry.getInt(), fry.getShort(), fry.getShort(),
                    fry.getShort(), fry.getInt(), fry.getInt(), fry.getString(), fry.getString(), fry.getInt());

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
        for(TimetableCategory cat : categories.getList()) {
            for(TimetableEntry ent : cat.offline_entries) {
                list.add(ent);
            }
        }
        return list;
    }

    public static ArrayList<TimetableEntry> getEntries(int month, int year) {
        Logger.Log("Timetable", "getEntries(int,int)");
        ArrayList<TimetableEntry> list = new ArrayList<>();
        DateSpan span = new DateSpan(new Date(1, month, year), new Date(Date.getDaysOfMonth(year, month), month, year));

        for(TimetableEntry ent : entries.getList()) {
            if(ent.isSpanOverlapping(span)) {
                list.add(ent);
            }
        }
        for(TimetableCategory cat : categories.getList()) {
            for(TimetableEntry ent : cat.offline_entries) {
                if(ent.isSpanOverlapping(span)) {
                    list.add(ent);
                }
            }
        }

        return list;
    }

    public static ArrayList<TimetableEntry> getEntries(int day, int month, int year) {
        Logger.Log("Timetable", "getEntries(int,int,int)");
        ArrayList<TimetableEntry> list = new ArrayList<>();
        Date date = new Date(day, month, year);

        for(TimetableEntry ent : entries.getList()) {
            if(ent.isDateInsideSpan(date)) {
                list.add(ent);
            }
        }
        for(TimetableCategory cat : categories.getList()) {
            for(TimetableEntry ent : cat.offline_entries) {
                if(ent.isDateInsideSpan(date)) {
                    list.add(ent);
                }
            }
        }

        return list;
    }

    public static TimetableEntry getEntryById(int id) {
        Logger.Log("Timetable", "getEntryById(int)");
        for(TimetableEntry e : entries.getList()) {
            if(e.id == id) {
                return e;
            }
        }
        return null;
    }

    public static ArrayList<TimetableCategory> getCategories() {
        Logger.Log("Timetable", "getCategories()");
        return categories.getList();
    }

    protected static TimetableCategory getCategoryById(int id) {
        Logger.Log("Timetable", "getCategoryById(int)");
        for(TimetableCategory cat : categories.getList()) {
            if(cat.id == id) {
                return cat;
            }
        }
        return null;
    }

    public static TimetableCategory getCategoryByName(String name) {
        Logger.Log("Timetable", "getCategoryByName(String)");
        for(TimetableCategory cat : categories.getList()) {
            if(cat.name.equals(name)) {
                return cat;
            }
        }
        return null;
    }

}