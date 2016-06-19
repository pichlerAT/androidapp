package fry.oldschool.data;

public class TasklistEntry extends OnlineEntry {

    public int table_id;

    public int user_id;

    public byte state;

    public String description;

    public TasklistEntry(String description, boolean state) {
        this(0,0,USER_ID,description,( state ? (byte)1 : (byte)0 ));
    }

    public TasklistEntry(int id, int table_id, int user_id, String description, byte state) {
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

    @Override
    protected boolean mysql() {
        String resp = getLine(DIR_TASKLIST_ENTRY + "create.php","&table_id="+table_id+"&description="+description+"&state="+state);
        if(resp.substring(0,3).equals("suc")) {
            id = Integer.parseInt(resp.substring(3));
            return true;
        }
        return false;
    }

    public String getUpdateString() {
        return ("&entry_id=" + id + "&description=" + description + "&state=" + state);
    }

}
