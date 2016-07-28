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

    public Share(char type, int id, int user_id, byte permission, int share_id, String email, String name) {
        super(type, id, user_id, email, name);
        Logger.Log("Share", "Share(char,int,int,byte,int,String,String)");
        this.permission = permission;
        this.share_id = share_id;
    }

    protected Share(char type, byte permission, int share_id, Contact contact) {
        this(type, 0, contact.user_id, permission, share_id, contact.email, contact.name);
        Logger.Log("Share", "Share(char,byte,int,Contact)");
    }

    @Override
    public boolean mysql_create() {
        Logger.Log("Share", "mysql_create()");
        String resp = getLine(getFileUrl()+"create.php", "&share_user_id=" + user_id + "&share_id=" + share_id + "&permission=" + permission);
        if(resp != null) {
            id = Integer.parseInt(resp);
            type = getType();
            return store();
        }
        return false;
    }

    @Override
    public boolean mysql_update() {
        Logger.Log("Share", "mysql_update()");
        return (getLine(getFileUrl()+"update.php", "&share_id="+id+"&permission="+permission) != null);
    }

    @Override
    public boolean mysql_delete() {
        Logger.Log("Share", "mysql_delete()");
        //if(share_id == 0) {     TODO Stefan: share delete_without_id.php needed?
        //    return (getLine(getFileUrl()+"delete_without_id.php", "&share_user_id="+user_id+"&share_id="+share_id) != null);
        //}
        return (getLine(getFileUrl()+"delete.php", "&share_id="+id) != null);
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("Share", "writeTo(FryFile)");
        super.writeTo(fry);
        fry.write(permission);
        fry.write(share_id);
    }

    protected boolean store() {
        switch(type) {
            case TYPE_CALENDAR:
                Timetable.shares.addStorage(permission, id, user_id);
                return true;

            case TYPE_CALENDAR_CATEGORY:
                TimetableCategory cat = Timetable.getCategoryById(share_id);
                if(cat == null) {
                    delete();
                    return false;
                }
                cat.shares.addStorage(permission, id, user_id);
                return true;

            case TYPE_CALENDAR_ENTRY:
                TimetableEntry ent = Timetable.getEntryById(share_id);
                if(ent == null) {
                    delete();
                    return false;
                }
                ent.shares.addStorage(permission, id, user_id);
                return true;

            case TYPE_TASKLIST:
                Tasklist tl = TasklistManager.getTasklistById(share_id);
                if(tl == null) {
                    delete();
                    return false;
                }
                tl.shares.addStorage(permission, id, user_id);
                return true;

        }
        return false;
    }

    protected void deleteWithoutId() {
        Logger.Log("Share", "deleteWithoutId()");
        if(user_id != 0 && share_id != 0) {
            id = 0;
            type = (char)((type & TYPE) | BASETYPE_DELETE);
            ConnectionManager.add(this);
        }
    }

    public void setPermission(byte permission) {
        Logger.Log("Share", "setPermission(byte)");
        this.permission = permission;
        if(id == 0) {
            create();
        }else {
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
                return DIR_CALENDAR_CATEGORY_SHARE;

            case TYPE_CALENDAR_ENTRY:
                return DIR_CALENDAR_ENTRY_SHARE;

        }
        return null;
    }

}
