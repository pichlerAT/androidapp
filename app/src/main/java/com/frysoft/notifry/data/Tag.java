package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Time;

public class Tag extends MySQLEntry {

    protected static final byte VSET_OFFSET = 127;

    protected static final byte VSET_TITLE          = 0x01;
    protected static final byte VSET_DESCRIPTION    = 0x02;
    protected static final byte VSET_DATE_START     = 0x04;
    protected static final byte VSET_TIME_START     = 0x08;
    protected static final byte VSET_DATE_END       = 0x10;
    protected static final byte VSET_TIME_END       = 0x20;
    protected static final byte VSET_COLOR          = 0x40;

    protected Category category;

    protected String title;

    protected String description;

    protected short date_start;

    protected short time_start;

    protected short date_end;

    protected short time_end;

    protected RRule rRule;

    protected int color;

    protected byte vset;

    protected Tag(Category category, String title, String description, Date date_start, Time time_start, Date date_end, Time time_end, RRule rRule, boolean set_color, int color) {
        super(0, 0);

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

        if(date_end == null) {
            this.date_end = 0;
        }else {
            vset |= VSET_DATE_END;
            this.date_end = date_end.getShort();
        }

        if(time_end == null) {
            this.time_end = 0;
        }else {
            vset |= VSET_TIME_END;
            this.time_end = time_end.time;
        }

        this.rRule = rRule;

        this.color = color;
        if(set_color) {
            vset |= VSET_TIME_START;
        }
    }

    protected Tag(FryFile fry) {
        super(fry);

        vset = (byte)(fry.getUnsignedByte() - VSET_OFFSET);
        category = Data.Categories.getById(fry.getUnsignedInt());

        if(issetTitle()) {
            title = fry.getString();
        }
        if(issetDescription()) {
            description = fry.getString();
        }
        if(issetDateStart()) {
            date_start = fry.getUnsignedShort();
        }
        if(issetTimeStart()) {
            time_start = fry.getUnsignedShort();
        }
        if(issetDateEnd()) {
            date_end = fry.getUnsignedShort();
        }
        if(issetTimeEnd()) {
            time_end = fry.getUnsignedShort();
        }

        rRule = new RRule(fry.getString());

        if(issetColor()) {
            color = fry.getInt();
        }
    }

    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);

        fry.writeUnsignedByte((byte)(vset + VSET_OFFSET));
        fry.writeUnsignedInt(getCategoryId());

        if(issetTitle()) {
            fry.writeString(title);
        }
        if(issetDescription()) {
            fry.writeString(description);
        }
        if(issetDateStart()) {
            fry.writeUnsignedShort(date_start);
        }
        if(issetTimeStart()) {
            fry.writeUnsignedShort(time_start);
        }

        if(issetDateEnd()) {
            fry.writeUnsignedShort(date_end);
        }

        if(issetTimeEnd()) {
            fry.writeUnsignedShort(time_end);
        }

        fry.writeString(rRule.getString());

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
        FryFile fry = executeMySQL(DIR_TAG + "create.php", "&category_id=" + signed(getCategoryId()) + "&title=" + title
                + "&description=" + description + "&time_start=" + signed(time_start) + "&date_start=" + signed(date_start)
                + "&time_start=" + signed(time_start) + "&date_end=" + signed(date_end) + "&time_end=" + signed(time_end)
                + "&rrule="+rRule.getString() + "&color=" + color + "&set=" + signed((byte)(vset + VSET_OFFSET)));
        if(fry != null) {
            id = fry.getUnsignedInt();
            //user_id = User.getId();
            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        return (executeMySQL(DIR_TAG + "update.php", "&id=" + signed(id) + "&category_id=" + signed(getCategoryId()) + "&title=" + title
                + "&description=" + description + "&time_start=" + signed(time_start) + "&date_start=" + signed(date_start)
                + "&time_start=" + signed(time_start) + "&date_end=" + signed(date_end) + "&time_end=" + signed(time_end)
                + "&rrule="+rRule.getString() + "&color=" + color + "&set=" + signed((byte)(vset + VSET_OFFSET))) != null);
    }

    @Override
    protected boolean mysql_delete() {
        return (executeMySQL(DIR_TAG + "delete.php", "&id=" + signed(id)) != null);
    }

    @Override
    protected byte getType() {
        return TYPE_TAG;
    }

    @Override
    protected String getPath() {
        return DIR_TAG;
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

    public boolean issetDateEnd() {
        return ((vset & VSET_DATE_END) > 0);
    }

    public boolean issetTimeEnd() {
        return ((vset & VSET_TIME_END) > 0);
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

    public Time getTimeStart() {
        return new Time(time_start);
    }

    public void set(Category category, String title, String description, Date date_start, Time time_start, Date date_end, Time time_end, int duration, RRule rRule) {
        set(category, title, description, date_start, time_start, date_end, time_end, rRule, false, 0);
    }

    public void set(Category category, String title, String description, Date date_start, Time time_start, Date date_end, Time time_end, int duration, RRule rRule, int color) {
        set(category, title, description, date_start, time_start, date_end, time_end, rRule, true, color);
    }

    protected void set(Category category, String title, String description, Date date_start, Time time_start, Date date_end, Time time_end, RRule rRule, boolean set_color, int color) {
        vset = 0;

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

        if(date_end == null) {
            this.date_end = 0;
        }else {
            vset |= VSET_DATE_END;
            this.date_end = date_end.getShort();
        }

        if(time_end == null) {
            this.time_end = 0;
        }else {
            vset |= VSET_TIME_END;
            this.time_end = time_end.time;
        }

        this.rRule = rRule;

        this.color = color;
        if(set_color) {
            vset |= VSET_TIME_START;
        }

        update();
    }

}
