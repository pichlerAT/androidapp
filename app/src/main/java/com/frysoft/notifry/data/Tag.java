package com.frysoft.notifry.data;

import com.frysoft.notifry.data.value.ValueCategory;
import com.frysoft.notifry.data.value.ValueDate;
import com.frysoft.notifry.data.value.ValueInteger;
import com.frysoft.notifry.data.value.ValueRRule;
import com.frysoft.notifry.data.value.ValueString;
import com.frysoft.notifry.data.value.ValueUnsignedByte;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Date;

public class Tag extends MySQLEntry {

    protected static final byte VSET_TITLE        = 0x01;
    protected static final byte VSET_DESCRIPTION  = 0x02;
    protected static final byte VSET_START        = 0x04;
    protected static final byte VSET_END          = 0x08;
    protected static final byte VSET_COLOR        = 0x10;

    protected ValueCategory category = new ValueCategory();

    protected ValueString title = new ValueString();

    protected ValueString description = new ValueString();

    protected ValueDate start = new ValueDate();

    protected ValueDate end = new ValueDate();

    protected ValueRRule rRule = new ValueRRule();

    protected ValueInteger color = new ValueInteger();

    protected ValueUnsignedByte vset = new ValueUnsignedByte();

    protected Tag(Category category, String title, String description, Date start, Date end, RRule rRule, boolean set_color, int color) {
        super(0, 0, Date.getMillis());

        vset.setValue((byte)0xFF);
        this.category.setValue(category);

        if(title == null) {
            this.title.setValue(" ");
        }else {
            vset.unsetBit(VSET_TITLE);
            this.title.setValue(title);
        }

        if(description == null) {
            this.description.setValue(" ");
        }else {
            vset.unsetBit(VSET_DESCRIPTION);
            this.description.setValue(description);
        }

        if(start == null) {
            this.start.setValue(0);
        }else {
            vset.unsetBit(VSET_START);
            this.start.setValue(start.getInt());
        }

        if(end == null) {
            this.end.setValue(0);
        }else {
            vset.unsetBit(VSET_END);
            this.end.setValue(end.getInt());
        }

        this.rRule.setValue(rRule);

        this.color.setValue(color);
        if(set_color) {
            vset.unsetBit(VSET_COLOR);
        }
    }

    protected Tag(FryFile fry) {
        super(fry);

        if(fry instanceof FryFile.Compact) {
            vset.readFrom(fry);
            category.readFrom(fry);

            if (issetTitle()) {
                title.readFrom(fry);
            }
            if (issetDescription()) {
                description.readFrom(fry);
            }
            if (issetStart()) {
                start.readFrom(fry);
            }
            if (issetEnd()) {
                end.readFrom(fry);
            }

            rRule.readFrom(fry);

            if (issetColor()) {
                color.readFrom(fry);
            }

            if(category.isChanged() || title.isChanged() || description.isChanged() || start.isChanged() || end.isChanged() || rRule.isChanged() || color.isChanged() || vset.isChanged()) {
                update();
            }

        }else {
            category.readFrom(fry);
            title.readFrom(fry);
            description.readFrom(fry);
            start.readFrom(fry);
            end.readFrom(fry);
            rRule.readFrom(fry);
            color.readFrom(fry);
            vset.readFrom(fry);
        }
    }

    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);

        vset.writeTo(fry);
        category.writeTo(fry);

        if(issetTitle()) {
            title.writeTo(fry);
        }
        if(issetDescription()) {
            description.writeTo(fry);
        }

        if(issetStart()) {
            start.writeTo(fry);
        }
        if(issetEnd()) {
            end.writeTo(fry);
        }

        rRule.writeTo(fry);

        if(issetColor()) {
            color.writeTo(fry);
        }

    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Tag) {
            Tag tag = (Tag)o;
            return ( tag.getCategoryId() == getCategoryId() &&
                     tag.title.equals(title) &&
                     tag.description.equals(description) &&
                     tag.start.equals(start) &&
                     tag.end.equals(end) &&
                     tag.rRule.equals(rRule) &&
                     tag.color.equals(color)                 );
        }
        return false;
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
    protected void sync(MySQLEntry entry) {
        Tag tag = (Tag) entry;
        boolean update = false;

        if(category.doUpdate(tag.category)) {
            update = true;
        }
        if(title.doUpdate(tag.title)) {
            update = true;
        }
        if(description.doUpdate(tag.description)) {
            update = true;
        }
        if(start.doUpdate(tag.start)) {
            update = true;
        }
        if(end.doUpdate(tag.end)) {
            update = true;
        }
        if(rRule.doUpdate(tag.rRule)) {
            update = true;
        }
        if(color.doUpdate(tag.color)) {
            update = true;
        }
        if(vset.doUpdate(tag.vset)) {
            update = true;
        }

        if(update) {
            update();
        }
    }

    @Override
    protected char getType() {
        return TYPE_TAG;
    }

    @Override
    protected void addData(MySQL mysql) {
        mysql.add("category_id", category);
        mysql.add("title", title);
        mysql.add("description", description);
        mysql.add("start", start);
        mysql.add("end", end);
        mysql.add("rrule", rRule);
        mysql.add("color", color);
        mysql.add("vset", vset);
    }

    @Override
    protected void remove() {
        Data.Tags.remove(this);
    }

    public boolean issetTitle() {
        return !vset.hasBit(VSET_TITLE);
    }

    public boolean issetDescription() {
        return !vset.hasBit(VSET_DESCRIPTION);
    }

    public boolean issetStart() {
        return !vset.hasBit(VSET_START);
    }

    public boolean issetEnd() {
        return !vset.hasBit(VSET_END);
    }

    public boolean issetColor() {
        return !vset.hasBit(VSET_COLOR);
    }

    public int getCategoryId() {
        return category.getId();
    }

    public String getTitle() {
        return title.getValue();
    }

    public Date getStart() {
        return new Date(start.getValue());
    }

    public Date getEnd() {
        return new Date(end.getValue());
    }

    public void set(Category category, String title, String description, Date start, Date end, RRule rRule) {
        set(category, title, description, start, end, rRule, false, 0);
    }

    public void set(Category category, String title, String description, Date start, Date end, RRule rRule, int color) {
        set(category, title, description, start, end, rRule, true, color);
    }

    protected void set(Category category, String title, String description, Date start, Date end, RRule rRule, boolean set_color, int color) {
        vset.setValue((byte)0xFF);

        this.category.setValue(category);

        if (title == null) {
            vset.setBit(VSET_TITLE);
            this.title.setValue(" ");
        } else {
            vset.unsetBit(VSET_TITLE);
            this.title.setValue(title);
        }

        if (description == null) {
            vset.setBit(VSET_DESCRIPTION);
            this.description.setValue(" ");
        } else {
            vset.unsetBit(VSET_DESCRIPTION);
            this.description.setValue(description);
        }

        if (start == null) {
            vset.setBit(VSET_START);
            this.start.setValue(0);
        } else {
            vset.unsetBit(VSET_START);
            this.start.setValue(start.getInt());
        }
        if (end == null) {
            vset.setBit(VSET_END);
            this.end.setValue(0);
        } else {
            vset.unsetBit(VSET_END);
            this.end.setValue(end.getInt());
        }

        if(rRule == null) {
            this.rRule.setValue(null);
        }else {
            this.rRule.setValue(rRule);
        }

        if(set_color) {
            this.color.setValue(color);
            vset.unsetBit(VSET_COLOR);
        }else {
            vset.setBit(VSET_COLOR);
        }

        update();
    }

}
