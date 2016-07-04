package fry.oldschool.data;

import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Logger;

public abstract class MySQLEntry extends MySQL {

    public static MySQLEntry load(char type, int id) {
        Logger.Log("MySQLEntry#load(char,int)");
        switch((char)(type & TYPE)) {

            case TYPE_CALENDAR_CATEGORY:
                return Timetable.getCategoryById(id);

            case TYPE_CALENDAR_ENTRY:
                return Timetable.getEntryById(id);

            case TYPE_CONTACT_GROUP:
                return ContactList.getContactGroupById(id);

            case TYPE_TASKLIST:
                return TasklistManager.getTasklistById(id);

            case TYPE_TASKLIST_ENTRY:
                return TasklistManager.getTasklistEntryById(id);

        }
        return null;
    }

    protected MySQLEntry(FryFile fry) {
        super(fry);
        Logger.Log("MySQLEntry#MySQLEntry(FryFile)");
    }

    protected MySQLEntry(char type, int id, int user_id) {
        super(type, id, user_id);
        Logger.Log("MySQLEntry#MySQLEntry(char,int,int)");
    }

    @Override
    public abstract boolean equals(Object o);

    public abstract Object backup();

    protected abstract void synchronize(MySQL mysql);

    public abstract boolean canEdit();

}
