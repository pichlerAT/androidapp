package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Time;

public class Tag extends MySQLEntry {

    protected static final byte VSET_TITLE          = 0x01;
    protected static final byte VSET_DESCRIPTION    = 0x02;
    protected static final byte VSET_DATE_START     = 0x04;
    protected static final byte VSET_TIME_START     = 0x08;
    protected static final byte VSET_COLOR          = 0x10;
    //protected static final byte VSET_COLOR          = 0x20;
    //protected static final byte VSET_COLOR          = 0x40;
    //protected static final byte VSET_COLOR          = 0x80;


    protected Category category;

    protected String title;

    protected String description;

    protected short date_start;

    protected short time_start;

    protected int duration;

    protected short addition;

    protected short end;

    protected short intervall; // (byte)

    protected int color;

    protected byte vset;

    protected Tag(Category category, String title, String description, Date date_start, Time time_start, int duration, short addition, short end, short intervall, boolean set_color, int color) {
        super(TYPE_TAG, 0, 0);

        this.category = category;

        if(title == null) {
            this.title = " ";
        }else {
            vset |= VSET_TITLE;
            this.title = title;
        }

        if(description == null) {
            this.description = " ";
        }else {
            vset |= VSET_DESCRIPTION;
            this.description = description;
        }

        if(date_start == null) {
            this.date_start = 0;
        }else {
            vset |= VSET_DATE_START;
            this.date_start = date_start.getShort();
        }

        if(time_start == null) {
            this.time_start = 0;
        }else {
            vset |= VSET_TIME_START;
            this.time_start = time_start.time;
        }

        this.duration = duration;

        this.addition = addition;

        this.end = end;

        this.intervall = intervall;

        this.color = color;
        if(set_color) {
            vset |= VSET_TIME_START;
        }
    }

    protected Tag(FryFile fry) {
        super(fry);

        vset = fry.getByte();
        category = Data.Categories.getById(fry.getInt());

        if(issetTitle()) {
            title = fry.getString();
        }
        if(issetDescription()) {
            description = fry.getString();
        }
        if(issetDateStart()) {
            date_start = fry.getShort();
        }
        if(issetTimeStart()) {
            time_start = fry.getShort();
        }

        duration = fry.getInt();
        addition = fry.getShort();
        end = fry.getShort();
        intervall = fry.getShort();

        if(issetColor()) {
            color = fry.getInt();
        }
    }

    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);

        fry.writeByte(vset);
        fry.writeInt(getCategoryId());

        if(issetTitle()) {
            fry.writeString(title);
        }
        if(issetDescription()) {
            fry.writeString(description);
        }
        if(issetDateStart()) {
            fry.writeShort(date_start);
        }
        if(issetTimeStart()) {
            fry.writeShort(time_start);
        }

        fry.writeInt(duration);
        fry.writeShort(addition);
        fry.writeShort(end);
        fry.writeShort(intervall);

        if(issetColor()) {
            fry.writeInt(color);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Tag) {

        }
        return false;
    }

    @Override
    public Object backup() {
        return null;
    }

    @Override
    protected void synchronize(MySQL mysql) {

    }

    @Override
    public boolean canEdit() {
        return true;
    }

    @Override
    public int getShareId() {
        return 0;
    }

    @Override
    protected boolean mysql_create() {
        String resp = executeMySQL(DIR_TAG + "create.php", "&category_id=" + signed(getCategoryId()) + "&title=" + title + "&description=" + description
                + "&date_start=" + signed(date_start) + "&time_start=" + signed(time_start) + "&duration=" + signed(duration)
                + "&addition="+signed(addition) + "&end=" + signed(end) + "&intervall=" + intervall + "&color=" + color + "&set=" + signed(vset));
        if(resp != null) {
            id = Integer.parseInt(resp);
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        return (executeMySQL(DIR_TAG + "update.php", "&id=" + signed(id) + "&category_id=" + signed(getCategoryId()) + "&title=" + title
                + "&description=" + description + "&date_start=" + signed(date_start) + "&time_start=" + signed(time_start) + "&duration=" + signed(duration)
                + "&addition="+signed(addition) + "&end=" + signed(end) + "&intervall=" + intervall + "&color=" + color + "&set=" + signed(vset)) != null);
    }

    @Override
    protected boolean mysql_delete() {
        return (executeMySQL(DIR_TAG + "delete.php", "&id=" + signed(id)) != null);
    }

    public boolean issetTitle() {
        return ((vset & VSET_TITLE) > 0);
    }

    public boolean issetDescription() {
        return ((vset & VSET_DESCRIPTION) > 0);
    }

    public boolean issetDateStart() {
        return ((vset & VSET_DATE_START) > 0);
    }

    public boolean issetTimeStart() {
        return ((vset & VSET_TIME_START) > 0);
    }

    public boolean issetColor() {
        return ((vset & VSET_COLOR) > 0);
    }

    public int getCategoryId() {
        if(category == null) {
            return 0;
        }
        return category.id;
    }

    public Date getDateStart() {
        return new Date(date_start);
    }

    public Time getTimeStart() {
        return new Time(time_start);
    }

    public void set(Category category, String title, String description, Date date_start, Time time_start, Date date_end, int duration, short intervall, short... additions) {
        short addition = 0;
        for(short a : additions) {
            addition |= a;
        }

        set(category, title, description, date_start, time_start, duration, addition, date_end.getShort(), intervall, false, 0);
    }

    public void set(Category category, String title, String description, Date date_start, Time time_start, Date date_end, int duration, short intervall, int color, short... additions) {
        short addition = TimetableEntry.REPEAT_UNTIL;
        for(short a : additions) {
            addition |= a;
        }

        set(category, title, description, date_start, time_start, duration, addition, date_end.getShort(), intervall, true, color);
    }

    public void set(Category category, String title, String description, Date date_start, Time time_start, short repetitions, int duration, short intervall, short... additions) {short addition = 0;
        for(short a : additions) {
            addition |= a;
        }

        set(category, title, description, date_start, time_start, duration, addition, repetitions, intervall, false, 0);
    }

    public void set(Category category, String title, String description, Date date_start, Time time_start, short repetitions, int duration, short intervall, int color, short... additions) {
        short addition = 0;
        for(short a : additions) {
            addition |= a;
        }

        set(category, title, description, date_start, time_start, duration, addition, repetitions, intervall, true, color);
    }

    protected void set(Category category, String title, String description, Date date_start, Time time_start, int duration, short addition, short end, short intervall, boolean set_color, int color) {
        this.category = category;

        if(title == null) {
            this.title = " ";
        }else {
            vset |= VSET_TITLE;
            this.title = title;
        }

        if(description == null) {
            this.description = " ";
        }else {
            vset |= VSET_DESCRIPTION;
            this.description = description;
        }

        if(date_start == null) {
            this.date_start = 0;
        }else {
            vset |= VSET_DATE_START;
            this.date_start = date_start.getShort();
        }

        if(time_start == null) {
            this.time_start = 0;
        }else {
            vset |= VSET_TIME_START;
            this.time_start = time_start.time;
        }

        this.duration = duration;

        this.addition = addition;

        this.end = end;

        this.intervall = intervall;

        this.color = color;
        if(set_color) {
            vset |= VSET_TIME_START;
        }

        update();
    }

}
