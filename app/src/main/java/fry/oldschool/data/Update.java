package fry.oldschool.data;

public class Update extends Entry {

    public Update(char type,int id) {
        this.type = (char)(BASETYPE_UPDATE | type);
        this.id = id;
    }

    public Update(char type,char c1,char c2) {
        this.type = (char)(BASETYPE_UPDATE | type);
        id = c1 | (c2 << 16);
    }

    @Override
    protected String getConManString() {
        return type + "" + getString();
    }

    @Override
    protected boolean mysql() {
        String[] data = getAddressData();
        String resp = getLine(data[0],data[1]);
        return resp.equals("suc");
    }

    public String getString() {
        return ( (char)(id & 65535) + "" + (char)((id >> 16) & 65535) );
    }

    public String[] getAddressData() {

        if((type & TYPE_CONTACT_GROUP) > 0) {
            ContactGroup grp = ContactList.findContactGroupById(id);
            if(grp == null) {
                return null;
            }
            return new String[]{DIR_CONTACT_GROUP + "update.php",grp.getUpdateString()};

        }else if((type & TYPE_TASKLIST_ENTRY) > 0) {
            TasklistEntry ent = TasklistManager.findTasklistEntryById(id);
            if (ent == null) {
                return null;
            }
            return new String[]{DIR_TASKLIST_ENTRY + "update.php", ent.getUpdateString()};

        }else if((type & TYPE_TASKLIST) > 0) {
            Tasklist tl = TasklistManager.findTasklistById(id);
            if (tl == null) {
                return null;
            }
            return new String[]{DIR_TASKLIST + "update.php", tl.getUpdateString()};

        }
        return null;
    }

}
