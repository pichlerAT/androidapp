package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;

public class Share extends Contact {

    public static final byte PERMISSION_NONE = 0;

    public static final byte PERMISSION_VIEW = 1;

    public static final byte PERMISSION_EDIT = 2;

    public static final byte PERMISSION_MORE = 3;

    protected byte permission;

    protected MySQL sharedEntry;

    protected Share(int id, int user_id, byte permission, String email, String name, MySQL sharedEntry) {
        super(id, user_id, email, name);
        Logger.Log("Share", "Share(char,int,int,byte,int,String,String)");
        this.sharedEntry = sharedEntry;
        this.permission = permission;
    }

    protected Share(byte permission, Contact contact, MySQL sharedEntry) {
        this(0, contact.user_id, permission, contact.email, contact.name, sharedEntry);
        Logger.Log("Share", "Share(char,byte,int,Contact)");
    }

    @Override
    public boolean mysql_create() {
        Logger.Log("Share", "mysql_create()");
        FryFile fry = executeMySQL(getPath()+"share/create.php", "&share_user_id=" + signed(user_id) + "&share_id=" + signed(sharedEntry.id) + "&permission=" + signed(permission));
        if(fry != null) {
            id = fry.getUnsignedInt();
            //user_id = User.getId();
            store();
            return true;
        }
        return false;
    }

    @Override
    public boolean mysql_update() {
        Logger.Log("Share", "mysql_update()");
        return (executeMySQL(getPath()+"share/update.php", "&id="+signed(id)+"&permission="+signed(permission)) != null);
    }

    @Override
    public boolean mysql_delete() {
        Logger.Log("Share", "mysql_delete()");
        return (executeMySQL(getPath()+"share/delete.php", "&id="+signed(id)) != null);
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("Share", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.writeUnsignedInt(sharedEntry.id);
        fry.writeUnsignedByte(permission);
    }

    protected void store() {
        switch(sharedEntry.getType()) {
            case TYPE_CALENDAR:
                Data.Timetable.Shares.addStorage(permission, id, user_id);
                return;

            case TYPE_CATEGORY:
                ((Category)sharedEntry).shares.addStorage(permission, id, user_id);
                return;

            case TYPE_CALENDAR_ENTRY:
                ((TimetableEntry)sharedEntry).shares.addStorage(permission, id, user_id);
                return;

            case TYPE_TASKLIST:
                ((Tasklist)sharedEntry).shares.addStorage(permission, id, user_id);

        }
    }

    public void setPermission(byte permission) {
        Logger.Log("Share", "setPermission(byte)");
        this.permission = permission;
        if(isOnline()) {
            update();
        }
    }

    public boolean hasPermissions() {
        Logger.Log("Share", "hasPermissions()");
        return (permission > 0);
    }

    public boolean hasPermissionView() {
        Logger.Log("Share", "hasPermissionView()");
        return ( permission >= PERMISSION_VIEW );
    }

    public boolean hasPermissionEdit() {
        Logger.Log("Share", "hasPermissionEdit()");
        return ( permission >= PERMISSION_EDIT );
    }

    public boolean hasPermissionMore() {
        Logger.Log("Share", "hasPermissionMore()");
        return ( permission >= PERMISSION_MORE );
    }

    public boolean equals(Contact contact) {
        Logger.Log("Share", "equals(Contact)");
        return ( contact.user_id == user_id );
    }
/*
    protected String getFileUrl() {
        Logger.Log("Share", "getFileUrl()");
        switch(sharedEntry.getType()) {

            case TYPE_TASKLIST:
                return DIR_TASKLIST_SHARE;

            case TYPE_CALENDAR:
                return DIR_CALENDAR_SHARE;

            case TYPE_CATEGORY:
                return DIR_CATEGORY_SHARE;

            case TYPE_CALENDAR_ENTRY:
                return DIR_CALENDAR_ENTRY_SHARE;

        }
        return null;
    }
*/
}
