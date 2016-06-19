package fry.oldschool.data;

import fry.oldschool.utils.FryFile;

public class Delete extends OfflineEntry {

    public Delete(char type,int id) {
        this.type = (char)(BASETYPE_DELETE | type);
        this.id = id;
    }

    @Override
    protected boolean mysql() {
        String resp = getLine(getAddress(), "&id=" + id);
        return resp.equals("suc");
    }

    @Override
    public void writeTo(FryFile file) {
        file.write(type);
        file.write(id);
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
