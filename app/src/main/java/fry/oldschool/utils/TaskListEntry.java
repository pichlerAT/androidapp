package fry.oldschool.utils;

public class TaskListEntry {

    protected byte state;

    protected int table_id;

    public String description;

    protected TaskListEntry(int id,int user_id,int table_id,String description,byte state) {
        //super(id,user_id);
        this.table_id = table_id;
        this.description = description;
        this.state = state;
    }

    protected TaskListEntry(int table_id,String description,byte state) {
        //this(0,table_id,USER_ID,description,state);
    }

    public void set(String description,boolean state) {
        this.description = description;
        this.state = ( state ? (byte)0 : (byte)1 );
        //App.conMan.add(this);
    }

    public boolean done() {
        return ( state == 0 );
    }
/*
    @Override
    protected String getConnectionManagerString() {
        return super.getConnectionManagerString() + SEP_1 + table_id + SEP_1 + description + SEP_1 + state ;
    }

    @Override
    protected byte getType() {
        return TYPE_TASKLISTENTRY;
    }

    @Override
    protected String[] getCreate() {
        return new String[]{"todolist/entry/create.php","&table_id="+table_id+"&description="+description+"&state="+state};
    }

    @Override
    protected String[] getUpdate() {
        return new String[]{"todolist/entry/update.php","&entry_id=" + id + "&description=" + description + "&state=" + state};
    }

    @Override
    protected String[] getDelete() {
        return new String[]{"todolist/entry/delete.php","&entry_id=" + id};
    }
*/
}
