package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;

public class Category extends MySQLEntry implements Fryable {

    protected String name;

    protected int color;

    public ShareList shares = new ShareList(this);

    protected Category(FryFile fry) {
        super(fry);
        Logger.Log("TimetableCategory", "TimetableCategory(FryFile)");
        name = fry.getString();
        color = fry.getInt();
        shares.readFrom(fry);
    }

    protected Category(int id, int user_id, String name, int color) {
        super(id, user_id);
        Logger.Log("TimetableCategory", "TimetableCategory(int,int,String)");
        this.name = name;
        this.color = color;

        if(id != 0) {
            shares = new ShareList(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        Logger.Log("TimetableCategory", "equals(Object)");
        if(o instanceof Category) {
            Category c = (Category) o;
            return (c.id == id && c.name.equals(name) && color == c.color);
        }
        return false;
    }

    @Override
    public Category backup() {
        Logger.Log("TimetableCategory", "backup()");
        return new Category(id, user_id, name, color);
    }

    @Override
    protected boolean mysql_create() {
        Logger.Log("TimetableCategory", "mysql_create()");
        FryFile fry = executeMySQL(DIR_CATEGORY + "create.php", "&name=" + name + "&color=" + signed(color));
        if(fry != null) {
            id = fry.getUnsignedInt();
            //user_id = User.getId();
            shares = new ShareList(this);

            return true;
        }
        return false;
    }

    @Override
    protected boolean mysql_update() {
        Logger.Log("TimetableCategory", "mysql_update()");
        return (executeMySQL(DIR_CATEGORY + "update.php", "&id=" + signed(id) + "&name=" + name + "&color=" + signed(color)) != null);
    }

    @Override
    protected byte getType() {
        return TYPE_CATEGORY;
    }

    @Override
    protected String getPath() {
        return DIR_CATEGORY;
    }

    @Override
    protected void synchronize(MySQL mysql) {
        Logger.Log("TimetableCategory", "synchronize(MySQL)");
        Category c = (Category) mysql;
        name = c.name;
        color = c.color;
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
    public void writeTo(FryFile fry) {
        Logger.Log("TimetableCategory", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.writeString(name);
        fry.writeInt(color);
        shares.writeTo(fry);
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        Logger.Log("TimetableCategory", "getName()");
        return name;
    }

    public void set(String name, int color) {
        this.name = name;
        this.color = color;
        update();
    }

    public void setColor(int color) {
        set(name, color);
    }

    public void setName(String name) {
        Logger.Log("TimetableCategory", "setName(String)");
        set(name, color);
    }

    @Override
    public void remove() {
        Logger.Log("TimetableCategory", "delete()");
        Data.Categories.remove(this);
    }

}
