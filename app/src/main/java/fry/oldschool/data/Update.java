package fry.oldschool.data;


public class Update extends OfflineEntry {

    public Update(char type,int id) {
        this.type = (char)(BASETYPE_UPDATE | type);
        this.id = id;
    }

    @Override
    protected boolean mysql() {
        String[] data = getAddressData();
        return (getLine(data[0] + "update.php", data[1]) != null);
    }

    public String[] getAddressData() {

        if((type & TYPE_CONTACT_GROUP) > 0) {
            ContactGroup grp = ContactList.getContactGroupById(id);
            if(grp == null) {
                return null;
            }
            return new String[]{DIR_CONTACT_GROUP,grp.getUpdateString()};

        }else if((type & TYPE_TASKLIST_ENTRY) > 0) {
            TasklistEntry ent = TasklistManager.getTasklistEntryById(id);
            if (ent == null) {
                return null;
            }
            return new String[]{DIR_TASKLIST_ENTRY, ent.getUpdateString()};

        }else if((type & TYPE_TASKLIST) > 0) {
            Tasklist tl = TasklistManager.getTasklistById(id);
            if (tl == null) {
                return null;
            }
            return new String[]{DIR_TASKLIST, tl.getUpdateString()};

        }else if((type & TYPE_CALENDAR_CATEGORY) > 0) {
            TimetableCategory cat = Timetable.getCategoryById(id);
            if(cat == null) {
                return null;
            }
            return new String[]{DIR_CALENDAR_CATEGORY, cat.getUpdateString()};

        }else if((type & TYPE_CALENDAR_ENTRY) > 0) {
            TimetableEntry ent = Timetable.getEntryById(id);
            if(ent == null) {
                return null;
            }
            return new String[]{DIR_CALENDAR_ENTRY, ent.getUpdateString()};
        }
        return null;
    }

}
