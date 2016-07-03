package fry.oldschool.data;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class TasklistEntry extends MySQL implements Fryable {

    protected int table_id;

    protected byte state;

    protected String description;

    protected TasklistEntry() {
        super(TYPE_TASKLIST_ENTRY, 0, 0);
    }

    protected TasklistEntry(int id, int table_id, int user_id, byte state, String description) {
        super(TYPE_TASKLIST_ENTRY, id, user_id);
        this.table_id = table_id;
        this.description = description;
        this.state = state;
    }

    protected TasklistEntry(String description, boolean state) {
        this(0,0,USER_ID,( state ? (byte)1 : (byte)0 ),description);
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
        return new TasklistEntry(id, table_id, user_id, state, description);
    }

    @Override
    protected boolean mysql() {
        String resp = getLine(DIR_TASKLIST_ENTRY + "create.php","&table_id="+table_id+"&description="+description+"&state="+state);
        if(resp != null) {
            id = Integer.parseInt(resp);
            return true;
        }
        return false;
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
        fry.write(id);
        fry.write(user_id);
        fry.write(state);
        fry.write(description);
    }

    @Override
    public void readFrom(FryFile fry) {
        id = fry.getInt();
        user_id = fry.getInt();
        state = fry.getByte();
        description = fry.getString();
    }

    public void set(String description,boolean state) {
        this.description = description;
        this.state = ( state ? (byte)1 : (byte)0 );
        if(table_id != 0) {
            OfflineEntry.update(this);
        }
    }

    public void setDescription(String description,boolean state) {
        this.description = description;
        if(table_id != 0) {
            OfflineEntry.update(this);
        }
    }

    public void setState(boolean state) {
        this.state = ( state ? (byte)1 : (byte)0 );
        if(table_id != 0) {
            OfflineEntry.update(this);
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

    public String getUpdateString() {
        return ("&id=" + id + "&description=" + description + "&state=" + state);
    }

    public boolean equals(TasklistEntry ent) {
        return (id == ent.id && table_id == ent.table_id && user_id == ent.user_id && state == ent.state && description.equals(ent.description));
    }

}
