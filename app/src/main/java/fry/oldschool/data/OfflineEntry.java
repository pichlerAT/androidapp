package fry.oldschool.data;

import fry.oldschool.utils.Fryable;

public abstract class OfflineEntry extends OnlineEntry implements Fryable {

    protected static OfflineEntry create(char type, int id) {
        if((type & BASETYPE_DELETE) > 0) {
            return new Delete(type,id);
        }else if((type & BASETYPE_UPDATE) > 0) {
            return new Delete(type,id);
        }
        return null;
    }

}
