package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.Date;
import fry.oldschool.utils.DateSpan;
import fry.oldschool.utils.FryFile;

public class Timetable {

    protected static BackupList<TimetableCategory> categories = new BackupList<>();

    protected static BackupList<TimetableEntry> entries = new BackupList<>();

    public static void writeTo(FryFile file) {
        file.write(categories.getList());
        file.write(categories.getBackupList());
        file.write(entries.getList());
        file.write(entries.getBackupList());
    }

    public static void readFrom(FryFile fry) {
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

    public static void synchronizeFromMySQL(String... r) {
        int index = 0;

        ArrayList<TimetableCategory> catList = new ArrayList<>();
        int NoCategories = Integer.parseInt(r[index++]);
        for(int i=0; i<NoCategories; ++i) {
            catList.add(new TimetableCategory(Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),r[index++]));
        }
        categories.synchronizeWith(catList);

        ArrayList<TimetableEntry> entList = new ArrayList<>();
        while(index < r.length) {
            entList.add(new TimetableEntry(Integer.parseInt(r[index++]), Integer.parseInt(r[index++]), Integer.parseInt(r[index++]), r[index++], r[index++],
                                        Short.parseShort(r[index++]), Short.parseShort(r[index++]), Integer.parseInt(r[index++]), Byte.parseByte(r[index++])));
        }
        entries.synchronizeWith(entList);
    }

    public static ArrayList<TimetableEntry> getEntries() {
        ArrayList<TimetableEntry> list = new ArrayList<>(entries.getList());
        for(TimetableCategory cat : categories.getList()) {
            for(TimetableEntry ent : cat.offline_entries) {
                list.add(ent);
            }
        }
        return list;
    }

    public static ArrayList<TimetableEntry> getEntries(int month, int year) {
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

    protected static TimetableEntry getEntryById(int id) {
        for(TimetableEntry e : entries.getList()) {
            if(e.id == id) {
                return e;
            }
        }
        return null;
    }

    public static ArrayList<TimetableCategory> getCategories() {
        return categories.getList();
    }

    protected static TimetableCategory getCategoryById(int id) {
        for(TimetableCategory cat : categories.getList()) {
            if(cat.id == id) {
                return cat;
            }
        }
        return null;
    }

    public static void shareWith(Contact cont) {
        ConnectionManager.add(new Share(MySQL.TYPE_CALENDAR, MySQL.USER_ID, cont));
    }

    public static void shareWith(Contact cont, byte permission) {
        ConnectionManager.add(new Share(MySQL.TYPE_CALENDAR, permission, MySQL.USER_ID, cont));
    }

}