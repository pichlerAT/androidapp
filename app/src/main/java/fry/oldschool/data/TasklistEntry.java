package fry.oldschool.data;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class TasklistEntry extends MySQL implements Fryable {

    public int table_id;

    public int user_id;

    public byte state;

    public String description;

    protected TasklistEntry() { }

    public TasklistEntry(int id, int table_id, int user_id, byte state, String description) {
        this.type = TYPE_TASKLIST_ENTRY;
        this.id = id;
        this.table_id = table_id;
        this.user_id = user_id;
        this.description = description;
        this.state = state;
    }

    protected TasklistEntry(String description, boolean state) {
        this(0,0,USER_ID,( state ? (byte)1 : (byte)0 ),description);
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

    public void change(String description,boolean state) {
        this.description = description;
        this.state = ( state ? (byte)1 : (byte)0 );
        if(table_id != 0) {
            OfflineEntry.update(TYPE_TASKLIST_ENTRY, id);
        }
    }

    public void change(boolean state) {
        this.state = ( state ? (byte)1 : (byte)0 );
        if(table_id != 0) {
            OfflineEntry.update(TYPE_TASKLIST_ENTRY, id);
        }
    }

    public boolean isDone() {
        return ( state == 1 );
    }

    public boolean isOwner() {
        return ( user_id == USER_ID );
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

    public TasklistEntry backup() {
        return new TasklistEntry(id, table_id, user_id, state, description);
    }

}
