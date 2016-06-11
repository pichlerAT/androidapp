package fry.oldschool.utils;

public abstract class Entry extends MySQL {

    protected static final char TYPE_CONTACT_DELETE = 1 ;

    protected static final char TYPE_CONTACTREQUEST_SEND = 2 ;

    protected static final char TYPE_CONTACTREQUEST_ACCEPT = 3 ;

    protected static final char TYPE_CONTACTREQUEST_DECLINE = 4 ;

    protected static final char TYPE_CONTACTGROUP_UPDATE = 5 ;

    protected static final char TYPE_CONTACTGROUP_DELETE = 6 ;

    protected static final char TYPE_TASKLIST_UPDATE = 7 ;

    protected static final char TYPE_TASKLIST_DELETE = 8 ;

    protected static final char TYPE_TASKLIST_ENTRY_UPDATE = 9 ;

    protected static final char TYPE_TASKLIST_ENTRY_DELETE = 10 ;

    protected static final char TYPE_TASKLIST_SHARE_UPDATE = 11 ;

    protected static final char TYPE_CALENDAR_ENTRY = 13 ;

    protected static final char TYPE_CALENDAR_ENTRY_DELETE = 14 ;

    protected static final char TYPE_CALENDAR_CATEGORY = 15 ;

    protected static final char TYPE_CALENDAR_CATEGORY_DELETE = 16 ;

    protected static Entry create(String line) {
        if(line == null) {
            return null;
        }
        switch(line.charAt(0)) {
            case TYPE_CONTACT_DELETE: return new Contact.Delete(line.substring(1));
            case TYPE_CONTACTREQUEST_SEND: return new ContactRequest.Send(line.substring(1));
            case TYPE_CONTACTREQUEST_ACCEPT: return new ContactRequest.Accept(line.substring(1));
            case TYPE_CONTACTREQUEST_DECLINE: return new ContactRequest.Decline(line.substring(1));
            case TYPE_CONTACTGROUP_UPDATE: return new ContactGroup.Update(line.substring(1));
            case TYPE_CONTACTGROUP_DELETE: return new ContactGroup.Delete(line.substring(1));
            case TYPE_TASKLIST_UPDATE: return new TaskList.Update(line.substring(1));
            case TYPE_TASKLIST_DELETE: return new TaskList.Delete(line.substring(1));
            case TYPE_TASKLIST_ENTRY_UPDATE: return new TaskListEntry.Update(line.substring(1));
            case TYPE_TASKLIST_ENTRY_DELETE: return new TaskListEntry.Delete(line.substring(1));
            case TYPE_TASKLIST_SHARE_UPDATE:
                return new Share(line);
        }
        return null;
    }

    protected abstract String getConManString();

}
