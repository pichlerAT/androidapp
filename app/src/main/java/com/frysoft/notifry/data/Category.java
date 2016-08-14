package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;

public class Category extends MySQLEntry implements Fryable {

    protected String name;

    protected int color;

    public ShareList shares = new ShareList(TYPE_CALENDAR_CATEGORY, id);

    protected Category(FryFile fry) {
        super(fry);
        Logger.Log("TimetableCategory", "TimetableCategory(FryFile)");
        name = fry.getString();
        color = fry.getInt();
        shares.readFrom(fry);
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
/*
            for(TimetableEntry ent : offline_entries) {
                ent.category = this;
                ent.create();
            }
            offline_entries = new ArrayList<>();

            for(Tasklist tl : offline_tasklists) {
                tl.category = this;
                tl.create();
            }
            offline_tasklists = new ArrayList<>();
*/
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
        return (isOwner() || (shares != null && shares.size() > 0 && shares.getPermission(0) >= Share.PERMISSION_EDIT));
    }

    @Override
    public int getShareId() {
        if(shares != null && shares.size() > 0 && !isOwner()) {
            return shares.getId(0);
        }
        return 0;
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("TimetableCategory", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.writeString(name);
        fry.writeInt(color);
        shares.writeTo(fry);
        /*
        fry.writeObjects(offline_entries);
        fry.writeObjects(offline_tasklists);
        */
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        Logger.Log("TimetableCategory", "getName()");
        return name;
    }
/*
    protected void addOfflineEntry(TimetableEntry entry) {
        Logger.Log("TimetableCategory", "addOfflineEntry(TimetableEntry)");
        offline_entries.add(entry);
    }

    protected void addOfflineEntry(Tasklist tasklist) {
        offline_tasklists.add(tasklist);
    }
*/
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
        Data.Categories.remove(this);
    }

}
