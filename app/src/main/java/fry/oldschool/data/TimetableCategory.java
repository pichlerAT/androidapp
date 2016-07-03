package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class TimetableCategory extends MySQLEntry implements Fryable {

    protected String name;

    protected ArrayList<TimetableEntry> offline_entries = new ArrayList<>();

    public static TimetableCategory create(String name) {
        TimetableCategory cat = new TimetableCategory(0,USER_ID,name);
        cat.create();
        Timetable.categories.add(cat);
        return cat;
    }

    protected TimetableCategory(FryFile fry) {
        super(fry);

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            offline_entries.add(new TimetableEntry(fry));
        }
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
    protected boolean mysql_create() {
        String resp = getLine(DIR_CALENDAR_CATEGORY + "create.php", "&name="+name);
        if(resp != null) {
            id = Integer.parseInt(resp);

            for(TimetableEntry ent : offline_entries) {
                ent.category_id = id;
                ent.create();
            }
            offline_entries = new ArrayList<>();
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        return (getLine(DIR_CALENDAR_CATEGORY + "update.php", "&category_id="+id+"&name="+name) != null);
    }

    @Override
    protected boolean mysql_delete() {
        return (getLine(DIR_CALENDAR_CATEGORY + "delete.php", "&id="+id) != null);
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
        super.writeTo(fry);
        fry.write(name);
        fry.write(offline_entries);
    }

    public String getName() {
        return name;
    }

    protected void addOfflineEntry(TimetableEntry entry) {
        offline_entries.add(entry);
    }

    public void shareWith(Contact cont) {
        ConnectionManager.add(new Share(TYPE_CALENDAR_CATEGORY, id, cont));
    }

    public void shareWith(Contact cont, byte permission) {
        ConnectionManager.add(new Share(TYPE_CALENDAR_CATEGORY, permission, id, cont));
    }

    public void setName(String name) {
        this.name = name;
        update();
    }

    public void delete() {
        super.delete();
        Timetable.categories.remove(this);
    }

}
