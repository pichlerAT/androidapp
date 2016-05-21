package fry.oldschool.utils;

public abstract class Entry extends MySQL {

    protected static final byte TYPE_CONTACT = 1 ;

    protected static final byte TYPE_CONTACTREQUEST_SEND = 2 ;

    protected static final byte TYPE_TASKLIST = 3 ;

    protected static final byte TYPE_TASKLISTENTRY = 4 ;

    protected static final byte TYPE_CONTACTREQUEST_ACCEPT = 5 ;

    protected static final byte TYPE_CONTACTREQUEST_DECLINE = 6 ;

    protected String getConnectionManagerString() {
        return ( getType() + "" );
    }

    protected abstract byte getType();

    protected abstract boolean mysql_update();

}