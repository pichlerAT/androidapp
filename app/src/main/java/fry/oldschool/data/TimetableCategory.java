package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class TimetableCategory extends MySQLEntry implements Fryable {

    protected String name;

    protected ArrayList<TimetableEntry> offline_entries = new ArrayList<>();

    protected ShareList shareList;

    public static TimetableCategory create(String name) {
        TimetableCategory cat = new TimetableCategory(0,USER_ID,name);
        cat.create();
        Timetable.categories.add(cat);
        return cat;
    }

    protected TimetableCategory(FryFile fry) {
        super(fry);
        name = fry.getString();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            offline_entries.add(new TimetableEntry(fry));
        }

        if(id != 0) {
            shareList = new ShareList(TYPE_TASKLIST, id);
        }
    }

    protected TimetableCategory(int id,int user_id,String name) {
        super(TYPE_CALENDAR_CATEGORY, id, user_id);
        this.name = name;

        if(id != 0) {
            shareList = new ShareList(TYPE_TASKLIST, id);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TimetableCategory) {
            TimetableCategory c = (TimetableCategory) o;
            return (c.id == id && c.name.equals(name));
        }
        return false;
    }

    @Override
    public TimetableCategory backup() {
        return new TimetableCategory(id, user_id, name);
    }

    @Override
    protected boolean mysql_create() {
        String resp = getLine(DIR_CALENDAR_CATEGORY + "create.php", "&name="+name);
        if(resp != null) {
            id = Integer.parseInt(resp);
            shareList = new ShareList(TYPE_CALENDAR_CATEGORY, id);

            for(TimetableEntry ent : offline_entries) {
                ent.category_id = id;
                ent.create();
            }
            offline_entries = new ArrayList<>();
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        return (getLine(DIR_CALENDAR_CATEGORY + "update.php", "&category_id="+id+"&name="+name) != null);
    }

    @Override
    protected boolean mysql_delete() {
        return (getLine(DIR_CALENDAR_CATEGORY + "delete.php", "&id="+id) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) {
        TimetableCategory c = (TimetableCategory) mysql;
        name = c.name;
    }

    @Override
    public boolean canEdit() {
        return isOwner();
    }

    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);
        fry.write(name);
        fry.write(offline_entries);
    }

    public String getName() {
        return name;
    }

    protected void addOfflineEntry(TimetableEntry entry) {
        offline_entries.add(entry);
    }

    public void setName(String name) {
        this.name = name;
        update();
    }

    @Override
    public void delete() {
        super.delete();
        Timetable.categories.remove(this);
    }

    public boolean isSharedWithUserId(int id) {
        return shareList.hasUserId(id);
    }

    public ArrayList<ContactGroup> getShared() {
        return shareList.getShareList();
    }

    public void addShare(Contact contact) {
        Share share = new Share(TYPE_CALENDAR_CATEGORY, id, contact);
        shareList.addShare(share);
        ConnectionManager.add(share);
    }

    public void addShare(ArrayList<Contact> contacts) {
        for(Contact contact : contacts) {
            addShare(contact);
        }
    }

    public void addShare(Contact contact,byte permission) {
        Share share = new Share(TYPE_CALENDAR_CATEGORY, permission, id, contact);
        addShare(share);
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

}
