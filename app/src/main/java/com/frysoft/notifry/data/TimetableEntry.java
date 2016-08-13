package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Time;

import java.util.ArrayList;
import java.util.Calendar;

public class TimetableEntry extends MySQLEntry implements Fryable {

    public static final short REPEAT_MONDAY     = 0x0001;
    public static final short REPEAT_TUESDAY    = 0x0002;
    public static final short REPEAT_WEDNESDAY  = 0x0004;
    public static final short REPEAT_THURSDAY   = 0x0008;
    public static final short REPEAT_FRIDAY     = 0x0010;
    public static final short REPEAT_SATURDAY   = 0x0020;
    public static final short REPEAT_SUNDAY     = 0x0040;
    public static final short REPEAT_MONTHLY    = 0x0080;
    public static final short REPEAT_ANNUALY    = 0x0100;
    public static final short NOTIFY_SELF       = 0x0200;
    public static final short NOTIFY_ALL        = 0x0400;
    public static final short REPEAT_UNTIL      = 0x0800;
    public static final short REPEAT_DAILY      = 0x1000;
    public static final short WHOLE_DAY         = 0x2000;
    public static final short EMPTY_2           = 0x4000;
    public static final short EMPTY_1    = (short)0x8000;

    public static final short REPEAT_WEEKLY     = REPEAT_MONDAY
                                                | REPEAT_TUESDAY
                                                | REPEAT_WEDNESDAY
                                                | REPEAT_THURSDAY
                                                | REPEAT_FRIDAY
                                                | REPEAT_SATURDAY
                                                | REPEAT_SUNDAY ;

    private static short TimezoneOffset = Time.getTimezoneOffset();

    public static void setTimezoneOffset(int offsetInMillis) {
        TimezoneOffset = (short)(offsetInMillis / 60000);
    }


    protected Category category;

    protected String title;

    protected String description;

    protected short date_start;

    protected short time_start;

    protected int duration;

    protected short addition;

    protected int color;

    protected int google_id;

    protected short end;

    protected short intervall;



    protected boolean repeat_daily;

    protected boolean repeat_weekly;

    protected boolean repeat_monthly;

    protected boolean repeat_annualy;

    protected short time_end;

    protected short date_end;

    protected int days;

    public ShareList shares = new ShareList(TYPE_CALENDAR_ENTRY, id);

    protected void updateMisc() {
        repeat_daily = ((addition & REPEAT_DAILY) > 0);
        repeat_weekly = ((addition & REPEAT_WEEKLY) > 0);
        repeat_monthly = ((addition & REPEAT_MONTHLY) > 0);
        repeat_annualy = ((addition & REPEAT_ANNUALY) > 0);

        Time t = new Time(time_start);
        days = t.addMinutes(duration);

        time_end = t.time;

        if((addition & REPEAT_UNTIL) > 0) {
            date_end = end;

        }else {
            Date d = new Date(date_start);
            d.addDays(days);

            if(repeat_annualy) {
                d.addYears(intervall * end);

            }else if(repeat_monthly) {
                d.addMonths(intervall * end);

            }else if(repeat_weekly) {


            }else if(repeat_daily) {
                d.addDays(intervall * end);
            }

            date_end = d.getShort();
        }
    }

    protected TimetableEntry(FryFile fry) {
        super(fry);
        Logger.Log("TimetableEntry", "TimetableEntry(FryFile)");

        category = Data.Categories.getById(fry.getInt());
        title = fry.getString();
        description = fry.getString();
        short date_start = fry.getShort();
        short time_start = fry.getShort();
        duration = fry.getInt();
        addition = fry.getShort();
        short end = fry.getShort();
        intervall = signed(fry.getByte());
        color = fry.getInt();
        google_id = fry.getInt();
        shares.readFrom(fry);

        setTimezoneAffectedData(date_start, time_start, end);
        updateMisc();
    }

    protected TimetableEntry(int id, int user_id, Category category, String title, String description, short date_start,
                             short time_start, int duration, short addition, short end, short intervall, int color, int google_id) {
        super(TYPE_CALENDAR_ENTRY, id, user_id);

        this.category = category;
        this.title = title;

        if(description == null || description.length() == 0) {
            this.description = " ";
        }else {
            this.description = description;
        }

        this.date_start = date_start;
        this.time_start = time_start;
        this.duration = duration;
        this.addition = addition;
        this.end = end;
        this.intervall = intervall;
        this.color = color;
        this.google_id = google_id;

        updateMisc();
    }

    protected TimetableEntry(int id, int user_id, int category_id, String title, String description, short date_start,
                             short time_start, int duration, short addition, short end, short intervall, int color, int google_id) {
        this(id, user_id, Data.Categories.getById(category_id), title, description, date_start,
                time_start, duration, addition, end, intervall, color, google_id);
    }

    protected void setTimezoneAffectedData(short date_start, short time_start, short end) {
        Date dateStart = new Date(date_start);
        Time timeStart = new Time(time_start);
        int dDay = timeStart.subtractMinutes(TimezoneOffset);
        dateStart.addDays(dDay);

        this.date_start = dateStart.getShort();
        this.time_start = timeStart.time;

        if(getAddition(REPEAT_UNTIL)) {
            Date dateEnd = new Date(end);
            dateEnd.addDays(dDay);
            this.end = dateEnd.getShort();

        }else {
            this.end = end;
        }
    }

    protected short[] getTimezoneAffectedData() {
        short[] data = new short[3];

        Date dateStart = new Date(date_start);
        Time timeStart = new Time(time_start);
        int dDay = timeStart.addMinutes(TimezoneOffset);
        dateStart.addDays(dDay);

        data[0] = dateStart.getShort();
        data[1] = timeStart.time;

        if(getAddition(REPEAT_UNTIL)) {
            Date dateEnd = new Date(end);
            dateEnd.addDays(dDay);
            data[2] = dateEnd.getShort();

        }else {
            data[2] = this.end;
        }

        return data;
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("TimetableEntry", "equals(Object)");
        if(o instanceof TimetableEntry) {
            TimetableEntry e = (TimetableEntry) o;
            return (e.id == id && e.getCategoryId() == getCategoryId() && e.title.equals(title) && e.description.equals(description)
                    && e.date_start == date_start && e.time_start == time_start && e.duration == duration
                    && e.addition == addition && e.color == color && e.end == end && e.intervall == intervall);
        }
        return false;
    }

    @Override
    public TimetableEntry backup() {
        Logger.Log("TimetableEntry", "backup()");
        return new TimetableEntry(id, user_id, category, title, description, date_start, time_start, duration, addition, end, intervall, color, google_id);
    }

    @Override
    protected boolean mysql_create() {
        Logger.Log("TimetableEntry", "mysql_create()");
        short[] TZdata = getTimezoneAffectedData();

        String resp = executeMySQL(DIR_CALENDAR_ENTRY + "create.php", "&category_id=" + signed(getCategoryId()) + "&title=" + title + "&description=" + description
                + "&date_start=" + signed(TZdata[0]) + "&time_start=" + signed(TZdata[1]) + "&duration=" + signed(duration)
                + "&addition="+signed(addition) + "&color=" + color + "&end=" + signed(TZdata[2]) + "&intervall=" + intervall);
        if(resp != null) {
            id = Integer.parseInt(resp);

            if(isOnline()) {
                shares = new ShareList(TYPE_CALENDAR_ENTRY, id);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        Logger.Log("TimetableEntry", "mysql_update()");
        short[] TZdata = getTimezoneAffectedData();

        return (executeMySQL(DIR_CALENDAR_ENTRY + "update.php", "&id=" + signed(id) + "&category_id=" + signed(getCategoryId()) + "&title=" + title
                + "&description=" + description + "&date_start=" + signed(TZdata[0]) + "&time_start=" + signed(TZdata[1]) + "&duration=" + signed(duration)
                + "&addition="+signed(addition) + "&color=" + color + "&google_id=" + signed(google_id) + "&end=" + signed(TZdata[2])
                + "&intervall=" + intervall) != null);
    }

    @Override
    protected boolean mysql_delete() {
        Logger.Log("TimetableEntry", "mysql_delete()");
        return (executeMySQL(DIR_CALENDAR_ENTRY+  "delete.php", "&id=" + signed(id)) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) {
        Logger.Log("TimetableEntry", "synchronize(MySQL)");
        TimetableEntry e = (TimetableEntry) mysql;
        category = e.category;
        title = e.title;
        description = e.description;
        date_start = e.date_start;
        time_start = e.time_start;
        duration = e.duration;
        addition = e.addition;
        color = e.color;
        end = e.end;
        intervall = e.intervall;
        updateMisc();
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

        short[] TZdata = getTimezoneAffectedData();

        fry.writeUnsignedInt(getCategoryId());
        fry.writeString(title);
        fry.writeString(description);
        fry.writeUnsignedShort(TZdata[0]);
        fry.writeUnsignedShort(TZdata[1]);
        fry.writeUnsignedInt(duration);
        fry.writeUnsignedShort(addition);
        fry.writeUnsignedShort(TZdata[2]);
        fry.writeUnsignedByte((byte)intervall);
        fry.writeInt(color);
        fry.writeUnsignedInt(google_id);
        shares.writeTo(fry);
    }

    @Override
    public void delete() {
        Logger.Log("TimetableEntry", "delete()");
        super.delete();
        Data.Timetable.Entries.remove(this);
    }

    protected int getCategoryId() {
        if(category == null) {
            return 0;
        }
        return category.id;
    }

    public void setTitle(String title) {
        this.title = title;
        update();
    }

    public void set(Category category, String title, String description, Date date_start, Date date_end,
                    Time time_start, Time time_end, int color, Date date_repeat_until, short intervall, short... additions) {

        short addition = REPEAT_UNTIL;
        for(short a : additions) {
            addition |= a;
        }

        set(category, title, description, date_start, date_end, time_start, time_end, color, date_repeat_until.getShort(), intervall, addition);
    }

    public void set(Category category, String title, String description, Date date_start, Date date_end,
                    Time time_start, Time time_end, int color, short repetitions, short intervall, short... additions) {

        this.category = category;
        this.title = title;
        this.description = description;
        this.date_start = date_start.getShort();
        this.time_start = time_start.time;
        duration = time_end.time - time_start.time + Time.MAX_TIME * date_start.getDaysUntil(date_end);

        addition = 0;
        for(short a : additions) {
            addition |= a;
        }

        this.color = color;
        end = repetitions;
        this.intervall = intervall;

        updateMisc();
        update();
    }

    public boolean getAddition(short parameter) {
        return ((addition & parameter) > 0);
    }

    public int getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDateStart() {
        return new Date(date_start);
    }

    public Time getTimeStart() {
        return new Time(time_start);
    }

    public Date getDateEnd() {
        return new Date(date_end);
    }

    public Time getTimeEnd() {
        return new Time(time_end);
    }

    /**
     * Only use this method if REPEAT_UNTIL is NOT set!
     *
     * @return number of repetitions
     */
    public short getRepetitions() {
        return end;
    }

    public int getColor() {
        return color;
    }

    public boolean[] getAdditions() {
        boolean[] b = new boolean[16];
        b[0] =  (addition & REPEAT_MONDAY)      > 0;
        b[1] =  (addition & REPEAT_TUESDAY)     > 0;
        b[2] =  (addition & REPEAT_WEDNESDAY)   > 0;
        b[3] =  (addition & REPEAT_THURSDAY)    > 0;
        b[4] =  (addition & REPEAT_FRIDAY)      > 0;
        b[5] =  (addition & REPEAT_SATURDAY)    > 0;
        b[6] =  (addition & REPEAT_SUNDAY)      > 0;
        b[7] =  (addition & REPEAT_MONTHLY)     > 0;
        b[8] =  (addition & REPEAT_ANNUALY)     > 0;
        b[9] =  (addition & NOTIFY_SELF)        > 0;
        b[10] = (addition & NOTIFY_ALL)         > 0;
        b[11] = (addition & REPEAT_UNTIL)       > 0;
        b[12] = (addition & REPEAT_DAILY)       > 0;
        b[13] = (addition & WHOLE_DAY)          > 0;
        b[14] = false;
        b[15] = false;
        return b;
    }

    protected void addEventsToList(ArrayList<Event> list, final short start, final short end) {
        if(date_end < start || date_start > end) {
            return;
        }

        short ending = (end < date_end ? end : date_end);

        if(repeat_daily) {
            Date e = new Date(date_start);
            e.addDays(days);

            while(e.getShort() < start) {
                e.addDays(intervall);
            }

            Date s = e.copy();
            s.subtractDays(days);

            while(s.getShort() <= ending) {
                addEventToList(list, start, ending, s.getShort());

                e.addDays(intervall);
                s.addDays(intervall);
            }


        }else if(repeat_weekly) {
            /*
            Date e = new Date(this.date_start);
            e.addDays(days);

            while(e.getShort() < start) {
                e.addDays(repetitions * 7);
            }

            Date s = e.copy();
            s.subtractDays(days);

            while(s.getShort() <= end) {
                addEventToList(list, start, end, s.getShort());

                e.addDays(repetitions * 7);
                s.addDays(repetitions * 7);
            }
            */

        }else if(repeat_monthly) {
            Date e = new Date(date_start);
            e.addDays(days);

            while(e.getShort() < start) {
                e.addMonths(intervall);
            }

            Date s = e.copy();
            s.subtractDays(days);

            while(s.getShort() <= ending) {
                addEventToList(list, start, ending, s.getShort());

                e.addMonths(intervall);
                s.addMonths(intervall);
            }

        }else if(repeat_annualy) {
            Date e = new Date(date_start);
            e.addDays(days);

            while(e.getShort() < start) {
                e.addYears(intervall);
            }

            Date s = e.copy();
            s.subtractDays(days);

            while(s.getShort() <= ending) {
                addEventToList(list, start, ending, s.getShort());

                e.addYears(intervall);
                s.addYears(intervall);
            }


        }else {


            addEventToList(list, start, ending, date_start);
        }

    }

    protected void addEventToList(ArrayList<Event> list, final short start, final short end, short date) {
        Date d = new Date(date);
        Event e;

        if(days == 0) {

            if(getAddition(WHOLE_DAY)) {
                e = new Event.WholeDay(this, d.day, d.month, d.year);

            }else if (time_start == Time.MIN_TIME) {
                if(time_end == Time.MAX_TIME) {
                    e = new Event.WholeDay(this, d.day, d.month, d.year);

                }else {
                    e = new Event.Start(this, d.day, d.month, d.year);
                }

            }else if(time_end == Time.MAX_TIME) {
                e = new Event.End(this, d.day, d.month, d.year);

            }else {
                e = new Event.StartEnd(this, d.day, d.month, d.year);
            }

            list.add(e);
            return;
        }

        if(date >= start) {
            if(getAddition(WHOLE_DAY)) {
                e = new Event.WholeDay(this, d.day, d.month, d.year);

            }else if (time_start == Time.MIN_TIME) {
                e = new Event.WholeDay(this, d.day, d.month, d.year);
            } else {
                e = new Event.Start(this, d.day, d.month, d.year);
            }
            list.add(e);
        }

        d.addDays(1);

        for(int i=1; i<days; ++i, d.addDays(1)) {
            if(d.getShort() > end) {
                return;
            }
            e = new Event.WholeDay(this, d.day, d.month, d.year);
            list.add(e);
        }
        if(d.getShort() > end) {
            return;
        }

        if(getAddition(WHOLE_DAY)) {
            e = new Event.WholeDay(this, d.day, d.month, d.year);

        }else if(time_end == Time.MAX_TIME) {
            e = new Event.WholeDay(this, d.day, d.month, d.year);
        }else {
            e = new Event.End(this, d.day, d.month, d.year);
        }
        list.add(e);
    }

}
