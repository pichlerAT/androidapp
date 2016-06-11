package fry.oldschool.utils;

public class TaskListEntry extends Entry {

    protected int id;

    protected int table_id;

    protected int user_id;

    protected byte state;

    public String description;

    protected TaskListEntry(String description,boolean state) {
        this(0,0,USER_ID,description,( state ? (byte)0 : (byte)1 ));
    }

    protected TaskListEntry(int id,int table_id,int user_id,String description,byte state) {
        this.id = id;
        this.table_id = table_id;
        this.user_id = user_id;
        this.description = description;
        this.state = state;
    }

    protected TaskListEntry(String line) {
        String[] r = line.split(S);
        id = Integer.parseInt(r[0]);
        user_id = Integer.parseInt(r[1]);
        table_id = Integer.parseInt(r[2]);
        description = r[3];
        state = Byte.parseByte(r[4]);
    }

    protected void create(int table_id) {
        if(id == 0) {
            this.table_id = table_id;
            App.conMan.add(this);
        }
    }

    public void change(String description,boolean state) {
        this.description = description;
        this.state = ( state ? (byte)1 : (byte)0 );
        App.conMan.add(this);
    }

    public void change(boolean state) {
        this.state = ( state ? (byte)1 : (byte)0 );
        App.conMan.add(this);
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
        return App.conLis.findContactByUserId(user_id);
    }

    @Override
    protected boolean mysql_update() {
        String resp = connect("tasklist/entry/create.php","&table_id="+table_id+"&description="+description+"&state="+state);
        if(resp.substring(0,3).equals("suc")) {
            id = Integer.parseInt(resp.substring(3));
            return true;
        }
        return false;
    }

    @Override
    protected String getConManString() {
        return null;
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

    protected static class Update extends Entry {

        protected int entry_id;

        protected Update(int entry_id) {
            this.entry_id = entry_id;
        }

        protected Update(String line) {
            entry_id = Integer.parseInt(line);
        }

        @Override
        protected boolean mysql_update() {
            TaskListEntry entry = TaskListManager.findEntryById(entry_id);
            if(entry == null) {
                return true;
            }
            String resp = connect("tasklist/entry/update.php", "&entry_id=" + entry_id + "&description=" + entry.description + "&state=" + entry.state);
            return resp.equals("suc");
        }

        @Override
        protected String getConManString() {
            return TYPE_TASKLIST_ENTRY_UPDATE + "" + entry_id;
        }

    }

}
