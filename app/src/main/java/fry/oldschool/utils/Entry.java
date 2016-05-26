package fry.oldschool.utils;

public abstract class Entry extends MySQL {

    protected static final char TYPE_CONTACT_DELETE = 1 ;

    protected static final char TYPE_CONTACTREQUEST_SEND = 2 ;

    protected static final char TYPE_CONTACTREQUEST_ACCEPT = 3 ;

    protected static final char TYPE_CONTACTREQUEST_DECLINE = 4 ;

    protected static final char TYPE_CONTACTGROUP = 5 ;

    protected static final char TYPE_CONTACTGROUP_DELETE = 6 ;

    protected static final char TYPE_TASKLIST = 7 ;

    protected static final char TYPE_TASKLIST_DELETE = 8 ;

    protected static final char TYPE_TASKLIST_ENTRY = 9 ;

    protected static final char TYPE_TASKLIST_ENTRY_DELETE = 10 ;

    protected static Entry create(String line) {
        switch(line.charAt(0)) {
            case TYPE_CONTACT_DELETE: return new Contact.Delete(line.substring(1));
            case TYPE_CONTACTREQUEST_SEND: return new ContactRequest.Send(line.substring(1));
            case TYPE_CONTACTREQUEST_ACCEPT: return new ContactRequest.Accept(line.substring(1));
            case TYPE_CONTACTREQUEST_DECLINE: return new ContactRequest.Decline(line.substring(1));
            case TYPE_CONTACTGROUP: return new ContactGroup(line.substring(1));
            case TYPE_CONTACTGROUP_DELETE: return new ContactGroup.Delete(line.substring(1));
            case TYPE_TASKLIST: return new TaskList(line.substring(1));
            case TYPE_TASKLIST_DELETE: return new TaskList.Delete(line.substring(1));
            case TYPE_TASKLIST_ENTRY: return new TaskListEntry(line.substring(1));
            case TYPE_TASKLIST_ENTRY_DELETE: return new TaskListEntry.Delete(line.substring(1));
        }
        return null;
    }

    protected abstract String getConManString();

}
