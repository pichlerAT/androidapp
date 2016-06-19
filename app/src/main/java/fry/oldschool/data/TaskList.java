package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.App;
import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class TaskList extends OnlineEntry implements Fryable {

    public int drag_id;

    protected int user_id;

    protected byte state;

    public String name;

    public ArrayList<TaskListEntry> entries = new ArrayList<>();

    public ArrayList<Share> sharedContacts = new ArrayList<>();

    public static TaskList create(String name) {
        return new TaskList(0,USER_ID,(byte)0,name);
    }

    public static TaskList createBackup(int id, int user_id, byte state, String name) {
        TaskList tl = new TaskList(id);
        tl.user_id = user_id;
        tl.state = state;
        tl.name = name;
        return tl;
    }

    public TaskList(int id, int user_id, byte state, String name) {
        this.type = TYPE_TASKLIST;
        this.id = id;
        this.user_id = user_id;
        this.state = state;
        this.name = name;
        if(id == 0) {
            ConnectionManager.add(this);
        }
    }

    public TaskList(int id) {
        this.id = id;
    }

    @Override
    protected boolean mysql() {
        String resp = getLine(DIR_TASKLIST + "create.php", "&name=" + name + "&state=" + state);
        if(resp.substring(0,3).equals("suc")) {
            id = Integer.parseInt(resp.substring(3));
            for(TaskListEntry ent : entries) {
                ent.table_id = id;
                ConnectionManager.add(ent);
            }
            return true;
        }
        return false;
    }

    @Override
    public void writeTo(FryFile file) {
        file.write(id);
        file.write(user_id);
        file.write(state);
        file.write(name);
        file.write(entries.toArray());
    }

    public boolean isOwner() {
        return ( user_id == OfflineEntry.USER_ID );
    }

    public Contact getOwner() {
        if(isOwner()) {
            return null;
        }
        return ContactList.findContactById(user_id);
    }

    public boolean isDone() {
        return ( state == 1 );
    }

    public void rename(String name) {
        this.name = name;
        ConnectionManager.add(new Update(TYPE_TASKLIST,id));
    }

    public void loadShared() {
        ConnectionManager.add(new GetShared(id));
    }

    public void addShare(Contact contact) {
        Share share = new Share(TYPE_TASKLIST,id,contact);
        sharedContacts.add(share);
        ConnectionManager.add(share);
    }

    public void addShare(ArrayList<Contact> contacts) {
        for(Contact contact : contacts) {
            addShare(contact);
        }
    }

    public void addShare(Contact contact,byte permission) {
        Share share = new Share(TYPE_TASKLIST,id,permission,contact);
        sharedContacts.add(share);
        ConnectionManager.add(share);
    }

    public void addShare(ArrayList<Contact> contacts,byte permission) {
        for(Contact contact : contacts) {
            addShare(contact,permission);
        }
    }

    public void addShare(ArrayList<Contact> contacts,byte[] permissions) {
        for(int i=0;i<contacts.size();++i) {
            addShare(contacts.get(i),permissions[i]);
        }
    }

    public void removeShare(int index) {
        Share share = sharedContacts.remove(index);
        if(share != null) {
            share.delete();
        }
    }

    public void removeShare(Share share) {
        if(sharedContacts.remove(share)) {
            share.delete();
        }
    }

    public void removeShare(ArrayList<Share> shares) {
        for(Share share : shares) {
            removeShare(share);
        }
    }

    public void addEntry(String task,boolean state) {
        TaskListEntry ent = new TaskListEntry(task,state);
        entries.add(ent);
    }

    public void addEntry(int index,String task,boolean state) {
        TaskListEntry ent = new TaskListEntry(task,state);
        entries.add(index,ent);
    }

    public void change(boolean state) {
        this.state = ( state ? (byte)1 : (byte)0 );
    }

    public void change(String name,boolean state) {
        this.name = name;
        this.state = ( state ? (byte)1 : (byte)0 );
    }

    public boolean isDone(int index) {
        return entries.get(index).isDone();
    }

    public int length() {
        return entries.size();
    }

    public String getTaskName(int index) {
        return entries.get(index).description;
    }

    public void delete() {
        App.Tasklists.remove(this);
        ConnectionManager.add(new Delete(TYPE_TASKLIST,id));
    }

    public void delete(int index) {
        TaskListEntry ent = entries.remove(index);
        ConnectionManager.add(new Delete(TYPE_TASKLIST, ent.id));
    }

    protected String getEntryStrings() {
        if(entries.size() <= 0) {
            return "n";
        }
        String s = "";
        for(TaskListEntry e : entries) {
            s += e.id + S + e.user_id + S + e.description + S + e.state + S;
        }
        return s;
    }

    public String getUpdateString() {
        return ("&table_id="+id+"&name="+name+"&state="+state);
    }

    protected class GetShared extends OnlineEntry {

        protected GetShared(int table_id) {
            this.id = id;
        }

        @Override
        protected boolean mysql() {
            String resp = getLine(DIR_TASKLIST_SHARE + "get.php","&table_id="+id);

            if(!resp.substring(0,3).equals("suc")) {
                return false;
            }

            String[] r = resp.substring(3).split(S);
            sharedContacts = new ArrayList<>();
            for(int i=2;i<r.length;i+=3) {
                Contact c = ContactList.findContactByUserId(Integer.parseInt(r[i-1]));
                if(c != null) {
                    sharedContacts.add(new Share(TYPE_TASKLIST,Integer.parseInt(r[i-2]),Byte.parseByte(r[i]),c));
                }
            }

            sharedContacts.get(0).allowEdit(true);

            return true;
        }

    }

}
