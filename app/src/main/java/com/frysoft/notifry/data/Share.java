package com.frysoft.notifry.data;

import com.frysoft.notifry.data.value.ValueUnsignedByte;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;

public class Share extends MySQLEntry {

    public static final byte PERMISSION_NONE = 0;

    public static final byte PERMISSION_VIEW = 1;

    public static final byte PERMISSION_EDIT = 2;

    public static final byte PERMISSION_EXECUTE = 3;

    protected ValueUnsignedByte permission = new ValueUnsignedByte();

    protected Contact contact;

    protected MySQLEntry sharedEntry;

    /**
     * Used in ShareList only
     */
    protected Share(byte permission, Contact contact, MySQLEntry sharedEntry) {
        super(0, contact.user_id, 0);
        this.permission.setValue(permission);
        this.contact = contact;
        this.sharedEntry = sharedEntry;

        if(this.permission.isChanged()) {
            update();
        }
    }

    protected Share(FryFile fry) {
        super(fry);
        permission.readFrom(fry);

        contact = ContactList.getContactByUserId(user_id);
    }

    @Override
    protected void addData(MySQL mysql) {
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
        Share share = (Share) entry;
        if(permission.doUpdate(share.permission)) {
            update();
        }
    }

    @Override
    protected boolean mysql() {
        if(id == 0) {
            MySQL mysql = new MySQL(getPath(), PHP_CREATE);
            mysql.addId("share_user_id", user_id);
            mysql.addId("share_id", sharedEntry.id);
            mysql.add("permission", permission);
            FryFile fry = mysql.execute();

            if (fry == null) {
                return false;
            }

            update_time = fry.readLong();
            id = fry.readId();
            if(id == 0) {
                // TODO message: entry is already shared with this user
                remove();
            }
            return true;

        }else {
            MySQL mysql = new MySQL(getPath(), PHP_UPDATE);
            mysql.addId("id", id);
            mysql.add("permission", permission);
            FryFile fry = mysql.execute();

            if (fry == null) {
                return false;
            }

            update_time = fry.readLong();
            return true;
        }
    }

    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);
        permission.writeTo(fry);
    }

    @Override
    protected void remove() {
        switch(sharedEntry.getType()) {
            case TYPE_TIMETABLE:
                ((Timetable) sharedEntry).shares.remove(this);
                break;

            case TYPE_CATEGORY:
                ((Category) sharedEntry).shares.remove(this);
                break;

            case TYPE_TIMETABLE_ENTRY:
                ((TimetableEntry) sharedEntry).shares.remove(this);
                break;

            case TYPE_TASKLIST:
                ((Tasklist) sharedEntry).shares.remove(this);

        }
    }

    @Override
    protected void synchronize(MySQLEntry entry) {
        super.synchronize(entry);
        Share share = (Share) entry;
        permission = share.permission;
    }

    @Override
    protected char getType() {
        return (char)(sharedEntry.getType() | SHARE);
    }

    public String getEmail() {
        return contact.email.getValue();
    }

    public String getName() {
        return contact.name.getValue();
    }

    public void setPermission(byte permission) {
        Logger.Log("Share", "setPermission(byte)");
        this.permission.setValue(permission);
        update();
    }

    public boolean hasPermissions() {
        Logger.Log("Share", "hasPermissions()");
        return (permission.getValue() > 0);
    }

    public boolean hasPermissionView() {
        Logger.Log("Share", "hasPermissionView()");
        return (permission.getValue() >= PERMISSION_VIEW);
    }

    public boolean hasPermissionEdit() {
        Logger.Log("Share", "hasPermissionEdit()");
        return (permission.getValue() >= PERMISSION_EDIT);
    }

    public boolean hasPermissionExecute() {
        Logger.Log("Share", "hasPermissionMore()");
        return (permission.getValue() >= PERMISSION_EXECUTE);
    }

    public boolean equals(Contact contact) {
        Logger.Log("Share", "equals(Contact)");
        return (contact.user_id == user_id);
    }

}
