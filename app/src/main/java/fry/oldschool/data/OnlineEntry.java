package fry.oldschool.data;

public abstract class OnlineEntry extends MySQL {

    public static final char BASETYPE_CREATE    = 0x0001;

    public static final char BASETYPE_UPDATE    = 0x0002;

    public static final char BASETYPE_DELETE    = 0x0004;

    public static final char BASETYPE_SHARE     = 0x0008;



    public static final char TYPE_CONTACT           = 0x0010;

    public static final char TYPE_CONTACT_GROUP     = 0x0020;

    public static final char TYPE_CONTACT_REQUEST   = 0x0040;

    public static final char TYPE_TASKLIST          = 0x0080;

    public static final char TYPE_TASKLIST_ENTRY    = 0x0100;

    public static final char TYPE_CALENDAR          = 0x0200;

    public static final char TYPE_CALENDAR_CATEGORY = 0x0400;

    public static final char TYPE_CALENDAR_ENTRY    = 0x0800;


    protected char type;

    public int id;

    protected abstract boolean mysql();

}
