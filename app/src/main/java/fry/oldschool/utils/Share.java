package fry.oldschool.utils;

public class Share extends Entry {

    public static final byte PERMISSION_VIEW = 0;

    public static final byte PERMISSION_EDIT = 1;

    public static final byte PERMISSION_MORE = 2;

    protected int id;

    protected byte permission;

    protected char type;

    protected int user_id;

    public String email;

    public String name;

    protected Share(char type,int id,byte permission,Contact contact) {
        this.type = type;
        this.id = id;
        this.permission = permission;
        user_id = contact.user_id;
        email = contact.email;
        name = contact.name;
    }

    protected Share(String line) {
        type = line.charAt(0);
        String[] r = line.substring(1).split(S);
        id = Integer.parseInt(r[0]);
        permission = Byte.parseByte(r[1]);
    }

    @Override
    public boolean mysql() {
        String resp = getLine(getFileUrl(),"&share_id="+id+"&permission="+permission);
        return resp.equals("suc");
    }

    @Override
    public String getConManString() {
        return type + "" + id + S + permission;
    }

    public void setPermission(byte permission) {
        this.permission = permission;
        App.conMan.add(this);
    }

    public boolean canView() {
        return ( permission >= PERMISSION_VIEW );
    }

    public boolean canEdit() {
        return ( permission >= PERMISSION_EDIT );
    }

    public void allowEdit(boolean b) {
        byte p = ( b ? (byte)1 : 0 );
        if(p != permission) {
            permission = p;
            App.conMan.add(this);
        }
    }

    public boolean equals(Contact contact) {
        return ( contact.user_id == user_id );
    }

    protected String getFileUrl() {
        switch(type) {
            case TYPE_TASKLIST_SHARE_UPDATE: return "tasklist/share/update.php";
        }
        return null;
    }

}
