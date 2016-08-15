package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;

public class Share extends Contact {

    public static final byte PERMISSION_NONE = 0;

    public static final byte PERMISSION_VIEW = 1;

    public static final byte PERMISSION_EDIT = 2;

    public static final byte PERMISSION_MORE = 3;

    protected byte permission;

    protected int share_id;

    protected Share(char type, int id, int user_id, int share_id, byte permission, String email, String name) {
        super(type, id, user_id, email, name);
        Logger.Log("Share", "Share(char,int,int,byte,int,String,String)");
        this.share_id = share_id;
        this.permission = permission;
    }

    protected Share(char type, int share_id, byte permission, Contact contact) {
        this(type, 0, contact.user_id, share_id, permission, contact.email, contact.name);
        Logger.Log("Share", "Share(char,byte,int,Contact)");
    }

    @Override
    public boolean mysql_create() {
        Logger.Log("Share", "mysql_create()");
        FryFile fry = executeMySQL(getFileUrl()+"create.php", "&share_user_id=" + signed(user_id) + "&share_id=" + signed(share_id) + "&permission=" + signed(permission));
        if(fry != null) {
            id = fry.getUnsignedInt();
            return store();
        }
        return false;
    }

    @Override
    public boolean mysql_update() {
        Logger.Log("Share", "mysql_update()");
        return (executeMySQL(getFileUrl()+"update.php", "&id="+signed(id)+"&permission="+signed(permission)) != null);
    }

    @Override
    public boolean mysql_delete() {
        Logger.Log("Share", "mysql_delete()");
        return (executeMySQL(getFileUrl()+"delete.php", "&id="+signed(id)) != null);
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("Share", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.writeUnsignedInt(share_id);
        fry.writeUnsignedByte(permission);
    }

    protected boolean store() {
        switch(getType()) {
            case TYPE_CALENDAR:
                Data.Timetable.Shares.addStorage(permission, id, user_id);
                return true;

            case TYPE_CALENDAR_CATEGORY:
                Category cat = Data.Categories.getById(share_id);
                if(cat == null) {
                    delete();
                    return false;
                }
                cat.shares.addStorage(permission, id, user_id);
                return true;

            case TYPE_CALENDAR_ENTRY:
                TimetableEntry ent = Data.Timetable.Entries.getById(share_id);
                if(ent == null) {
                    delete();
                    return false;
                }
                ent.shares.addStorage(permission, id, user_id);
                return true;

            case TYPE_TASKLIST:
                Tasklist tl = Data.Tasklists.getById(share_id);
                if(tl == null) {
                    delete();
                    return false;
                }
                tl.shares.addStorage(permission, id, user_id);
                return true;

        }
        return false;
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

    protected String getFileUrl() {
        Logger.Log("Share", "getFileUrl()");
        switch(getType()) {

            case TYPE_TASKLIST:
                return DIR_TASKLIST_SHARE;

            case TYPE_CALENDAR:
                return DIR_CALENDAR_SHARE;

            case TYPE_CALENDAR_CATEGORY:
                return DIR_CATEGORY_SHARE;

            case TYPE_CALENDAR_ENTRY:
                return DIR_CALENDAR_ENTRY_SHARE;

        }
        return null;
    }

}
