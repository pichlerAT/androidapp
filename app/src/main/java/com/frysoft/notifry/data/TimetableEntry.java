package com.frysoft.notifry.data;

import com.frysoft.notifry.data.value.ValueCategory;
import com.frysoft.notifry.data.value.ValueDate;
import com.frysoft.notifry.data.value.ValueInteger;
import com.frysoft.notifry.data.value.ValueRRule;
import com.frysoft.notifry.data.value.ValueString;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Date;

public class TimetableEntry extends MySQLEntry implements Fryable {


    protected static short TimezoneOffset = Date.getTimezoneOffset();

    public static void setTimezoneOffset(int offsetInMillis) {
        TimezoneOffset = (short)(offsetInMillis / 60000);
    }


    protected int owner_id;

    protected ValueCategory category = new ValueCategory();

    protected ValueString title = new ValueString();

    protected ValueString description = new ValueString();

    protected ValueDate raw_start = new ValueDate();

    protected ValueDate raw_end = new ValueDate();

    protected ValueRRule rRule = new ValueRRule();

    protected ValueInteger color = new ValueInteger();

    protected String google_id;


    protected int start;

    protected int end;

    protected int days;

    public ShareList shares = new ShareList(this);

    protected void setValues() {
        Date d_start = raw_start.getDate();
        Date d_end = raw_end.getDate();

        if(!rRule.getValue().wholeDay) {
            d_start.addMinutes(TimezoneOffset);
            d_end.addMinutes(TimezoneOffset);
        }

        start = d_start.getInt();
        end = d_end.getInt();

        days = d_start.getDaysUntil(d_end);
    }

    protected void setRawValues() {
        Date d_start = new Date(start);
        Date d_end = new Date(start);

        if(!rRule.getValue().wholeDay) {
            d_start.subtractMinutes(TimezoneOffset);
            d_end.subtractMinutes(TimezoneOffset);
        }

        raw_start.setValue(d_start.getInt());
        raw_start.setValue(d_end.getInt());

        days = d_start.getDaysUntil(d_end);
    }

    protected TimetableEntry(FryFile fry) {
        super(fry);
        Logger.Log("TimetableEntry", "TimetableEntry(FryFile)");

        owner_id = fry.readId();

        category.readFrom(fry);
        title.readFrom(fry);
        description.readFrom(fry);
        raw_start.readFrom(fry);
        raw_end.readFrom(fry);
        rRule.readFrom(fry);
        color.readFrom(fry);
        google_id = fry.readString();
        shares.readFrom(fry);

        if(category.isChanged() || title.isChanged() || description.isChanged() || raw_start.isChanged() || raw_end.isChanged() || rRule.isChanged() || color.isChanged()) {
            update();
        }

        setValues();
    }

    protected TimetableEntry(int id, int user_id, Category category, String title, String description,
                             int start, int end, RRule rRule, int color, String google_id) {
        super(id, user_id, Date.getMillis());

        this.category.setValue(category);
        this.title.setValue(title);
        this.description.setValue(description);

        this.start = start;
        this.end = end;
        this.rRule.setValue(rRule);
        this.color.setValue(color);
        this.google_id = google_id;

        setRawValues();
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("TimetableEntry", "equals(Object)");
        if(o instanceof TimetableEntry) {
            TimetableEntry e = (TimetableEntry) o;
            return (e.id == id && e.category.getId() == category.getId() && e.title.equals(title)
                    && e.description.equals(description)&& e.raw_start.equals(raw_start)
                    && e.raw_end.equals(raw_end) && e.color.equals(color) && e.rRule.equals(rRule) );
        }
        return false;
    }

    @Override
    protected void addData(MySQL mysql) {
        mysql.addId("category_id", category.getId());
        mysql.add("title", title);
        mysql.add("description", description);
        mysql.add("start", raw_start);
        mysql.add("end", raw_end);
        mysql.addString("rrule", rRule.getString());
        mysql.add("color", color);
        mysql.addString("google_id", google_id);
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
    protected void sync(MySQLEntry entry) {
        TimetableEntry te = (TimetableEntry) entry;
        boolean update = false;

        if(category.doUpdate(te.category)) {
            update = true;
        }
        if(title.doUpdate(te.title)) {
            update = true;
        }
        if(description.doUpdate(te.description)) {
            update = true;
        }
        if(raw_start.doUpdate(te.raw_start)) {
            update = true;
        }
        if(raw_end.doUpdate(te.raw_end)) {
            update = true;
        }
        if(rRule.doUpdate(te.rRule)) {
            update = true;
        }
        if(color.doUpdate(te.color)) {
            update = true;
        }
        google_id = te.google_id;
        setValues();

        if(update) {
            update();
        }
    }

    @Override
    protected char getType() {
        return TYPE_TIMETABLE_ENTRY;
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("TimetableEntry", "writeTo(FryFile)");
        super.writeTo(fry);

        fry.writeId(owner_id);

        category.writeTo(fry);
        title.writeTo(fry);
        description.writeTo(fry);
        raw_start.writeTo(fry);
        raw_end.writeTo(fry);
        rRule.writeTo(fry);
        color.writeTo(fry);
        fry.writeString(google_id);
        shares.writeTo(fry);
    }

    @Override
    public void remove() {
        Data.Timetable.Entries.remove(this);
    }

    protected int getCategoryId() {
        return category.getId();
    }

    public void setTitle(String title) {
        this.title.setValue(title);
        update();
    }

    public void set(Category category, String title, String description, Date start, Date end, RRule rRule, int color) {

        this.category.setValue(category);
        this.title.setValue(title);
        this.description.setValue(description);

        this.start = start.getInt();
        this.end = end.getInt();

        this.rRule.setValue(rRule);
        this.color.setValue(color);

        setRawValues();
        update();
    }

    public int getId() {
        return id;
    }

    public Category getCategory() {
        return category.getValue();
    }

    public String getTitle() {
        return title.getValue();
    }

    public String getDescription() {
        return description.getValue();
    }

    public Date getStart() {
        return new Date(start);
    }

    public Date getEnd() {
        return new Date(end);
    }

    public RRule getRRule() {
        return rRule.getValue();
    }

    public int getColor() {
        return color.getValue();
    }

}
