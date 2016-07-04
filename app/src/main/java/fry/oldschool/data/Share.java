package fry.oldschool.data;

import fry.oldschool.utils.FryFile;

public class Share extends Contact {

    public static final byte PERMISSION_VIEW = 1;

    public static final byte PERMISSION_EDIT = 2;

    public static final byte PERMISSION_MORE = 3;

    protected byte permission;

    protected int share_id;

    public Share(char type, int id, int user_id, byte permission, int share_id, String email, String name) {
        super((char)(BASETYPE_SHARE | type), id, user_id, email, name);
        this.permission = permission;
        this.share_id = share_id;
    }

    public Share(int share_id,Contact cont) {
        this((char)0, 0, cont.user_id, PERMISSION_VIEW, share_id, cont.email, cont.name);
    }

    public Share(int id, int user_id, byte permission, int share_id) {
        this((char)0, id, user_id, permission, share_id, null, null);
    }

    protected Share(char type, byte permission, int share_id, Contact contact) {
        this(type, 0, contact.user_id, permission, share_id, contact.email, contact.name);
    }

    protected Share(char type,int share_id,Contact contact) {
        this(type, 0, contact.user_id, PERMISSION_VIEW, share_id, contact.email, contact.name);
    }

    protected Share(Share share) {
        this(share.type, share.id, share.user_id, share.permission, share.share_id, share.email, share.name);
    }

    protected Share(FryFile fry) {
        super(fry);
        permission = fry.getByte();
        share_id = fry.getInt();
    }

    @Override
    public boolean mysql_create() {
        String resp = getLine(getFileUrl()+"create.php", "&share_user_id=" + user_id + "&share_id=" + share_id + "&permission=" + permission);
        if(resp != null) {
            id = Integer.parseInt(resp);
            type &= ~BASETYPE_CREATE;
            return true;
        }
        return false;
    }

    @Override
    public boolean mysql_update() {
        return (getLine(getFileUrl()+"update.php", "&id="+id+"&permission="+permission) != null);
    }

    @Override
    public boolean mysql_delete() {
        if(id == 0) {
            return (getLine(getFileUrl()+"delete_without_id.php", "&share_user_id="+user_id+"&share_id="+share_id) != null);
        }
        return (getLine(getFileUrl()+"delete.php", "&id="+id) != null);
    }

    @Override
    public void writeTo(FryFile fry) {
        super.writeTo(fry);
        fry.write(permission);
        fry.write(share_id);
    }

    protected void deleteWithoutId() {
        if(user_id != 0 && share_id != 0) {
            id = 0;
            type = (char)((type & TYPE) | BASETYPE_DELETE);
            ConnectionManager.add(this);
        }
    }

    public void setPermission(byte permission) {
        this.permission = permission;
        update();
    }

    public boolean hasPermissions() {
        return (permission > 0);
    }

    public boolean hasPermissionView() {
        return ( permission >= PERMISSION_VIEW );
    }

    public boolean hasPermissionEdit() {
        return ( permission >= PERMISSION_EDIT );
    }

    public boolean hasPermissionMore() {
        return ( permission >= PERMISSION_MORE );
    }

    public boolean equals(Contact contact) {
        return ( contact.user_id == user_id );
    }

    protected String getFileUrl() {
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
