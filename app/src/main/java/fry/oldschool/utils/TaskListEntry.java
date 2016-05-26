package fry.oldschool.utils;

public class TaskListEntry extends Entry {

    protected int id;

    protected int table_id;

    protected int user_id;

    protected byte state;

    public String description;

    protected static TaskListEntry create(int table_id,String description,boolean state) {
        TaskListEntry ent = new TaskListEntry(0,table_id,Entry.USER_ID,description,( state ? (byte)0 : (byte)1 ));
        App.conMan.add(ent);
        return ent;
    }

    protected TaskListEntry(int id,int table_id,int user_id,String description,byte state) {
        this.id = id;
        this.table_id = table_id;
        this.user_id = user_id;
        this.description = description;
        this.state = state;
    }

    protected TaskListEntry(String line) {
        String[] r = line.split(";");
        id = Integer.parseInt(r[0]);
        user_id = Integer.parseInt(r[1]);
        table_id = Integer.parseInt(r[2]);
        description = r[3];
        state = Byte.parseByte(r[4]);
    }

    public void change(String description,boolean state) {
        this.description = description;
        this.state = ( state ? (byte)0 : (byte)1 );
        App.conMan.add(this);
    }

    public boolean done() {
        return ( state == 0 );
    }

    public boolean owner() {
        return ( user_id == Entry.USER_ID );
    }

    public Contact getOwner() {
        if(owner()) {
            return null;
        }
        return App.conLis.findContactById(user_id);
    }

    @Override
    protected String getConManString() {
        return TYPE_TASKLIST_ENTRY + "" + id + ";" + table_id + ";" + user_id + ";" + description + ";" + state;
    }

    @Override
    protected boolean mysql_update() {
        if(id == 0) {
            String resp = connect("tasklist/entry/create.php","&table_id="+table_id+"&description="+description+"&state="+state);
            if(resp.substring(0,3).equals("suc")) {
                id = Integer.parseInt(resp.substring(3));
                return true;
            }
        }else {
            String resp = connect("tasklist/entry/update.php","&entry_id=" + id + "&description=" + description + "&state=" + state);
            return resp.equals("suc");
        }
        return false;
    }


    protected static class Delete extends Entry {

        protected int entry_id;

        protected Delete(int entry_id) {
            this.entry_id = entry_id;
        }

        protected Delete(String line) {
            entry_id = Integer.parseInt(line);
        }

        @Override
        protected boolean mysql_update() {
            String resp = connect("tasklist/entry/delete.php", "&entry_id=" + entry_id);
            return resp.equals("suc");
        }

        @Override
        protected String getConManString() {
            return TYPE_TASKLIST_ENTRY_DELETE + "" + entry_id;
        }

    }
}
