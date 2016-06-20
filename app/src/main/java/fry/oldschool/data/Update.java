package fry.oldschool.data;

import fry.oldschool.utils.FryFile;

public class Update extends OfflineEntry {

    public Update(char type,int id) {
        this.type = (char)(BASETYPE_UPDATE | type);
        this.id = id;
    }

    @Override
    protected boolean mysql() {
        String[] data = getAddressData();
        return (getLine(data[0],data[1]) != null);
    }

    @Override
    public void writeTo(FryFile file) {
        file.write(type);
        file.write(id);
    }

    public String[] getAddressData() {

        if((type & TYPE_CONTACT_GROUP) > 0) {
            ContactGroup grp = ContactList.getContactGroupById(id);
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
