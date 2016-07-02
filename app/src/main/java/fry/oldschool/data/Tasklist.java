package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class Tasklist extends MySQL implements Fryable {

    public int drag_id;

    protected int user_id;

    protected byte state;

    public String name;

    public ArrayList<TasklistEntry> entries = new ArrayList<>();

    public ArrayList<Share> sharedContacts = new ArrayList<>();

    public static Tasklist create(String name) {
        Tasklist tl = new Tasklist(0,USER_ID,(byte)0,name);
        ConnectionManager.add(tl);
        return tl;
    }

    protected Tasklist() { }

    public Tasklist(int id, int user_id, byte state, String name) {
        this.type = TYPE_TASKLIST;
        this.id = id;
        this.user_id = user_id;
        this.state = state;
        this.name = name;
        if(id == 0) {
            ConnectionManager.add(this);
        }
    }

    @Override
    protected boolean mysql() {
        String resp = getLine(DIR_TASKLIST + "create.php", "&name=" + name + "&state=" + state);
        if(resp != null) {
            id = Integer.parseInt(resp);
            for(TasklistEntry ent : entries) {
                ent.table_id = id;
                ConnectionManager.add(ent);
            }
            return true;
        }
        return false;
    }

    @Override
    public void writeTo(FryFile fry) {
        fry.write(id);
        fry.write(user_id);
        fry.write(state);
        fry.write(name);
        fry.write(entries.toArray());
    }

    @Override
    public void readFrom(FryFile fry) {
        id = fry.getInt();
        user_id = fry.getInt();
        state = fry.getByte();
        name = fry.getString();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            TasklistEntry ent = new TasklistEntry();
            ent.readFrom(fry);
            ent.table_id = id;
            entries.add(ent);
        }
    }

    public boolean isOwner() {
        return ( user_id == MySQL.USER_ID );
    }

    public Contact getOwner() {
        if(isOwner()) {
            return null;
        }
        return ContactList.getContactByUserId(user_id);
    }

    public boolean isDone() {
        return ( state == 1 );
    }

    public void rename(String name) {
        this.name = name;
        OfflineEntry.update(TYPE_TASKLIST, id);
    }

    public ArrayList<ShareGroup> getShared() {
        ArrayList<ShareGroup> grpList = new ArrayList<>(ContactList.groups.size());
        ContactGroup allc = ContactList.groups.get(ContactList.groups.size()-1);
        ShareGroup alls = new ShareGroup(allc.name);

        for(Contact cont : allc.contacts) {
            Share s = new Share(id, cont);
            alls.contacts.add(s);
        }

        for(Share s : sharedContacts) {
            Share si = alls.findShareByUserId(s.user_id);
            if(si != null) {
                si.id = s.id;
                si.permission = s.permission;
            }
        }

        for(ContactGroup grpc : ContactList.groups) {
            ShareGroup grps = new ShareGroup(grpc.name);
            for(Contact cont : grpc.contacts) {
                Share s = alls.findShareByUserId(cont.user_id);
                if(s != null) {
                    grps.contacts.add(s);
                }
            }
            if(grps.contacts.size() > 0) {
                grpList.add(grps);
            }
        }
        return grpList;
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
        TasklistEntry ent = new TasklistEntry(task,state);
        entries.add(ent);
        if(id > 0) {
            ConnectionManager.add(ent);
        }
    }

    public void addEntry(int index,String task,boolean state) {
        TasklistEntry ent = new TasklistEntry(task,state);
        entries.add(index,ent);
        if(id > 0) {
            ConnectionManager.add(ent);
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

    public void delete() {
        TasklistManager.removeTasklist(id);
        OfflineEntry.delete(TYPE_TASKLIST, id);
    }

    public void delete(int index) {
        TasklistEntry ent = entries.remove(index);
        OfflineEntry.delete(TYPE_TASKLIST, id);
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

    public String getUpdateString() {
        return ("&id=" + id + "&name=" + name + "&state=" + state);
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

    public Tasklist backup() {
        Tasklist tl = new Tasklist(id, user_id, state, name);
        for(TasklistEntry ent : entries) {
            tl.entries.add(ent.backup());
        }
        for(Share share : sharedContacts) {
            tl.sharedContacts.add(share.backup());
        }
        return tl;
    }

}
