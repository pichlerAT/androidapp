package fry.oldschool.data;

public class Delete extends OfflineEntry {

    public Delete(char type,int id) {
        this.type = (char)(BASETYPE_DELETE | type);
        this.id = id;
    }

    @Override
    protected boolean mysql() {
        return (getLine(getAddress() + "delete.php", "&id=" + id) != null);
    }

    public String getAddress() {
        if((type & TYPE_TASKLIST) > 0) {
            return DIR_TASKLIST;

        }else if((type & TYPE_TASKLIST_ENTRY) > 0) {
            return DIR_TASKLIST_ENTRY;

        }else if((type & TYPE_CONTACT) > 0) {
            return DIR_CONTACT;

        }else if((type & TYPE_CONTACT_GROUP) > 0) {
            return DIR_CONTACT_GROUP;

        }else if((type & TYPE_CONTACT_REQUEST) > 0) {
            return DIR_CONTACT_REQUEST;

        }
        return null;
    }
}
