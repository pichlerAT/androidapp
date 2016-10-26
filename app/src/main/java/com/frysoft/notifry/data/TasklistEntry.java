package com.frysoft.notifry.data;

import com.frysoft.notifry.data.value.ValueString;
import com.frysoft.notifry.data.value.ValueUnsignedByte;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Date;

public class TasklistEntry extends MySQLEntry implements Fryable {

    protected ValueString description = new ValueString();

    protected ValueUnsignedByte state = new ValueUnsignedByte();

    protected Tasklist tasklist;

    protected TasklistEntry(FryFile fry) {
        super(fry);
        Logger.Log("TasklistEntry", "TasklistEntry(FryFile)");
        description.readFrom(fry);
        state.readFrom(fry);

        if(description.isChanged() || state.isChanged()) {
            update();
        }
    }

    protected TasklistEntry(int id, int user_id, Tasklist tasklist, String description, byte state) {
        super(id, user_id, Date.getMillis());
        Logger.Log("TasklistEntry", "TasklistEntry(int,int,byte,int,String)");
        this.tasklist = tasklist;
        this.description.setValue(description);
        this.state.setValue(state);
    }

    protected TasklistEntry(Tasklist tasklist, String description, boolean state) {
        this(0, 0, tasklist, description, (state ? (byte)1 : (byte)0));
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("TasklistEntry", "equals(Object)");
        if(o instanceof TasklistEntry) {
            TasklistEntry e = (TasklistEntry) o;
            return (e.id == id && e.state.equals(state) && e.description.equals(description));
        }
        return false;
    }

    @Override
    protected void addData(MySQL mysql) {
        mysql.addId("table_id", tasklist.id);
        mysql.add("description", description);
        mysql.add("state", state);
    }

    @Override
    public boolean canEdit() {
        Logger.Log("TasklistEntry", "canEdit()");
        return tasklist.canEdit();
    }

    @Override
    public int getShareId() {
        return tasklist.getShareId();
    }

    @Override
    protected void sync(MySQLEntry entry) {
        TasklistEntry tle = (TasklistEntry) entry;
        boolean update = false;

        if(state.doUpdate(tle.state)) {
            update = true;
        }
        if(description.doUpdate(tle.description)) {
            update = true;
        }

        if(update) {
            update();
        }
    }

    @Override
    protected char getType() {
        return TYPE_TASKLIST_ENTRY;
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("TasklistEntry", "writeTo(FryFile)");
        super.writeTo(fry);
        description.writeTo(fry);
        state.writeTo(fry);
    }

    @Override
    protected void remove() {
        tasklist.entries.remove(this);
    }

    public void set(String description, byte state) {
        this.description.setValue(description);
        this.state.setValue(state);
        update();
    }
    public void set(String description, boolean state) {
        set(description, (state ? (byte)1 : (byte)0));
    }

    public void setDescription(String description) {
        this.description.setValue(description);
        update();
    }

    public void setState(boolean state) {
        this.state.setValue(state ? (byte)1 : (byte)0);
        update();
    }

    public String getDescription() {
        Logger.Log("TasklistEntry", "getDescription()");
        return description.getValue();
    }

    public boolean isDone() {
        Logger.Log("TasklistEntry", "isDone()");
        return ( state.getValue() == 1 );
    }

    public Contact getOwner() {
        Logger.Log("TasklistEntry", "getOwner()");
        if(isOwner()) {
            return null;
        }
        return ContactList.getContactByUserId(user_id);
    }

    public boolean equals(TasklistEntry ent) {
        Logger.Log("TasklistEntry", "equals(TasklistEntry)");
        return (id == ent.id && tasklist.id == ent.tasklist.id && user_id == ent.user_id && state == ent.state && description.equals(ent.description));
    }

}
