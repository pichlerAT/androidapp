package fry.oldschool.data;

import fry.oldschool.utils.DateTime;
import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;
import fry.oldschool.utils.Time;

public class TimetableEntry extends MySQL implements Fryable {

    protected int category_id;

    protected String title;

    protected String description;

    protected DateTime start;

    protected Time duration;

    protected byte addition;

    public static TimetableEntry create(String title, String description, DateTime start, Time duration, byte addition, TimetableCategory category) {
        TimetableEntry ent = new TimetableEntry(0,USER_ID,category.id,title,description,start,duration,addition);
        if(ent.category_id == 0) {
            category.addOfflineEntry(ent);
        }else {
            Timetable.entries.add(ent);
            ConnectionManager.add(ent);
        }
        return ent;
    }

    protected TimetableEntry() {
        super(TYPE_CALENDAR_ENTRY, 0 ,0);
    }

    protected TimetableEntry(int id,int user_id,int category_id,String title,String description,short date_start,short time_start,int duration,byte addition) {
        this(id,user_id,category_id,title,description,new DateTime(date_start,time_start),new Time(duration),addition);
    }

    protected TimetableEntry(int id,int user_id,int category_id,String title,String description,DateTime start,Time duration,byte addition) {
        super(TYPE_CALENDAR_ENTRY, id, user_id);
        this.category_id = category_id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.duration = duration;
        this.addition = addition;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TimetableEntry) {
            TimetableEntry e = (TimetableEntry) o;
            return (e.id == id && e.title.equals(title) && e.description.equals(description) && e.start.equals(start) && e.duration.equals(duration) && e.addition == addition);
        }
        return false;
    }

    @Override
    public TimetableEntry backup() {
        return new TimetableEntry(id, user_id, category_id, title, description, start.date.getShort(), start.time.getShort(), duration.time, addition);
    }

    @Override
    protected boolean mysql() {
        String resp = getLine(DIR_CALENDAR_ENTRY+"create.php","&category_id="+category_id+"&title="+title+"&description="+description
                +"&date_start="+start.date.getShort()+"&time_start="+start.time.time+"&duration="+duration.time+"&addition="+addition);
        if(resp != null) {
            id = Integer.parseInt(resp);
            return true;
        }
        return false;
    }

    @Override
    protected void synchronize(MySQL mysql) {
        TimetableEntry e = (TimetableEntry) mysql;
        title = e.title;
        description = e.description;
        start = e.start;
        duration = e.duration;
        addition = e.addition;
    }

    @Override
    public boolean canEdit() {
        return isOwner();
    }


    @Override
    public void writeTo(FryFile fry) {
        fry.write(id);
        fry.write(user_id);
        fry.write(category_id);
        fry.write(title);
        fry.write(description);
        fry.write(start.date.getShort());
        fry.write(start.time.getShort());
        fry.write(duration.time);
        fry.write(addition);
    }

    @Override
    public void readFrom(FryFile fry) {
        id = fry.getInt();
        user_id = fry.getInt();
        category_id = fry.getInt();
        title = fry.getString();
        description = fry.getString();
        start = new DateTime(fry.getShort(), fry.getShort());
        duration = new Time(fry.getInt());
        addition = fry.getByte();
    }

    protected String getUpdateString() {
        return ("&entry_id="+id+"&category_id="+category_id+"&title="+title+"&description="+description
                +"&date_start="+start.date.getShort()+"&time_start="+start.time.time+"&duration="+duration.time+"&addition="+addition);
    }

    public void shareWith(Contact cont) {
        ConnectionManager.add(new Share(TYPE_CALENDAR_ENTRY, id, cont));
    }

    public void shareWith(Contact cont, byte permission) {
        ConnectionManager.add(new Share(TYPE_CALENDAR_ENTRY, id, permission, cont));
    }

    public void delete() {
        OfflineEntry.delete(this);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public DateTime getStart() {
        return start;
    }

    public Time getDuration() {
        return duration;
    }

}
