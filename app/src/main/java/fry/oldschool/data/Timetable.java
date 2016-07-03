package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.Date;
import fry.oldschool.utils.DateSpan;
import fry.oldschool.utils.FryFile;

public class Timetable {

    protected static BackupList<TimetableCategory> categories = new BackupList<>();

    protected static BackupList<TimetableEntry> entries = new BackupList<>();

    protected static ShareList shareList = new ShareList(MySQL.TYPE_CALENDAR, MySQL.USER_ID);

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

        int NoShares = Integer.parseInt(r[index++]);
        for(int k=0; k<NoShares; ++k) {
            shareList.add(Byte.parseByte(r[index++]),Integer.parseInt(r[index++]),Integer.parseInt(r[index++]));
        }

        ArrayList<TimetableCategory> catList = new ArrayList<>();
        int NoCategories = Integer.parseInt(r[index++]);
        for(int i=0; i<NoCategories; ++i) {
            TimetableCategory cat = new TimetableCategory(Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),r[index++]);

            NoShares = Integer.parseInt(r[index++]);
            for(int k=0; k<NoShares; ++k) {
                cat.shareList.add(Byte.parseByte(r[index++]),Integer.parseInt(r[index++]),Integer.parseInt(r[index++]));
            }

            catList.add(cat);
        }
        categories.synchronizeWith(catList);

        ArrayList<TimetableEntry> entList = new ArrayList<>();
        while(index < r.length) {
            TimetableEntry ent = new TimetableEntry(Integer.parseInt(r[index++]), Integer.parseInt(r[index++]), Byte.parseByte(r[index++]), Short.parseShort(r[index++]),
                                    Short.parseShort(r[index++]), Integer.parseInt(r[index++]), Integer.parseInt(r[index++]), r[index++], r[index++]);

            NoShares = Integer.parseInt(r[index++]);
            for(int k=0; k<NoShares; ++k) {
                ent.shareList.add(Byte.parseByte(r[index++]),Integer.parseInt(r[index++]),Integer.parseInt(r[index++]));
            }

            entries.add(ent);
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

    public static boolean isSharedWithUserId(int id) {
        return shareList.hasUserId(id);
    }

    public static ArrayList<ContactGroup> getShared() {
        return shareList.getShareList();
    }

    public static void addShare(Contact contact) {
        Share share = new Share(MySQL.TYPE_CALENDAR, MySQL.USER_ID, contact);
        shareList.addShare(share);
        ConnectionManager.add(share);
    }

    public static void addShare(ArrayList<Contact> contacts) {
        for(Contact contact : contacts) {
            addShare(contact);
        }
    }

    public static void addShare(Contact contact,byte permission) {
        Share share = new Share(MySQL.TYPE_CALENDAR, permission, MySQL.USER_ID, contact);
        addShare(share);
        ConnectionManager.add(share);
    }

    public static void addShare(ArrayList<Contact> contacts,byte permission) {
        for(Contact contact : contacts) {
            addShare(contact,permission);
        }
    }

    public static void addShare(ArrayList<Contact> contacts,byte[] permissions) {
        for(int i=0;i<contacts.size();++i) {
            addShare(contacts.get(i),permissions[i]);
        }
    }

    public static void removeShare(Share share) {
        if(shareList.remove(share)) {
            share.delete();
        }
    }

    public static void removeShare(ArrayList<Share> shares) {
        for(Share share : shares) {
            removeShare(share);
        }
    }


}