package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class Tasklist extends MySQLEntry implements Fryable {

    public int drag_id;

    protected byte state;

    protected String name;

    protected ArrayList<TasklistEntry> entries = new ArrayList<>();

    protected ShareList shareList;

    public static Tasklist create(String name) {
        Tasklist tl = new Tasklist(0,USER_ID,(byte)0,name);
        tl.create();
        TasklistManager.Tasklists.add(tl);
        return tl;
    }

    protected Tasklist(FryFile fry) {
        super(fry);
        state = fry.getByte();
        name = fry.getString();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            TasklistEntry ent = new TasklistEntry(fry);
            ent.table_id = id;
            entries.add(ent);
        }

        if(id != 0) {
            shareList = new ShareList(TYPE_TASKLIST, id);
        }
    }

    protected Tasklist(int id, int user_id, byte state, String name) {
        super(TYPE_TASKLIST, id, user_id);
        this.state = state;
        this.name = name;

        if(id != 0) {
            shareList = new ShareList(TYPE_TASKLIST, id);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Tasklist) {
            Tasklist t = (Tasklist) o;
            if(t.id != id || t.state != state || !t.name.equals(name) || t.entries.size() != entries.size()) {
                return false;
            }
            for(int i=0; i<entries.size(); ++i) {
                if(!t.entries.get(i).equals(entries.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Tasklist backup() {
        Tasklist tl = new Tasklist(id, user_id, state, name);
        for(TasklistEntry ent : entries) {
            tl.entries.add(ent.backup());
        }
        return tl;
    }

    @Override
    protected boolean mysql_create() {
        String resp = getLine(DIR_TASKLIST + "create.php", "&name=" + name + "&state=" + state);
        if(resp != null) {
            id = Integer.parseInt(resp);
            shareList = new ShareList(TYPE_TASKLIST, id);
            for(TasklistEntry ent : entries) {
                ent.table_id = id;
                ent.create();
            }
            return true;
        }
        return false;
    }
    @Override
    protected boolean mysql_update() {
        return (getLine(DIR_TASKLIST + "update.php", "&id="+id+"&name="+name+"&state="+state) != null);
    }

    @Override
    protected boolean mysql_delete() {
        return (getLine(DIR_TASKLIST + "delete.php", "&id="+id) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) {
        Tasklist t = (Tasklist) mysql;
        state = t.state;
        name = t.name;
        entries = t.entries;
    }

    @Override
    public boolean canEdit() {
        return (isOwner() || isSharedWithUserId(USER_ID));
    }

    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);
        fry.write(state);
        fry.write(name);
        fry.write(entries);
    }

    public int getNoEntries() {
        return entries.size();
    }

    public TasklistEntry getEntry(int index) {
        return entries.get(index);
    }

    public String getName() {
        return name;
    }

    public Contact getOwner() {
        if(isOwner()) {
            return null;
        }
        return ContactList.getContactByUserId(user_id);
    }

    public ArrayList<TasklistEntry> getEntries() {
        return entries;
    }

    public boolean isDone() {
        return ( state == 1 );
    }

    public void rename(String name) {
        this.name = name;
        update();
    }

    public boolean isSharedWithUserId(int id) {
        return shareList.hasUserId(id);
    }

    public ArrayList<ContactGroup> getShared() {
        return shareList.getShareList();
    }

    public void addShare(Contact contact) {
        Share share = new Share(TYPE_TASKLIST,id,contact);
        shareList.addShare(share);
        ConnectionManager.add(share);
    }

    public void addShare(ArrayList<Contact> contacts) {
        for(Contact contact : contacts) {
            addShare(contact);
        }
    }

    public void addShare(Contact contact,byte permission) {
        Share share = new Share(TYPE_TASKLIST, permission, id, contact);
        shareList.addShare(share);
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

    public void removeShare(Share share) {
        if(shareList.remove(share)) {
            share.delete();
        }
    }

    public void removeShare(ArrayList<Share> shares) {
        for(Share share : shares) {
            removeShare(share);
        }
    }

    public void addEntry(String task,boolean state) {
        TasklistEntry ent = new TasklistEntry(task,state);
        entries.add(ent);
        if(id > 0) {
            ent.create();
        }
    }

    public void addEntry(int index,String task,boolean state) {
        TasklistEntry ent = new TasklistEntry(task,state);
        entries.add(index,ent);
        if(id > 0) {
            ent.create();
        }
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

    @Override
    public void delete() {
        super.delete();
        TasklistManager.removeTasklist(id);
    }

    public void delete(int index) {
        entries.remove(index).delete();
    }

    protected String getEntryStrings() {
        if(entries.size() <= 0) {
            return "n";
        }
        String s = "";
        for(TasklistEntry e : entries) {
            s += e.id + S + e.user_id + S + e.description + S + e.state + S;
        }
        return s;
    }

    public boolean equals(Tasklist tl) {
        if(state != tl.state || entries.size() != tl.entries.size() || !name.equals(tl.name)) {
            return false;
        }
        for(int i=0; i<entries.size(); ++i) {
            if(!entries.get(i).equals(tl.entries.get(i))) {
                return false;
            }
        }
        return true;
    }

}
