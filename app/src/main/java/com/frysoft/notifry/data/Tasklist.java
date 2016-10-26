package com.frysoft.notifry.data;

import com.frysoft.notifry.data.value.ValueCategory;
import com.frysoft.notifry.data.value.ValueInteger;
import com.frysoft.notifry.data.value.ValueString;
import com.frysoft.notifry.data.value.ValueUnsignedByte;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Date;

import java.util.ArrayList;

public class Tasklist extends MySQLEntry implements Fryable {

    public int drag_id;

    protected int owner_id;

    protected ValueCategory category = new ValueCategory();

    protected ValueString name = new ValueString();

    protected ValueUnsignedByte state = new ValueUnsignedByte();

    protected ValueInteger color = new ValueInteger();

    protected ArrayList<TasklistEntry> entries = new ArrayList<>();

    public ShareList shares = new ShareList(this);

    protected Tasklist(FryFile fry) {
        super(fry);
        Logger.Log("Tasklist", "Tasklist(FryFile)");
        owner_id = fry.readId();

        category.readFrom(fry);
        name.readFrom(fry);
        state.readFrom(fry);
        color.readFrom(fry);

        int NoEntries = fry.readArrayLength();
        for(int i=0; i<NoEntries; ++i) {
            TasklistEntry ent = new TasklistEntry(fry);
            ent.tasklist = this;
            entries.add(ent);
        }

        shares.readFrom(fry);

        if(category.isChanged() || name.isChanged() || state.isChanged() || color.isChanged()) {
            update();
        }
    }

    protected Tasklist(int id, int user_id, Category category, String name, byte state, int color) {
        super(id, user_id, Date.getMillis());
        Logger.Log("Tasklist", "Tasklist(int,int,byte,String)");
        this.state.setValue(state);
        this.name.setValue(name);
        this.color.setValue(color);
        this.category.setValue(category);
    }

    @Override
    protected void remove() {
        Data.Tasklists.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("Tasklist", "equals(Object)");
        if(o instanceof Tasklist) {
            Tasklist t = (Tasklist) o;
            if (t.id == id || t.state.equals(state) || t.name.equals(name) || t.color.equals(color) || t.entries.size() == entries.size()) {
                for (int i = 0; i < entries.size(); ++i) {
                    if (!t.entries.get(i).equals(entries.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void addData(MySQL mysql) {
        mysql.addId("category_id", id);
        mysql.add("name", name);
        mysql.add("state", state);
        mysql.add("color", color);
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
    protected void sync(MySQLEntry entry) {
        Tasklist tl = (Tasklist) entry;
        boolean update = false;

        if(category.doUpdate(tl.category)) {
            update = true;
        }
        if(name.doUpdate(tl.name)) {
            update = true;
        }
        if(state.doUpdate(tl.state)) {
            update = true;
        }
        if(color.doUpdate(tl.color)) {
            update = true;
        }

        if(update) {
            update();
        }
    }

    @Override
    protected char getType() {
        return TYPE_TASKLIST;
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("Tasklist", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.writeId(owner_id);

        category.writeTo(fry);
        name.writeTo(fry);
        state.writeTo(fry);
        color.writeTo(fry);
        fry.writeObjects(entries);
        shares.writeTo(fry);
    }

    protected TasklistEntry getEntryById(int id) {
        for(TasklistEntry ent : entries) {
            if(ent.id == id) {
                return ent;
            }
        }
        return null;
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
        return name.getValue();
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
        return ( state.getValue() == 1 );
    }

    public void rename(String name) {
        Logger.Log("Tasklist", "rename(String)");
        this.name.setValue(name);
        update();
    }

    public void addEntry(String task, boolean state) {
        Logger.Log("Tasklist", "addEntry(String,boolean)");
        TasklistEntry ent = new TasklistEntry(this, task, state);
        entries.add(ent);
        if(isOnline()) {
            ent.create();
        }
    }

    public void addEntry(int index, String task, boolean state) {
        Logger.Log("Tasklist", "addEntry(int,String,boolean)");
        TasklistEntry ent = new TasklistEntry(this, task, state);
        entries.add(index,ent);
        if(isOnline()) {
            ent.create();
        }
    }

    public void set(String name, byte state, int color) {
        this.name.setValue(name);
        this.state.setValue(state);
        this.color.setValue(color);
        update();
    }

    public void set(String name, boolean done, int color) {
        Logger.Log("Tasklist", "change(String,boolean)");
        set(name, done ? (byte)1 : (byte)0, color);
    }

    public void setState(boolean done) {
        Logger.Log("Tasklist", "change(boolean)");
        this.state.setValue(done ? (byte)1 : (byte)0);
        update();
    }

    public void setName(String name) {
        this.name.setValue(name);
        update();
    }

    public void setColor(int color) {
        this.color.setValue(color);
        update();
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
        return entries.get(index).description.getValue();
    }

    public int getCategoryId() {
        return category.getId();
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
        return color.getValue();
    }

}
