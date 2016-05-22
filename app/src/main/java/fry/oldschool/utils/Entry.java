package fry.oldschool.utils;

public abstract class Entry extends MySQL {

    protected static final char TYPE_CONTACT_DELETE = 1 ;

    protected static final char TYPE_CONTACTREQUEST_SEND = 2 ;

    protected static final char TYPE_CONTACTREQUEST_ACCEPT = 5 ;

    protected static final char TYPE_CONTACTREQUEST_DECLINE = 6 ;

    protected static final char TYPE_CONTACTGROUP = 7 ;

    protected static final char TYPE_CONTACTGROUP_DELETE = 8 ;

    protected static Entry create(String line) {
        switch(line.charAt(0)) {
            case TYPE_CONTACT_DELETE: return new Contact.Delete(line.substring(1));
            case TYPE_CONTACTREQUEST_SEND: return new ContactRequest.Send(line.substring(1));
            case TYPE_CONTACTREQUEST_ACCEPT: return new ContactRequest.Accept(line.substring(1));
            case TYPE_CONTACTREQUEST_DECLINE: return new ContactRequest.Decline(line.substring(1));
            case TYPE_CONTACTGROUP: return new ContactGroup(line.substring(1));
            case TYPE_CONTACTGROUP_DELETE: return new ContactGroup.Delete(line.substring(1));
        }
        return null;
    }

    protected abstract String getString();

}
