package fry.oldschool.data;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;
import fry.oldschool.utils.Logger;

public class TasklistEntry extends MySQLEntry implements Fryable {

    protected byte state;

    protected int table_id;

    protected String description;

    protected TasklistEntry(FryFile fry) {
        super(fry);
        Logger.Log("TasklistEntry", "TasklistEntry(FryFile)");
        state = fry.getByte();
        description = fry.getString();
    }

    protected TasklistEntry(int id, int user_id, byte state, int table_id, String description) {
        super(TYPE_TASKLIST_ENTRY, id, user_id);
        Logger.Log("TasklistEntry", "TasklistEntry(int,int,byte,int,String)");
        this.state = state;
        this.table_id = table_id;
        this.description = description;
    }

    protected TasklistEntry(String description, boolean state) {
        this(0, USER_ID, (state ? (byte)1 : (byte)0), 0, description);
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
        return new TasklistEntry(id, user_id, state, table_id, description);
    }

    @Override
    protected boolean mysql_create() {
        Logger.Log("TasklistEntry", "mysql_create()");
        String resp = getLine(DIR_TASKLIST_ENTRY + "create.php","&table_id="+table_id+"&description="+description+"&state="+state);
        if(resp != null) {
            id = Integer.parseInt(resp);
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        Logger.Log("TasklistEntry", "mysql_update()");
        return (getLine(DIR_TASKLIST_ENTRY + "update.php","&id="+id+"&description="+description+"&state="+state) != null);
    }

    @Override
    protected boolean mysql_delete() {
        Logger.Log("TasklistEntry", "mysql_delete()");
        return (getLine(DIR_TASKLIST_ENTRY + "delete.php","&id="+id) != null);
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
        return (isOwner() || TasklistManager.getTasklistById(table_id).sharedContacts.isSharedWithUserId(USER_ID));
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("TasklistEntry", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.write(state);
        fry.write(description);
    }

    public void set(String description,boolean state) {
        Logger.Log("TasklistEntry", "set(String,boolean)");
        this.description = description;
        this.state = ( state ? (byte)1 : (byte)0 );
        if(table_id != 0) {
            update();
        }
    }

    public void setDescription(String description,boolean state) {
        Logger.Log("TasklistEntry", "setDescription(String,boolean)");
        this.description = description;
        if(table_id != 0) {
            update();
        }
    }

    public void setState(boolean state) {
        Logger.Log("TasklistEntry", "setState(boolean)");
        this.state = ( state ? (byte)1 : (byte)0 );
        if(table_id != 0) {
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
        return (id == ent.id && table_id == ent.table_id && user_id == ent.user_id && state == ent.state && description.equals(ent.description));
    }

}
