package fry.oldschool.data;

import fry.oldschool.utils.DateTime;
import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;
import fry.oldschool.utils.Time;

public class TimetableEntry extends MySQL implements Fryable {

    public int user_id;

    public int category_id;

    public String title;

    public String description;

    public DateTime start;

    public Time duration;

    public byte addition;

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

    protected TimetableEntry() { }

    protected TimetableEntry(int id,int user_id,int category_id,String title,String description,short date_start,short time_start,int duration,byte addition) {
        this(id,user_id,category_id,title,description,new DateTime(date_start,time_start),new Time(duration),addition);
    }

    protected TimetableEntry(int id,int user_id,int category_id,String title,String description,DateTime start,Time duration,byte addition) {
        this.id = id;
        this.user_id = user_id;
        this.category_id = category_id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.duration = duration;
        this.addition = addition;
        type = TYPE_CALENDAR_ENTRY;
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

    public String getUpdateString() {
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
        OfflineEntry.delete(TYPE_CALENDAR_ENTRY, id);
    }

    public TimetableEntry backup() {
        return new TimetableEntry(id, user_id, category_id, title, description, start.date.getShort(), start.time.getShort(), duration.time, addition);
    }

}
