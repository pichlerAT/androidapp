package fry.oldschool.data;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class TasklistEntry extends OnlineEntry implements Fryable {

    public int table_id;

    public int user_id;

    public byte state;

    public String description;

    public static TasklistEntry createBackup(int id, int table_id, int user_id, byte state, String description) {
        TasklistEntry ent = new TasklistEntry(id);
        ent.table_id = table_id;
        ent.user_id = user_id;
        ent.state = state;
        ent.description = description;
        return ent;
    }

    public TasklistEntry(int id, int table_id, int user_id, byte state, String description) {
        this.type = TYPE_TASKLIST_ENTRY;
        this.id = id;
        this.table_id = table_id;
        this.user_id = user_id;
        this.description = description;
        this.state = state;
        if(table_id != 0 && id == 0) {
            ConnectionManager.add(this);
        }
    }

    protected TasklistEntry(String description, boolean state) {
        this(0,0,USER_ID,( state ? (byte)1 : (byte)0 ),description);
    }

    protected TasklistEntry(int id) {
        this.id = id;
    }

    @Override
    protected boolean mysql() {
        String resp = getLine(DIR_TASKLIST_ENTRY + "create.php","&table_id="+table_id+"&description="+description+"&state="+state);
        if(resp.substring(0,3).equals("suc")) {
            id = Integer.parseInt(resp.substring(3));
            return true;
        }
        return false;
    }

    @Override
    public void writeTo(FryFile file) {
        file.write(id);
        file.write(user_id);
        file.write(state);
        file.write(description);
    }

    public void change(String description,boolean state) {
        this.description = description;
        this.state = ( state ? (byte)1 : (byte)0 );
        if(table_id != 0) {
            ConnectionManager.add(new Update(TYPE_TASKLIST_ENTRY,id));
        }
    }

    public void change(boolean state) {
        this.state = ( state ? (byte)1 : (byte)0 );
        if(table_id != 0) {
            ConnectionManager.add(new Update(TYPE_TASKLIST_ENTRY,id));
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
        return ContactList.findContactByUserId(user_id);
    }

    public String getUpdateString() {
        return ("&entry_id=" + id + "&description=" + description + "&state=" + state);
    }

}
