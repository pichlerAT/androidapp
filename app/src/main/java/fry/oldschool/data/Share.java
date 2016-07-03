package fry.oldschool.data;

public class Share extends MySQL {

    public static final byte PERMISSION_VIEW = 1;

    public static final byte PERMISSION_EDIT = 2;

    public static final byte PERMISSION_MORE = 3;

    protected byte permission;

    protected int share_id;

    protected String email;

    protected String name;

    public Share(char type, int id, int user_id, byte permission, int share_id, String email, String name) {
        super((char)(BASETYPE_SHARE | BASETYPE_UPDATE | type), id, user_id);
        this.permission = permission;
        this.share_id = share_id;
        this.email = email;
        this.name = name;
        if(id == 0) {
            this.type |= BASETYPE_CREATE;
        }
    }

    public Share(int share_id,Contact cont) {
        this((char)0, 0, cont.user_id, PERMISSION_VIEW, share_id, cont.email, cont.name);
    }

    public Share(int id,int share_id, int user_id, byte permission) {
        this((char)0, id, user_id, permission, share_id, null, null);
    }

    protected Share(char type,int share_id, byte permission,Contact contact) {
        this(type, 0, contact.user_id, permission, share_id, contact.email, contact.name);
    }

    protected Share(char type,int share_id,Contact contact) {
        this(type, 0, contact.user_id, PERMISSION_VIEW, share_id, contact.email, contact.name);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public Share backup() {
        return new Share(type, id, user_id, permission, share_id, email, name);
    }

    @Override
    public boolean mysql() {
        if(id == 0) {
            String resp = getLine(getFileUrl(type), "&share_user_id=" + user_id + "&share_id=" + share_id + "&permission=" + permission);
            if(resp != null) {
                id = Integer.parseInt(resp);
                type &= ~BASETYPE_CREATE;
                return true;
            }
            return false;
        }
        return (getLine(getFileUrl(type), "&id=" + id + "&permission=" + permission) != null);
    }

    @Override
    protected void synchronize(MySQL mysql) { }

    protected void delete() {
        this.type |= BASETYPE_DELETE;
        ConnectionManager.add(this);
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setPermission(byte permission) {
        this.permission = permission;
        ConnectionManager.add(this);
    }

    public boolean canView() {
        return ( permission >= PERMISSION_VIEW );
    }

    public boolean canEdit() {
        return ( permission >= PERMISSION_EDIT );
    }

    public boolean canMore() {
        return ( permission >= PERMISSION_MORE );
    }

    public boolean equals(Contact contact) {
        return ( contact.user_id == user_id );
    }

    protected String getFileUrl(char type) {
        if((type & BASETYPE_CREATE) > 0) {
            return (getPathUrl(type) + "create.php");

        }else if((type & BASETYPE_DELETE) > 0) {
            return (getPathUrl(type) + "update.php");

        }else if((type & BASETYPE_UPDATE) > 0) {
            return (getPathUrl(type) + "delete.php");
        }
        return null;
    }

    protected String getPathUrl(char type) {
        if((type & TYPE_TASKLIST) > 0) {
            return DIR_TASKLIST_SHARE;

        }else if((type & TYPE_CALENDAR) > 0) {
            return DIR_CALENDAR_SHARE;

        }else if((type & TYPE_CALENDAR_CATEGORY) > 0) {
            return DIR_CALENDAR_CATEGORY_SHARE;

        }else if((type & TYPE_CALENDAR_ENTRY) > 0) {
            return DIR_CALENDAR_ENTRY_SHARE;
        }
        return null;
    }

}
