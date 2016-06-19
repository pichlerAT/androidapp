package fry.oldschool.data;

public class Share extends OnlineEntry {

    public static final byte PERMISSION_VIEW = 1;

    public static final byte PERMISSION_EDIT = 2;

    public static final byte PERMISSION_MORE = 3;

    protected byte permission;

    protected int user_id;

    protected int share_id;

    public String email;

    public String name;

    protected Share(char type,int id,int share_id,byte permission,Contact contact) {
        this.id = id;
        this.permission = permission;
        this.type = (char)(BASETYPE_SHARE | BASETYPE_UPDATE | type);
        this.share_id = share_id;
        user_id = contact.user_id;
        email = contact.email;
        name = contact.name;
        if(id == 0) {
            this.type |= BASETYPE_CREATE;
        }
    }

    protected Share(char type,int share_id, byte permission,Contact contact) {
        this(type,0,share_id,permission,contact);
    }

    protected Share(char type,int share_id,Contact contact) {
        this(type,0,share_id,PERMISSION_VIEW,contact);
    }

    @Override
    public boolean mysql() {
        if(id == 0) {
            String resp = getLine(getFileUrl(type), "&share_user_id=" + user_id + "&share_id=" + share_id + "&permission=" + permission);
            if(resp.substring(0,3).equals("suc")) {
                id = Integer.parseInt(resp.substring(4));
                type &= ~BASETYPE_CREATE;
                return true;
            }
            return false;
        }
        String resp = getLine(getFileUrl(type), "&id=" + id + "&permission=" + permission);
        return resp.equals("suc");
    }

    protected void delete() {
        this.type |= BASETYPE_DELETE;
        ConnectionManager.add(this);
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

    public void allowEdit(boolean b) {
        byte p = ( b ? (byte)1 : 0 );
        if(p != permission) {
            permission = p;
            ConnectionManager.add(this);
        }
    }

    public boolean equals(Contact contact) {
        return ( contact.user_id == user_id );
    }

    protected String getFileUrl(char type) {
        if((type & BASETYPE_CREATE) > 0) {
            if((type & TYPE_TASKLIST) > 0) {
                return (DIR_TASKLIST_SHARE + "create.php");
            }
        }else if((type & BASETYPE_DELETE) > 0) {
            if((type & TYPE_TASKLIST) > 0) {
                return (DIR_TASKLIST_SHARE + "delete.php");
            }
        }else if((type & BASETYPE_UPDATE) > 0) {
            if ((type & TYPE_TASKLIST) > 0) {
                return (DIR_TASKLIST_SHARE + "update.php");
            }
        }
        return null;
    }

}
