package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class TimetableCategory extends OnlineEntry implements Fryable {

    public int user_id;

    public String name;

    public ArrayList<TimetableEntry> offline_entries = new ArrayList<>();

    public static TimetableCategory create(String name) {
        TimetableCategory cat = new TimetableCategory(0,USER_ID,name);
        Timetable.categories.add(cat);
        return cat;
    }

    protected TimetableCategory() { }

    protected TimetableCategory(int id,int user_id,String name) {
        type = TYPE_CALENDAR_CATEGORY;
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        if(id == 0) {
            ConnectionManager.add(this);
        }
    }

    @Override
    protected boolean mysql() {
        String resp = getLine(DIR_CALENDAR_CATEGORY + "create.php","&name=" + name);
        if(resp != null) {
            id = Integer.parseInt(resp);
            for(int i=offline_entries.size()-1; i>=0; --i) {
                TimetableEntry ent = offline_entries.remove(i);
                ent.category_id = id;
                ConnectionManager.add(ent);
            }
            return true;
        }
        return false;
    }

    @Override
    public void writeTo(FryFile fry) {
        fry.write(id);
        fry.write(user_id);
        fry.write(name);
        fry.write(offline_entries.toArray());
    }

    @Override
    public void readFrom(FryFile fry) {
        id = fry.getInt();
        user_id = fry.getInt();
        name = fry.getString();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            TimetableEntry ent = new TimetableEntry();
            ent.readFrom(fry);
            offline_entries.add(ent);
        }
    }

    public void addOfflineEntry(TimetableEntry entry) {
        offline_entries.add(entry);
    }

    public String getUpdateString() {
        return ("&category_id=" + id + "&name=" + name);
    }

    public void shareWith(Contact cont) {
        ConnectionManager.add(new Share(TYPE_CALENDAR_CATEGORY, id, cont));
    }

    public void shareWith(Contact cont, byte permission) {
        ConnectionManager.add(new Share(TYPE_CALENDAR_CATEGORY, id, permission, cont));
    }

    public void rename(String name) {
        this.name = name;
        ConnectionManager.add(new Update(TYPE_CALENDAR_CATEGORY, id));
    }

    public void delete() {
        ConnectionManager.add(new Delete(TYPE_CALENDAR_CATEGORY, id));
    }

}
