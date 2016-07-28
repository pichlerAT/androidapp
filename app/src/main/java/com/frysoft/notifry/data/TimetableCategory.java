package com.frysoft.notifry.data;

import java.util.ArrayList;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;

public class TimetableCategory extends MySQLEntry implements Fryable {

    protected String name;

    protected ArrayList<TimetableEntry> offline_entries = new ArrayList<>();

    public ShareList shares;

    public static TimetableCategory create(String name) {
        Logger.Log("TimetableCategory", "create(String)");
        TimetableCategory cat = new TimetableCategory(0,USER_ID,name);
        cat.create();
        Timetable.categories.add(cat);
        return cat;
    }

    protected TimetableCategory(FryFile fry) {
        super(fry);
        Logger.Log("TimetableCategory", "TimetableCategory(FryFile)");
        name = fry.getString();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            offline_entries.add(new TimetableEntry(fry));
        }

        if(id != 0) {
            shares = new ShareList(TYPE_CALENDAR_CATEGORY, id);
        }
    }

    protected TimetableCategory(int id,int user_id,String name) {
        super(TYPE_CALENDAR_CATEGORY, id, user_id);
        Logger.Log("TimetableCategory", "TimetableCategory(int,int,String)");
        this.name = name;

        if(id != 0) {
            shares = new ShareList(TYPE_CALENDAR_CATEGORY, id);
        }
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("TimetableCategory", "equals(Object)");
        if(o instanceof TimetableCategory) {
            TimetableCategory c = (TimetableCategory) o;
            return (c.id == id && c.name.equals(name));
        }
        return false;
    }

    @Override
    public TimetableCategory backup() {
        Logger.Log("TimetableCategory", "backup()");
        return new TimetableCategory(id, user_id, name);
    }

    @Override
    protected boolean mysql_create() {
        Logger.Log("TimetableCategory", "mysql_create()");
        String resp = getLine(DIR_CALENDAR_CATEGORY + "create.php", "&name="+name);
        if(resp != null) {
            id = Integer.parseInt(resp);
            shares = new ShareList(TYPE_CALENDAR_CATEGORY, id);

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
        Logger.Log("TimetableCategory", "mysql_update()");
        return (getLine(DIR_CALENDAR_CATEGORY + "update.php", "&share_id="+id+"&name="+name) != null);
    }

    @Override
    protected boolean mysql_delete() {
        Logger.Log("TimetableCategory", "mysql_delete()");
        return (getLine(DIR_CALENDAR_CATEGORY + "delete.php", "&share_id="+id) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) {
        Logger.Log("TimetableCategory", "synchronize(MySQL)");
        TimetableCategory c = (TimetableCategory) mysql;
        name = c.name;
    }

    @Override
    public boolean canEdit() {
        Logger.Log("TimetableCategory", "canEdit()");
        return isOwner();
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("TimetableCategory", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.write(name);
        fry.write(offline_entries);
    }

    public String getName() {
        Logger.Log("TimetableCategory", "getName()");
        return name;
    }

    protected void addOfflineEntry(TimetableEntry entry) {
        Logger.Log("TimetableCategory", "addOfflineEntry(TimetableEntry)");
        offline_entries.add(entry);
    }

    public void setName(String name) {
        Logger.Log("TimetableCategory", "setName(String)");
        this.name = name;
        update();
    }

    @Override
    public void delete() {
        Logger.Log("TimetableCategory", "delete()");
        super.delete();
        Timetable.categories.remove(this);
    }

}
