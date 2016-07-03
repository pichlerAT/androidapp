package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.Date;
import fry.oldschool.utils.DateSpan;
import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;
import fry.oldschool.utils.Time;

public class TimetableEntry extends MySQLEntry implements Fryable {

    protected byte addition;

    protected int category_id;

    protected String title;

    protected String description;

    protected DateSpan span;

    protected ShareList shareList;

    public static TimetableEntry create(byte addition, String title, String description, DateSpan span, TimetableCategory category) {
        TimetableEntry ent = new TimetableEntry(0, USER_ID, addition, category.id, title, description, span);
        if(ent.category_id == 0) {
            category.addOfflineEntry(ent);
        }else {
            Timetable.entries.add(ent);
            ent.create();
        }
        return ent;
    }

    protected TimetableEntry(FryFile fry) {
        super(fry);
        addition = fry.getByte();
        category_id = fry.getInt();
        title = fry.getString();
        description = fry.getString();
        span = new DateSpan(fry);

        if(id != 0) {
            shareList = new ShareList(TYPE_CALENDAR_ENTRY, id);
        }
    }

    protected TimetableEntry(int id, int user_id, byte addition, short date_start, short time_start, int duration, int category_id, String title, String description) {
        super(TYPE_CALENDAR_ENTRY, id, user_id);
        this.addition = addition;
        this.category_id = category_id;
        this.title = title;
        this.description = description;
        this.span = new DateSpan(date_start, time_start, duration);

        if(id != 0) {
            shareList = new ShareList(TYPE_CALENDAR_ENTRY, id);
        }
    }

    protected TimetableEntry(int id, int user_id, byte addition, int category_id, String title, String description, DateSpan span) {
        this(id, user_id, addition, span.getDateStart(), span.getTimeStart(), span.getDuration(), category_id, title, description);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TimetableEntry) {
            TimetableEntry e = (TimetableEntry) o;
            return (e.id == id && e.title.equals(title) && e.description.equals(description) && e.span.equals(span) && e.addition == addition);
        }
        return false;
    }

    @Override
    public TimetableEntry backup() {
        return new TimetableEntry(id, user_id, addition, category_id, title, description, span);
    }

    @Override
    protected boolean mysql_create() {
        String resp = getLine(DIR_CALENDAR_ENTRY+"create.php","&category_id="+category_id+"&title="+title+"&description="+description
                +"&date_start="+span.getDateStart()+"&time_start="+span.getTimeStart()+"&duration="+span.getDuration()+"&addition="+addition);
        if(resp != null) {
            id = Integer.parseInt(resp);

            if(id != 0) {
                shareList = new ShareList(TYPE_CALENDAR_ENTRY, id);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        return (getLine(DIR_CALENDAR_ENTRY+"update.php", "&entry_id="+id+"&category_id="+category_id+"&title="+title+"&description="+description
                +"&date_start="+span.getDateStart()+"&time_start="+span.getTimeStart()+"&duration="+span.getDuration()+"&addition="+addition) != null);
    }

    @Override
    protected boolean mysql_delete() {
        return (getLine(DIR_CALENDAR_ENTRY+"delete.php", "&id="+id) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) {
        TimetableEntry e = (TimetableEntry) mysql;
        title = e.title;
        description = e.description;
        span = e.span;
    }

    @Override
    public boolean canEdit() {
        return isOwner();
    }


    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);
        fry.write(addition);
        fry.write(category_id);
        fry.write(title);
        fry.write(description);
        span.writeTo(fry);
    }

    public void shareWith(Contact cont) {
        ConnectionManager.add(new Share(TYPE_CALENDAR_ENTRY, id, cont));
    }

    public void shareWith(Contact cont, byte permission) {
        ConnectionManager.add(new Share(TYPE_CALENDAR_ENTRY, permission, id, cont));
    }

    @Override
    public void delete() {
        super.delete();
        Timetable.entries.remove(this);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDateInsideSpan(Date date) {
        return span.isInsideSpan(date);
    }

    public boolean isSpanOverlapping(DateSpan span) {
        return span.isOverlapping(span);
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
