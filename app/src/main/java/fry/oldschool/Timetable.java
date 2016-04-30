package fry.oldschool;

/**
 * Created by Stefan on 30.04.2016.
 */
public class Timetable {

    public int id;

    public int owner_id;

    public String name;

    public int[] entry_id;

    public int[] user_id;

    public Date[] date;

    public Time[] time_start;

    public Time[] time_end;

    public Timetable(int id,int owner_id,String name) {
        this.id = id;
        this.owner_id = owner_id;
        this.name = name;
    }

    public Timetable(int id,int owner_id,String name,int[] entry_id,int[] user_id,Date[] date,Time[] time_start,Time[] time_end) {
        this(id,owner_id,name);
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.date = date;
        this.time_start = time_start;
        this.time_end = time_end;
    }

    public String getTimeSpan(int index) {
        return time_start[index].getString() + " - " + time_end[index].getString();
    }
}
