package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;

public class TasklistEntry extends MySQLEntry implements Fryable {

    protected byte state;

    protected String description;

    protected Tasklist tasklist;

    protected TasklistEntry(FryFile fry) {
        super(fry);
        Logger.Log("TasklistEntry", "TasklistEntry(FryFile)");
        description = fry.getString();
        state = fry.getUnsignedByte();
    }

    protected TasklistEntry(int id, int user_id, Tasklist tasklist, String description, byte state) {
        super(id, user_id);
        Logger.Log("TasklistEntry", "TasklistEntry(int,int,byte,int,String)");
        this.tasklist = tasklist;
        this.description = description;
        this.state = state;
    }

    protected TasklistEntry(Tasklist tasklist, String description, boolean state) {
        this(0, 0, tasklist, description, (state ? (byte)1 : (byte)0));
        Logger.Log("TasklistEntry", "TasklistEntry(String,boolean)");
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("TasklistEntry", "equals(Object)");
        if(o instanceof TasklistEntry) {
            TasklistEntry e = (TasklistEntry) o;
            return (e.id == id && e.state == state && e.description.equals(description));
        }
        return false;
    }

    @Override
    public TasklistEntry backup() {
        Logger.Log("TasklistEntry", "backup()");
        return new TasklistEntry(id, user_id, tasklist, description, state);
    }

    @Override
    protected boolean mysql_create() {
        Logger.Log("TasklistEntry", "mysql_create()");
        FryFile fry = executeMySQL(DIR_TASKLIST_ENTRY + "create.php","&table_id="+signed(tasklist.id)+"&description="+description+"&state="+signed(state));
        if(fry != null) {
            id = fry.getUnsignedInt();
            //user_id = User.getId();
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        Logger.Log("TasklistEntry", "mysql_update()");
        return (executeMySQL(DIR_TASKLIST_ENTRY + "update.php","&id="+signed(id)+"&description="+description+"&state="+signed(state)) != null);
    }

    @Override
    protected boolean mysql_delete() {
        Logger.Log("TasklistEntry", "mysql_delete()");
        return (executeMySQL(DIR_TASKLIST_ENTRY + "delete.php","&id="+signed(id)) != null);
    }

    @Override
    protected byte getType() {
        return TYPE_TASKLIST_ENTRY;
    }

    @Override
    protected String getPath() {
        return DIR_TASKLIST_ENTRY;
    }

    @Override
    protected void synchronize(MySQL mysql) {
        Logger.Log("TasklistEntry", "synchronize(MySQL)");
        TasklistEntry e = (TasklistEntry) mysql;
        state = e.state;
        description = e.description;
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
    public void writeTo(FryFile fry) {
        Logger.Log("TasklistEntry", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.writeString(description);
        fry.writeUnsignedByte(state);
    }

    public void set(String description,boolean state) {
        Logger.Log("TasklistEntry", "set(String,boolean)");
        this.description = description;
        this.state = ( state ? (byte)1 : (byte)0 );
        if(tasklist.isOnline()) {
            update();
        }
    }

    public void setDescription(String description, boolean state) {
        Logger.Log("TasklistEntry", "setDescription(String,boolean)");
        this.description = description;
        if(tasklist.isOnline()) {
            update();
        }
    }

    public void setState(boolean state) {
        Logger.Log("TasklistEntry", "setState(boolean)");
        this.state = ( state ? (byte)1 : (byte)0 );
        if(tasklist.isOnline()) {
            update();
        }
    }

    public String getDescription() {
        Logger.Log("TasklistEntry", "getDescription()");
        return description;
    }

    public boolean isDone() {
        Logger.Log("TasklistEntry", "isDone()");
        return ( state == 1 );
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
