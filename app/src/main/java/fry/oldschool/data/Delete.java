package fry.oldschool.data;

public class Delete extends Entry {

    public Delete(char type,int id) {
        this.type = (char)(BASETYPE_DELETE | type);
        this.id = id;
    }

    public Delete(char type,char c1,char c2) {
        this.type = (char)(BASETYPE_DELETE | type);
        id = c1 | (c2 << 16);
    }

    @Override
    protected String getConManString() {
        return type + "" + (char)(id & 65535) + "" + (char)((id >> 16) & 65535);
    }

    @Override
    protected boolean mysql() {
        String resp = getLine(getAddress(), "&id=" + id);
        return resp.equals("suc");
    }

    public String getAddress() {
        if((type & TYPE_TASKLIST) > 0) {
            return (DIR_TASKLIST + "delete.php");
        }else if((type & TYPE_TASKLIST_ENTRY) > 0) {
            return (DIR_TASKLIST_ENTRY + "delete.php");
        }else if((type & TYPE_CONTACT) > 0) {
            return (DIR_CONTACT + "delete.php");
        }else if((type & TYPE_CONTACT_GROUP) > 0) {
            return (DIR_CONTACT_GROUP + "delete.php");
        }else if((type & TYPE_CONTACT_REQUEST) > 0) {
            return (DIR_CONTACT_REQUEST + "decline.php");
        }
        return null;
    }

}
