package fry.oldschool.data;

public abstract class Entry extends MySQL {

    public static final char BASETYPE_CREATE    = 0x0001;

    public static final char BASETYPE_UPDATE    = 0x0002;

    public static final char BASETYPE_DELETE    = 0x0004;

    public static final char BASETYPE_SHARE     = 0x0008;



    public static final char TYPE_CONTACT           = 0x0010;

    public static final char TYPE_CONTACT_GROUP     = 0x0020;

    public static final char TYPE_CONTACT_REQUEST   = 0x0040;

    public static final char TYPE_TASKLIST          = 0x0080;

    public static final char TYPE_TASKLIST_ENTRY    = 0x0100;

/*
    protected static final char TYPE_CALENDAR_ENTRY = 14 ;

    protected static final char TYPE_CALENDAR_ENTRY_DELETE = 15 ;

    protected static final char TYPE_CALENDAR_ENTRY_UPDATE = 16 ;

    protected static final char TYPE_CALENDAR_CATEGORY = 17 ;

    protected static final char TYPE_CALENDAR_CATEGORY_DELETE = 18 ;
*/

    protected static Entry create(char type,char c1,char c2) {
        if((type & BASETYPE_DELETE) > 0) {
            return new Delete(type,c1,c2);
        }else if((type & BASETYPE_UPDATE) > 0) {
            return new Delete(type,c1,c2);
        }
        return null;
    }

    protected char type;

    protected int id;

    protected abstract boolean mysql();

    protected abstract String getConManString();

}
