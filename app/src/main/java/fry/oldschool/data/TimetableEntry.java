package fry.oldschool.data;

import java.util.ArrayList;

import fry.oldschool.utils.Date;
import fry.oldschool.utils.DateSpan;
import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;
import fry.oldschool.utils.Logger;
import fry.oldschool.utils.Time;

public class TimetableEntry extends MySQLEntry implements Fryable {

    protected byte addition;

    protected int category_id;

    protected String title;

    protected String description;

    protected DateSpan span;

    public ShareList sharedContacts;

    public static TimetableEntry create(byte addition, String title, String description, DateSpan span, TimetableCategory category) {
        Logger.Log("TimetableEntry", "create(byte,String,String,DateSpan,TimetableCategory)");
        TimetableEntry ent;
        if(category == null) {
            ent = new TimetableEntry(0, USER_ID, addition, 0, title, description, span);
        }else {
            ent = new TimetableEntry(0, USER_ID, addition, category.id, title, description, span);
            if (ent.category_id == 0) {
                category.addOfflineEntry(ent);
                return ent;
            }
        }
        Timetable.entries.add(ent);
        ent.create();
        return ent;
    }

    protected TimetableEntry(FryFile fry) {
        super(fry);
        Logger.Log("TimetableEntry", "TimetableEntry(FryFile)");
        addition = fry.getByte();
        category_id = fry.getInt();
        title = fry.getString();
        description = fry.getString();
        span = new DateSpan(fry);

        if(id != 0) {
            sharedContacts = new ShareList(TYPE_CALENDAR_ENTRY, id);
        }
    }

    protected TimetableEntry(int id, int user_id, byte addition, short date_start, short time_start, int duration, int category_id, String title, String description) {
        super(TYPE_CALENDAR_ENTRY, id, user_id);
        Logger.Log("TimetableEntry", "TimetableEntry(int,int,byte,short,short,int,int,String,String)");
        this.addition = addition;
        this.category_id = category_id;
        this.title = title;
        this.description = description;
        this.span = new DateSpan(date_start, time_start, duration);

        if(id != 0) {
            sharedContacts = new ShareList(TYPE_CALENDAR_ENTRY, id);
        }
    }

    protected TimetableEntry(int id, int user_id, byte addition, int category_id, String title, String description, DateSpan span) {
        this(id, user_id, addition, span.getDateStart(), span.getTimeStart(), span.getDuration(), category_id, title, description);
        Logger.Log("TimetableEntry", "TimetableEntry(int,int,byte,int,String,String,DateSpan)");
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("TimetableEntry", "equals(Object)");
        if(o instanceof TimetableEntry) {
            TimetableEntry e = (TimetableEntry) o;
            return (e.id == id && e.title.equals(title) && e.description.equals(description) && e.span.equals(span) && e.addition == addition);
        }
        return false;
    }

    @Override
    public TimetableEntry backup() {
        Logger.Log("TimetableEntry", "backup()");
        return new TimetableEntry(id, user_id, addition, category_id, title, description, span);
    }

    @Override
    protected boolean mysql_create() {
        Logger.Log("TimetableEntry", "mysql_create()");
        String resp = getLine(DIR_CALENDAR_ENTRY+"create.php","&category_id="+category_id+"&title="+title+"&description="+description
                +"&date_start="+span.getDateStart()+"&time_start="+span.getTimeStart()+"&duration="+span.getDuration()+"&addition="+addition);
        if(resp != null) {
            id = Integer.parseInt(resp);

            if(id != 0) {
                sharedContacts = new ShareList(TYPE_CALENDAR_ENTRY, id);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        Logger.Log("TimetableEntry", "mysql_update()");
        return (getLine(DIR_CALENDAR_ENTRY+"update.php", "&id="+id+"&category_id="+category_id+"&title="+title+"&description="+description
                +"&date_start="+span.getDateStart()+"&time_start="+span.getTimeStart()+"&duration="+span.getDuration()+"&addition="+addition) != null);
    }

    @Override
    protected boolean mysql_delete() {
        Logger.Log("TimetableEntry", "mysql_delete()");
        return (getLine(DIR_CALENDAR_ENTRY+"delete.php", "&id="+id) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) {
        Logger.Log("TimetableEntry", "synchronize(MySQL)");
        TimetableEntry e = (TimetableEntry) mysql;
        title = e.title;
        description = e.description;
        span = e.span;
    }

    @Override
    public boolean canEdit() {
        Logger.Log("TimetableEntry", "canEdit()");
        return isOwner();
    }


    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("TimetableEntry", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.write(addition);
        fry.write(category_id);
        fry.write(title);
        fry.write(description);
        span.writeTo(fry);
    }

    @Override
    public void delete() {
        Logger.Log("TimetableEntry", "delete()");
        super.delete();
        Timetable.entries.remove(this);
    }

    public String getTitle() {
        Logger.Log("TimetableEntry", "getTitle()");
        return title;
    }

    public String getDescription() {
        Logger.Log("TimetableEntry", "getDescription()");
        return description;
    }

    public boolean isDateInsideSpan(Date date) {
        Logger.Log("TimetableEntry", "isDateInsideSpan(Date)");
        return span.isInsideSpan(date);
    }

    public boolean isSpanOverlapping(DateSpan span) {
        Logger.Log("TimetableEntry", "isSpanOverlapping(DateSpan)");
        return span.isOverlapping(span);
    }

}
