package fry.oldschool.utils;

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
        App.conMan.add(ent);
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

    @Override
    protected String getConManString() {
        return TYPE_CALENDAR_ENTRY + "" + id + ";" + category_id + ";" + user_id + ";" + title + ";" + description + ";" + start.date.getShort() + ";" + start.time.time + ";" + duration.time + ";" + repeat;
    }

    @Override
    protected boolean mysql() {
        if(id == 0) {
            String resp = getLine("calendar/entry/create.php", "&category_id=" + category_id + "&title=" + title + "&description=" + description + "&date_start=" + start.date.getShort() + "&time_start=" + start.time.time + "&duration=" + duration.time + "&repeat=" + repeat);
            if (resp.substring(0, 3).equals("suc")) {
                id = Integer.parseInt(resp.substring(3));
                return true;
            }
            return false;
        }
        String resp = getLine("calendar/entry/update.php","&entry_id="+id+"&category_id="+category_id+"&title="+title+"&description="+description+"&date_start="+start.date.getShort()+"&time_start="+start.time.time+"&duration="+duration.time+"&repeat="+repeat);
        return resp.equals("suc");
    }

    public void delete() {
        App.conMan.add(new Delete(id));
    }

    protected static class Delete extends Entry {

        protected int id;

        protected Delete(int id) {
            this.id = id;
        }

        @Override
        protected String getConManString() {
            return TYPE_CALENDAR_ENTRY_DELETE + "" + id;
        }

        @Override
        protected boolean mysql() {
            String resp = getLine("calendar/entry/delete.php","entry_id="+id);
            return resp.equals("suc");
        }
    }

}
