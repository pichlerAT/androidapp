package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.DateTime;
import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Time;

public class Timetable {

    public static ArrayList<TimetableCategory> categories = new ArrayList<>();

    protected static ArrayList<TimetableCategory> categoriesBackup = new ArrayList<>();

    protected static ArrayList<TimetableEntry> entries = new ArrayList<>();

    protected static ArrayList<TimetableEntry> entriesBackup = new ArrayList<>();

    public static void writeTo(FryFile file) {
        file.write(categories.toArray());
        file.write(categoriesBackup.toArray());
        file.write(entries.toArray());
        file.write(entriesBackup.toArray());
    }

    public static void readFrom(FryFile fry) {
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
            categoriesBackup.add(cat);
        }

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
            entriesBackup.add(ent);
        }
    }

    public static void synchronizeFromMySQL(String... r) {
        int index = 0;

        boolean[] isOnline = new boolean[categories.size()];
        int NoCategories = Integer.parseInt(r[index++]);
        for(int i=0; i<NoCategories; ++i) {
            TimetableCategory on = new TimetableCategory(Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),r[index++]);

            if(!ConnectionManager.hasEntry(MySQL.TYPE_CALENDAR_CATEGORY | MySQL.BASETYPE_DELETE, on.id)) {

                int off_index = getCategoryIndexById(on.id);
                if (off_index < 0) {
                    categories.add(on);
                }else {
                    // TODO Stefan: calendar_category synchronization
                    isOnline[off_index] = true;
                }
            }
        }
        for(int i=isOnline.length-1; i>=0; --i) {
            if(!isOnline[i] && categories.get(i).id > 0) {
                categories.remove(i);
            }
        }

        isOnline = new boolean[entries.size()];
        while(index < r.length) {
            TimetableEntry on = new TimetableEntry(Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),Integer.parseInt(r[index++]),
                    r[index++],r[index++],Short.parseShort(r[index++]),Short.parseShort(r[index++]),Integer.parseInt(r[index++]),Byte.parseByte(r[index++]));

            int off_index = getEntryIndexById(on.id);
            if(off_index < 0) {
                entries.add(on);
            }else {
                // TODO Stefan: calendar_entry synchronization
                isOnline[off_index] = true;
            }
        }
        for(int i=isOnline.length-1; i>=0; --i) {
            if(!isOnline[i] && entries.get(i).id > 0) {
                entries.remove(i);
            }
        }
    }

    public static ArrayList<TimetableEntry> getEntries() {
        ArrayList<TimetableEntry> list = new ArrayList<>(entries);
        for(TimetableCategory cat : categories) {
            for(TimetableEntry ent : cat.offline_entries) {
                list.add(ent);
            }
        }
        return list;
    }

    public static int getEntryIndexById(int id) {
        for(int i=0; i<entries.size(); ++i) {
            if(entries.get(i).id == id) {
                return i;
            }
        }
        return -1;
    }

    public static TimetableEntry getEntryById(int id) {
        for(TimetableEntry e : entries) {
            if(e.id == id) {
                return e;
            }
        }
        return null;
    }

    public static ArrayList<TimetableEntry> getEntriesOfCategoryIds(int... ids) {
        ArrayList<TimetableEntry> entr = new ArrayList<>();
        for(TimetableEntry ent : entries) {
            for(int id : ids) {
                if (ent.category_id == id) {
                    entr.add(ent);
                }
            }
        }
        return entr;
    }

    public static int getCategoryIndexById(int id) {
        for(int i=0; i<categories.size(); ++i) {
            if(categories.get(i).id == id) {
                return i;
            }
        }
        return -1;
    }

    public static TimetableCategory getCategoryById(int id) {
        for(TimetableCategory cat : categories) {
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