package fry.oldschool.data;

import java.util.ArrayList;

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
            TimetableCategory cat = new TimetableCategory();
            cat.readFrom(fry);
            categories.add(cat);
        }

        NoCategories = fry.getChar();
        for(int i=0; i<NoCategories; ++i) {
            TimetableCategory cat = new TimetableCategory();
            cat.readFrom(fry);
            categories.addBackup(cat);
        }

        entries = new BackupList<>();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            TimetableEntry ent = new TimetableEntry();
            ent.readFrom(fry);
            entries.add(ent);
        }

        NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            TimetableEntry ent = new TimetableEntry();
            ent.readFrom(fry);
            entries.addBackup(ent);
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
            entList.add(new TimetableEntry(Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),
                    r[index++],r[index++],Short.parseShort(r[index++]),Short.parseShort(r[index++]),Integer.parseInt(r[index++]),Byte.parseByte(r[index++])));
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
        ConnectionManager.add(new Share(MySQL.TYPE_CALENDAR, MySQL.USER_ID, permission, cont));
    }

}