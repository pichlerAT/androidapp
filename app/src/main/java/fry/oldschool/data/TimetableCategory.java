package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;

public class TimetableCategory extends OfflineEntry {

    public int user_id;

    public String name;

    public ArrayList<TimetableEntry> offline_entries = new ArrayList<>();

    public static TimetableCategory create(String name) {
        TimetableCategory cat = new TimetableCategory(0,USER_ID,name);
        Timetable.categories.add(cat);
        return cat;
    }

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
    public void writeTo(FryFile file) {
        file.write(id);
        file.write(user_id);
        file.write(name);
        file.write(offline_entries.toArray());
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
