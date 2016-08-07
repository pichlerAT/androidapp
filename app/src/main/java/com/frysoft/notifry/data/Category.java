package com.frysoft.notifry.data;

import java.util.ArrayList;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.User;

public class Category extends MySQLEntry implements Fryable {

    protected String name;

    protected int color;

    protected ArrayList<TimetableEntry> offline_entries = new ArrayList<>();

    public ShareList shares;

    public static Category create(String name, int color) {
        Logger.Log("TimetableCategory", "create(String)");
        Category cat = new Category(0, User.getId(), name, color);
        cat.create();
        Timetable.categories.add(cat);
        return cat;
    }

    protected Category(FryFile fry) {
        super(fry);
        Logger.Log("TimetableCategory", "TimetableCategory(FryFile)");
        name = fry.getString();
        color = fry.getInt();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            offline_entries.add(new TimetableEntry(fry));
        }

        if(id != 0) {
            shares = new ShareList(TYPE_CALENDAR_CATEGORY, id);
        }
    }

    protected Category(int id, int user_id, String name, int color) {
        super(TYPE_CALENDAR_CATEGORY, id, user_id);
        Logger.Log("TimetableCategory", "TimetableCategory(int,int,String)");
        this.name = name;
        this.color = color;

        if(id != 0) {
            shares = new ShareList(TYPE_CALENDAR_CATEGORY, id);
        }
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("TimetableCategory", "equals(Object)");
        if(o instanceof Category) {
            Category c = (Category) o;
            return (c.id == id && c.name.equals(name) && color == c.color);
        }
        return false;
    }

    @Override
    public Category backup() {
        Logger.Log("TimetableCategory", "backup()");
        return new Category(id, user_id, name, color);
    }

    @Override
    protected boolean mysql_create() {
        Logger.Log("TimetableCategory", "mysql_create()");
        String resp = executeMySQL(DIR_CATEGORY + "create.php", "&name=" + name + "&color=" + signed(color));
        if(resp != null) {
            id = Integer.parseInt(resp);
            shares = new ShareList(TYPE_CALENDAR_CATEGORY, id);

            for(TimetableEntry ent : offline_entries) {
                ent.category = this;
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
        return (executeMySQL(DIR_CATEGORY + "update.php", "&share_id=" + signed(id) + "&name=" + name + "&color=" + signed(color)) != null);
    }

    @Override
    protected boolean mysql_delete() {
        Logger.Log("TimetableCategory", "mysql_delete()");
        return (executeMySQL(DIR_CATEGORY + "delete.php", "&share_id=" + signed(id)) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) {
        Logger.Log("TimetableCategory", "synchronize(MySQL)");
        Category c = (Category) mysql;
        name = c.name;
        color = c.color;
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
        fry.write(color);
        fry.write(offline_entries);
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        Logger.Log("TimetableCategory", "getName()");
        return name;
    }

    protected void addOfflineEntry(TimetableEntry entry) {
        Logger.Log("TimetableCategory", "addOfflineEntry(TimetableEntry)");
        offline_entries.add(entry);
    }

    public void set(String name, int color) {
        this.name = name;
        this.color = color;
        update();
    }

    public void setColor(int color) {
        set(name, color);
    }

    public void setName(String name) {
        Logger.Log("TimetableCategory", "setName(String)");
        set(name, color);
    }

    @Override
    public void delete() {
        Logger.Log("TimetableCategory", "delete()");
        super.delete();
        Timetable.categories.remove(this);
    }

}