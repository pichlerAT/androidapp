package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;

import java.util.ArrayList;

public class Tasklist extends MySQLEntry implements Fryable {

    public int drag_id;

    protected int color;

    protected byte state;

    protected String name;

    protected Category category;

    protected ArrayList<TasklistEntry> entries = new ArrayList<>();

    public ShareList shares = new ShareList(TYPE_TASKLIST, id);

    protected Tasklist(FryFile fry) {
        super(fry);
        Logger.Log("Tasklist", "Tasklist(FryFile)");

        category = Data.Categories.getById(fry.getUnsignedInt());
        name = fry.getString();
        state = fry.getUnsignedByte();
        color = fry.getInt();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            TasklistEntry ent = new TasklistEntry(fry);
            ent.tasklist = this;
            entries.add(ent);
        }

        shares.readFrom(fry);
    }

    protected Tasklist(int id, int user_id, Category category, String name, byte state, int color) {
        super(TYPE_TASKLIST, id, user_id);
        Logger.Log("Tasklist", "Tasklist(int,int,byte,String)");
        this.state = state;
        this.name = name;
        this.color = color;

        this.category = category;

        if(id != 0) {
            shares = new ShareList(TYPE_TASKLIST, id);
        }
    }

    protected Tasklist(int id, int user_id, int category_id, String name, byte state, int color) {
        this(id, user_id, Data.Categories.getById(category_id), name, state, color);
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("Tasklist", "equals(Object)");
        if(o instanceof Tasklist) {
            Tasklist t = (Tasklist) o;
            if(t.id != id || t.state != state || !t.name.equals(name) || color!=t.color || t.entries.size() != entries.size()) {
                return false;
            }
            for(int i=0; i<entries.size(); ++i) {
                if(!t.entries.get(i).equals(entries.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Tasklist backup() {
        Logger.Log("Tasklist", "backup()");
        Tasklist tl = new Tasklist(id, user_id, category, name, state, color);
        for(TasklistEntry ent : entries) {
            tl.entries.add(ent.backup());
        }
        return tl;
    }

    @Override
    protected boolean mysql_create() {
        Logger.Log("Tasklist", "mysql_create()");
        FryFile fry = executeMySQL(DIR_TASKLIST + "create.php", "&name=" + name + "&state=" + signed(state) + "$color=" + signed(color));
        if(fry != null) {
            id = fry.getUnsignedInt();
            shares = new ShareList(TYPE_TASKLIST, id);

            for(TasklistEntry ent : entries) {
                ent.create();
            }
            return true;
        }
        return false;
    }
    @Override
    protected boolean mysql_update() {
        Logger.Log("Tasklist", "mysql_update()");
        return (executeMySQL(DIR_TASKLIST + "update.php", "&share_id=" + signed(id) + "&name=" + name + "&state=" + signed(state) + "$color=" + signed(color)) != null);
    }

    @Override
    protected boolean mysql_delete() {
        Logger.Log("Tasklist", "mysql_delete()");
        return (executeMySQL(DIR_TASKLIST + "delete.php", "&share_id=" + signed(id)) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) {
        Logger.Log("Tasklist", "synchronize(MySQL)");
        Tasklist t = (Tasklist) mysql;
        state = t.state;
        name = t.name;
        entries = t.entries;
        color = t.color;
    }

    @Override
    public boolean canEdit() {
        Logger.Log("Tasklist", "canEdit()");
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
        Logger.Log("Tasklist", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.writeUnsignedInt(getCategoryId());
        fry.writeString(name);
        fry.writeUnsignedByte(state);
        fry.writeInt(color);
        fry.writeObjects(entries);
        shares.writeTo(fry);
    }

    public int getNoEntries() {
        Logger.Log("Tasklist", "getNoEntries()");
        return entries.size();
    }

    public TasklistEntry getEntry(int index) {
        Logger.Log("Tasklist", "getEntry(int)");
        return entries.get(index);
    }

    public String getName() {
        Logger.Log("Tasklist", "getName()");
        return name;
    }

    public Contact getOwner() {
        Logger.Log("Tasklist", "getOwner()");
        if(isOwner()) {
            return null;
        }
        return ContactList.getContactByUserId(user_id);
    }

    public ArrayList<TasklistEntry> getEntries() {
        Logger.Log("Tasklist", "getEntries()");
        return entries;
    }

    public boolean isDone() {
        Logger.Log("Tasklist", "isDone()");
        return ( state == 1 );
    }

    public void rename(String name) {
        Logger.Log("Tasklist", "rename(String)");
        this.name = name;
        update();
    }

    public void addEntry(String task,boolean state) {
        Logger.Log("Tasklist", "addEntry(String,boolean)");
        TasklistEntry ent = new TasklistEntry(this, task, state);
        entries.add(ent);
        if(isOnline()) {
            ent.create();
        }
    }

    public void addEntry(int index,String task,boolean state) {
        Logger.Log("Tasklist", "addEntry(int,String,boolean)");
        TasklistEntry ent = new TasklistEntry(this, task, state);
        entries.add(index,ent);
        if(isOnline()) {
            ent.create();
        }
    }

    public void setState(boolean state) {
        Logger.Log("Tasklist", "change(boolean)");
        set(name, state, color);
    }

    public void set(String name,boolean state, int color) {
        Logger.Log("Tasklist", "change(String,boolean)");
        set(name, state ? (byte)1 : (byte)0, color);
    }

    public void set(String name, byte state, int color) {
        this.name = name;
        this.state = state;
        this.color = color;
        update();
    }

    public void setName(String name) {
        set(name, state, color);
    }

    public void setColor(int color) {
        set(name, state, color);
    }

    public boolean isDone(int index) {
        Logger.Log("Tasklist", "isDone(int)");
        return entries.get(index).isDone();
    }

    public int length() {
        Logger.Log("Tasklist", "length()");
        return entries.size();
    }

    public String getTaskName(int index) {
        Logger.Log("Tasklist", "getTaskName(int)");
        return entries.get(index).description;
    }

    public int getCategoryId() {
        if(category == null) {
            return 0;
        }
        return category.id;
    }

    @Override
    public void delete() {
        Logger.Log("Tasklist", "delete()");
        super.delete();
        Data.Tasklists.remove(this);
    }

    public void delete(int index) {
        Logger.Log("Tasklist", "delete(int)");
        entries.remove(index).delete();
    }

    public boolean equals(Tasklist tl) {
        Logger.Log("Tasklist", "equals(Tasklist)");
        if(state != tl.state || entries.size() != tl.entries.size() || !name.equals(tl.name) || color!=tl.color) {
            return false;
        }
        for(int i=0; i<entries.size(); ++i) {
            if(!entries.get(i).equals(tl.entries.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int getColor() {
        return color;
    }

}
