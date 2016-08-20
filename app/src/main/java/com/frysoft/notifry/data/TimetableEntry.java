package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Time;

import java.util.ArrayList;
import java.util.Calendar;

public class TimetableEntry extends MySQLEntry implements Fryable {


    protected static short TimezoneOffset = Time.getTimezoneOffset();

    public static void setTimezoneOffset(int offsetInMillis) {
        TimezoneOffset = (short)(offsetInMillis / 60000);
    }


    protected Category category;

    protected String title;

    protected String description;

    protected short raw_date_start;

    protected short raw_time_start;

    protected short raw_date_end;

    protected short raw_time_end;

    protected RRule rRule;

    protected int color;

    protected String google_id;


    protected short date_start;

    protected short time_start;

    protected short date_end;

    protected short time_end;

    protected int days;

    public ShareList shares = new ShareList(this);

    protected void setValues() {
        Date dateStart = new Date(raw_date_start);
        Time timeStart = new Time(raw_time_start);
        Date dateEnd = new Date(raw_date_end);
        Time timeEnd = new Time(raw_time_end);

        if(!rRule.wholeDay) {
            dateStart.addDays(timeStart.addMinutes(TimezoneOffset));
            dateEnd.addDays(timeEnd.addMinutes(TimezoneOffset));
        }

        date_start = dateStart.getShort();
        time_start = timeStart.time;
        date_end = dateEnd.getShort();
        time_end = timeEnd.time;

        days = dateStart.getDaysUntil(dateEnd);
    }

    protected void setRawValues() {
        Date dateStart = new Date(date_start);
        Time timeStart = new Time(time_start);
        Date dateEnd = new Date(date_end);
        Time timeEnd = new Time(time_end);

        if(!rRule.wholeDay) {
            dateStart.addDays(timeStart.subtractMinutes(TimezoneOffset));
            dateEnd.addDays(timeEnd.subtractMinutes(TimezoneOffset));
        }

        raw_date_start = dateStart.getShort();
        raw_time_start = timeStart.time;
        raw_date_end = dateEnd.getShort();
        raw_time_end = timeEnd.time;

        days = dateStart.getDaysUntil(dateEnd);
    }

    protected TimetableEntry(FryFile fry) {
        super(fry);
        Logger.Log("TimetableEntry", "TimetableEntry(FryFile)");

        category = Data.Categories.getById(fry.getUnsignedInt());
        title = fry.getString();
        description = fry.getString();
        raw_date_start = fry.getUnsignedShort();
        raw_time_start = fry.getUnsignedShort();
        raw_date_end = fry.getUnsignedShort();
        raw_time_end = fry.getUnsignedShort();
        rRule = new RRule(fry.getString());
        color = fry.getInt();
        google_id = fry.getString();
        shares.readFrom(fry);

        setValues();
    }

    protected TimetableEntry(int id, int user_id, Category category, String title, String description, short date_start,
                             short time_start, short date_end, short time_end, RRule rRule, int color, String google_id) {
        super(id, user_id);

        this.category = category;
        this.title = title;
        this.description = description;
        this.date_start = date_start;
        this.time_start = time_start;
        this.date_end = date_end;
        this.time_end = time_end;
        this.rRule = rRule;
        this.color = color;
        this.google_id = google_id;

        setRawValues();
    }

    protected TimetableEntry(int id, int user_id, int category_id, String title, String description, short date_start,
                             short time_start, short date_end, short time_end, String rRule, int color, String google_id) {
        this(id, user_id, Data.Categories.getById(category_id), title, description, date_start,
                time_start, date_end, time_end, new RRule(rRule), color, google_id);
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("TimetableEntry", "equals(Object)");
        if(o instanceof TimetableEntry) {
            TimetableEntry e = (TimetableEntry) o;
            return (e.id == id && e.getCategoryId() == getCategoryId() && e.title.equals(title) && e.description.equals(description)
                    && e.raw_date_start == raw_date_start && e.raw_time_start == raw_time_start && e.date_end == date_end
                    && e.time_end == time_end && e.color == color && e.rRule.equals(rRule) );
        }
        return false;
    }

    @Override
    public TimetableEntry backup() {
        Logger.Log("TimetableEntry", "backup()");
        return new TimetableEntry(id, user_id, getCategoryId(), title, description, date_start, time_start, date_end, time_end, rRule.getString(), color, google_id);
    }

    @Override
    protected boolean mysql_create() {
        Logger.Log("TimetableEntry", "mysql_create()");

        FryFile fry = executeMySQL(DIR_CALENDAR_ENTRY + "create.php", "&category_id=" + signed(getCategoryId()) + "&title=" + title + "&description=" + description
                + "&date_start=" + signed(raw_date_start) + "&time_start=" + signed(raw_time_start) + "&date_end=" + signed(raw_date_end)
                + "&time_end=" + signed(raw_time_end) + "&rrule="+rRule.getString() + "&color=" + color + "&google_id=" + google_id);
        if(fry != null) {
            id = fry.getUnsignedInt();
            shares = new ShareList(this);
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        Logger.Log("TimetableEntry", "mysql_update()");

        return (executeMySQL(DIR_CALENDAR_ENTRY + "update.php", "&id=" + signed(id) + "&category_id=" + signed(getCategoryId()) + "&title=" + title
                + "&description=" + description + "&date_start=" + signed(raw_date_start) + "&time_start=" + signed(raw_time_start)
                + "&date_end=" + signed(raw_date_end) + "&time_end=" + signed(raw_time_end) + "&rrule="+rRule.getString() + "&color=" + color + "&google_id=" + google_id) != null);
    }

    @Override
    protected byte getType() {
        return TYPE_CALENDAR_ENTRY;
    }

    @Override
    protected String getPath() {
        return DIR_CALENDAR_ENTRY;
    }

    @Override
    protected void synchronize(MySQL mysql) {
        Logger.Log("TimetableEntry", "synchronize(MySQL)");
        TimetableEntry e = (TimetableEntry) mysql;
        category = e.category;
        title = e.title;
        description = e.description;
        raw_date_start = e.raw_date_start;
        raw_time_start = e.raw_time_start;
        raw_date_end = e.raw_date_end;
        raw_time_end = e.raw_time_end;
        rRule = e.rRule;
        color = e.color;

        setValues();
    }

    @Override
    public boolean canEdit() {
        Logger.Log("TimetableEntry", "canEdit()");
        return (isOwner() || (shares != null && shares.size() > 0 && shares.getPermission(0) >= Share.PERMISSION_EDIT));
    }

    @Override
    public int getShareId() {
        if(shares != null && shares.size() > 0 && !isOwner()) {
            return shares.getId(0);
        }
        return 0;
    }


    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("TimetableEntry", "writeTo(FryFile)");
        super.writeTo(fry);

        fry.writeUnsignedInt(getCategoryId());
        fry.writeString(title);
        fry.writeString(description);
        fry.writeUnsignedShort(raw_date_start);
        fry.writeUnsignedShort(raw_time_start);
        fry.writeUnsignedShort(raw_date_end);
        fry.writeUnsignedShort(raw_time_end);
        fry.writeString(rRule.getString());
        fry.writeInt(color);
        fry.writeString(google_id);
        shares.writeTo(fry);
    }

    @Override
    public void remove() {
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
                    Time time_start, Time time_end, RRule rRule, int color) {

        this.category = category;
        this.title = title;
        this.description = description;
        this.date_start = date_start.getShort();
        this.time_start = time_start.time;
        this.date_end = date_end.getShort();
        this.time_end = time_end.time;
        this.rRule = rRule;
        this.color = color;

        setRawValues();
        update();
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

    public RRule getRRule() {
        return rRule;
    }

    public int getColor() {
        return color;
    }

}
