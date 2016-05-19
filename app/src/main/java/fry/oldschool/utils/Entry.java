package fry.oldschool.utils;

public abstract class Entry extends MySQL {

    protected static final byte type_contact = 1;
    protected static final byte type_contactrequest = 2;
    protected static final byte type_tasklist = 3;
    protected static final byte type_tasklistentry = 4;

    protected void update() {
        App.conMan.add(this);
    }

    protected String getConnectionManagerString() {
        return ( getType() + "" );
    }

    protected abstract byte getType();

    protected abstract boolean mysql_update();

}
