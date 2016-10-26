package com.frysoft.notifry.data;

import com.frysoft.notifry.data.value.ValueInteger;
import com.frysoft.notifry.data.value.ValueString;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.Date;

public class Category extends MySQLEntry implements Fryable {

    protected int owner_id;

    protected ValueString name = new ValueString();

    protected ValueInteger color = new ValueInteger();

    public ShareList shares = new ShareList(this);

    protected Category(FryFile fry) {
        super(fry);
        owner_id = fry.readId();

        name.readFrom(fry);
        color.readFrom(fry);

        shares.readFrom(fry);

        if(name.isChanged() || color.isChanged()) {
            update();
        }
    }

    protected Category(int id, int user_id, String name, int color) {
        super(id, user_id, Date.getMillis());
        this.name.setValue(name);
        this.color.setValue(color);
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("TimetableCategory", "equals(Object)");
        if(o instanceof Category) {
            Category c = (Category) o;
            return (c.id == id && c.name.equals(name) && c.color.equals(color));
        }
        return false;
    }

    @Override
    protected void addData(MySQL mysql) {
        mysql.add("name", name);
        mysql.add("color", color);
    }

    @Override
    public boolean canEdit() {
        Logger.Log("TimetableCategory", "canEdit()");
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
        Category cat = (Category) entry;
        boolean update = false;

        if(name.doUpdate(cat.name)) {
            update = true;
        }
        if(color.doUpdate(cat.color)) {
            update = true;
        }

        shares = cat.shares;

        if(update) {
            update();
        }
    }

    @Override
    protected char getType() {
        return TYPE_CATEGORY;
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("TimetableCategory", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.writeId(owner_id);
        name.writeTo(fry);
        color.writeTo(fry);

        shares.writeTo(fry);
    }

    @Override
    protected void remove() {
        Logger.Log("TimetableCategory", "delete()");
        Data.Categories.remove(this);
    }

    public int getColor() {
        return color.getValue();
    }

    public String getName() {
        Logger.Log("TimetableCategory", "getName()");
        return name.getValue();
    }

    public void set(String name, int color) {
        this.name.setValue(name);
        this.color.setValue(color);
        update();
    }

    public void setColor(int color) {
        this.color.setValue(color);
        update();
    }

    public void setName(String name) {
        this.name.setValue(name);
        update();
    }

}
