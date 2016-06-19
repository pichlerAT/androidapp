package fry.oldschool.data;

import fry.oldschool.utils.Fryable;

public abstract class OfflineEntry extends OnlineEntry implements Fryable {

/*
    protected static final char TYPE_CALENDAR_ENTRY = 14 ;

    protected static final char TYPE_CALENDAR_ENTRY_DELETE = 15 ;

    protected static final char TYPE_CALENDAR_ENTRY_UPDATE = 16 ;

    protected static final char TYPE_CALENDAR_CATEGORY = 17 ;

    protected static final char TYPE_CALENDAR_CATEGORY_DELETE = 18 ;
*/

    protected static OfflineEntry create(char type, int id) {
        if((type & BASETYPE_DELETE) > 0) {
            return new Delete(type,id);
        }else if((type & BASETYPE_UPDATE) > 0) {
            return new Delete(type,id);
        }
        return null;
    }

}
