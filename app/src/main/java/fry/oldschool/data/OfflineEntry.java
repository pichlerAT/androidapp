package fry.oldschool.data;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Fryable;

public class OfflineEntry extends MySQL implements Fryable {

    public static void delete(char type, int id) {
        ConnectionManager.add(new OfflineEntry((char)(type | BASETYPE_DELETE), id));
    }

    public static void update(char type, int id) {
        ConnectionManager.add(new OfflineEntry((char)(type | BASETYPE_UPDATE), id));
    }

    protected OfflineEntry(char type, int id) {
        this.type = type;
        this.id = id;
    }

    @Override
    protected boolean mysql() {
        String[] data = getAddressData();
        return (getLine(data[0] + "update.php", data[1]) != null);
    }

    @Override
    public void writeTo(FryFile fry) {
        fry.write(type);
        fry.write(id);
    }

    @Override
    public void readFrom(FryFile fry) {
        type = fry.getChar();
        id = fry.getInt();
    }

    public String[] getAddressData() {
        if((type & BASETYPE_DELETE) > 0) {
            return new String[]{getDelete() + "delete.php", "&id=" + id};

        }else if((type & BASETYPE_UPDATE) > 0) {
            return getUpdate();
        }
        return null;
    }

    public String getDelete() {
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

    public String[] getUpdate() {
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
        return null;}
}
