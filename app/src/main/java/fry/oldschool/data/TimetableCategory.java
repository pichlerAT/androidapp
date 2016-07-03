package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class TimetableCategory extends MySQL implements Fryable {

    protected String name;

    protected ArrayList<TimetableEntry> offline_entries = new ArrayList<>();

    public static TimetableCategory create(String name) {
        TimetableCategory cat = new TimetableCategory(0,USER_ID,name);
        Timetable.categories.add(cat);
        ConnectionManager.add(cat);
        return cat;
    }

    protected TimetableCategory() {
        super(TYPE_CALENDAR_CATEGORY, 0, 0);
    }

    protected TimetableCategory(int id,int user_id,String name) {
        super(TYPE_CALENDAR_CATEGORY, id, user_id);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TimetableCategory) {
            TimetableCategory c = (TimetableCategory) o;
            return (c.id == id && c.name.equals(name));
        }
        return false;
    }

    @Override
    public TimetableCategory backup() {
        return new TimetableCategory(id, user_id, name);
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
    protected void synchronize(MySQL mysql) {
        TimetableCategory c = (TimetableCategory) mysql;
        name = c.name;
    }

    @Override
    public boolean canEdit() {
        return isOwner();
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

    public String getName() {
        return name;
    }

    protected void addOfflineEntry(TimetableEntry entry) {
        offline_entries.add(entry);
    }

    protected String getUpdateString() {
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
        OfflineEntry.update(this);
    }

    public void delete() {
        OfflineEntry.delete(this);
    }

}
