package fry.oldschool.data;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class TasklistEntry extends MySQLEntry implements Fryable {

    protected byte state;

    protected int table_id;

    protected String description;

    protected TasklistEntry(FryFile fry) {
        super(fry);
        state = fry.getByte();
        description = fry.getString();
    }

    protected TasklistEntry(int id, int user_id, byte state, int table_id, String description) {
        super(TYPE_TASKLIST_ENTRY, id, user_id);
        this.state = state;
        this.table_id = table_id;
        this.description = description;
    }

    protected TasklistEntry(String description, boolean state) {
        this(0, USER_ID, (state ? (byte)1 : (byte)0), 0, description);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TasklistEntry) {
            TasklistEntry e = (TasklistEntry) o;
            return (e.id == id && e.state == state && e.description.equals(description));
        }
        return false;
    }

    @Override
    public TasklistEntry backup() {
        return new TasklistEntry(id, user_id, state, table_id, description);
    }

    @Override
    protected boolean mysql_create() {
        String resp = getLine(DIR_TASKLIST_ENTRY + "create.php","&table_id="+table_id+"&description="+description+"&state="+state);
        if(resp != null) {
            id = Integer.parseInt(resp);
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        return (getLine(DIR_TASKLIST_ENTRY + "update.php","&id="+id+"&description="+description+"&state="+state) != null);
    }

    @Override
    protected boolean mysql_delete() {
        return (getLine(DIR_TASKLIST_ENTRY + "delete.php","&id="+id) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) {
        TasklistEntry e = (TasklistEntry) mysql;
        state = e.state;
        description = e.description;
    }

    @Override
    public boolean canEdit() {
        return (isOwner() || TasklistManager.getTasklistById(table_id).hasShareByUserId(USER_ID));
    }

    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);
        fry.write(state);
        fry.write(description);
    }

    public void set(String description,boolean state) {
        this.description = description;
        this.state = ( state ? (byte)1 : (byte)0 );
        if(table_id != 0) {
            update();
        }
    }

    public void setDescription(String description,boolean state) {
        this.description = description;
        if(table_id != 0) {
            update();
        }
    }

    public void setState(boolean state) {
        this.state = ( state ? (byte)1 : (byte)0 );
        if(table_id != 0) {
            update();
        }
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return ( state == 1 );
    }

    public Contact getOwner() {
        if(isOwner()) {
            return null;
        }
        return ContactList.getContactByUserId(user_id);
    }

    public boolean equals(TasklistEntry ent) {
        return (id == ent.id && table_id == ent.table_id && user_id == ent.user_id && state == ent.state && description.equals(ent.description));
    }

}
