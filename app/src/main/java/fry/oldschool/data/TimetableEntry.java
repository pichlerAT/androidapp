package fry.oldschool.data;

import fry.oldschool.utils.App;
import fry.oldschool.utils.DateTime;
import fry.oldschool.utils.Time;

public class TimetableEntry extends Entry {

    protected int id;

    protected int category_id;

    protected int user_id;

    protected String title;

    protected String description;

    protected DateTime start;

    protected Time.Int duration;

    protected byte repeat;

    public static TimetableEntry create(String title,String description,DateTime start,Time.Int duration,byte repeat,TimetableCategory category) {
        TimetableEntry ent = new TimetableEntry(0,category.id,USER_ID,title,description,start,duration,repeat);
        ConnectionManager.add(ent);
        return ent;
    }

    protected TimetableEntry(int id,int category_id,int user_id,String title,String description,short date_start,short time_start,int duration,byte repeat) {
        this(id,category_id,user_id,title,description,new DateTime(date_start,time_start),new Time.Int(duration),repeat);
    }

    protected TimetableEntry(int id,int category_id,int user_id,String title,String description,DateTime start,Time.Int duration,byte repeat) {
        this.id = id;
        this.category_id = category_id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.duration = duration;
        this.repeat = repeat;
    }

    protected TimetableEntry(String line) {
        String[] r = line.split(S);
        id = Integer.parseInt(r[0]);
        user_id = Integer.parseInt(r[1]);
        category_id = Integer.parseInt(r[2]);
        title = r[3];
        description = r[4];
        start = new DateTime(Short.parseShort(r[5]),Short.parseShort(r[6]));
        duration = new Time.Int(Integer.parseInt(r[7]));
        repeat = Byte.parseByte(r[8]);
    }

    @Override
    protected boolean mysql() {
        String resp = getLine("calendar/entries/create.php", "&category_id="+category_id+"&title="+title+"&description="+description+"&date_start="+start.date.getShort()
                +"&time_start="+start.time.time+"&duration="+duration.time+"&repeat="+repeat);
        if (resp.substring(0, 3).equals("suc")) {
            id = Integer.parseInt(resp.substring(3));
            return true;
        }
        return false;
    }

    @Override
    protected String getConManString() {
        return null;
        //return TYPE_CALENDAR_ENTRY + "" + id + S + category_id + S + user_id + S + title + S + description + S + start.date.getShort() + S + start.time.time
        //        + S + duration.time + S + repeat;
    }

    public void delete() {
        //App.conMan.add(new Delete(Entry.TYPE_CALENDAR_ENTRY_DELETE,id));
    }

    public String getUpdateString() {
        return "&entry_id="+id+"&category_id="+category_id+"&title="+title+"&description="+description+"&date_start="
                +start.date.getShort()+"&time_start="+start.time.time+"&duration="+duration.time+"&repeat="+repeat;
    }

}
