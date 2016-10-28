package com.frysoft.notifry.data;

import android.support.annotation.CallSuper;

import com.frysoft.notifry.utils.Date;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;

public abstract class MySQLEntry implements Fryable {

    static final String PATH_MYSQL                    =                         "mysql/"    ;
    static final String PATH_REGISTER                 =                         "register/" ;
    static final String PATH_USER                     =                         "user/"     ;
    static final String PATH_USER_CHANGE              = PATH_USER             + "change/"   ;
    static final String PATH_TASKLIST                 = PATH_MYSQL            + "tasklist/" ;
    static final String PATH_TASKLIST_ENTRY           = PATH_TASKLIST         + "entry/"    ;
    static final String PATH_TASKLIST_SHARE           = PATH_TASKLIST         + "share/"    ;
    static final String PATH_CONTACT                  = PATH_MYSQL            + "contact/"  ;
    static final String PATH_CONTACT_GROUP            = PATH_CONTACT          + "group/"    ;
    static final String PATH_CONTACT_REQUEST          = PATH_CONTACT          + "request/"  ;
    static final String PATH_TIMETABLE                = PATH_MYSQL            + "timetable/" ;
    static final String PATH_TIMETABLE_SHARE          = PATH_TIMETABLE        + "share/"    ;
    static final String PATH_CATEGORY                 = PATH_MYSQL            + "category/" ;
    static final String PATH_CATEGORY_SHARE           = PATH_CATEGORY         + "share/"    ;
    static final String PATH_TIMETABLE_ENTRY          = PATH_TIMETABLE        + "entry/"    ;
    static final String PATH_TIMETABLE_ENTRY_SHARE    = PATH_TIMETABLE_ENTRY  + "share/"    ;
    static final String PATH_TAG                      = PATH_MYSQL            + "tag/"      ;

    static final String PHP_GET = "get.php";
    static final String PHP_CREATE = "create.php";
    static final String PHP_DELETE = "delete.php";
    static final String PHP_UPDATE = "update.php";

    protected static final char BASETYPE                    = 0x3;
    protected static final char BASETYPE_NOTHING            = 0xFFFC;
    protected static final char BASETYPE_CREATE             = 0x1;
    protected static final char BASETYPE_UPDATE             = 0x2;
    protected static final char BASETYPE_DELETE             = 0x3;

    protected static final char TYPE                        = 0x7C;
    protected static final char SHARE                       = 0x4;

    protected static final char TYPE_CONTACT                = 0x08;
    protected static final char TYPE_CONTACT_GROUP          = 0x10;
    protected static final char TYPE_CONTACT_REQUEST        = 0x18;
    protected static final char TYPE_TASKLIST               = 0x20;
    protected static final char TYPE_TASKLIST_ENTRY         = 0x28;
    protected static final char TYPE_TIMETABLE              = 0x30;
    protected static final char TYPE_CATEGORY               = 0x38;
    protected static final char TYPE_TIMETABLE_ENTRY        = 0x40;
    protected static final char TYPE_TAG                    = 0x48;

    protected static final char TYPE_TASKLIST_SHARE         = SHARE | TYPE_TASKLIST;
    protected static final char TYPE_TIMETABLE_SHARE        = SHARE | TYPE_TIMETABLE;
    protected static final char TYPE_CATEGORY_SHARE         = SHARE | TYPE_CATEGORY;
    protected static final char TYPE_TIMETABLE_ENTRY_SHARE  = SHARE | TYPE_TIMETABLE_ENTRY;

    protected int id;

    protected int user_id;

    protected long update_time;

    protected MySQLEntry(int id, int user_id, long update_time) {
        this.id = id;
        this.user_id = user_id;
        this.update_time = update_time;
    }

    protected MySQLEntry(FryFile fry) {
        this(fry.readId(), fry.readId(), fry.readLong());
        if(isOffline()) {
            update();
        }
    }

    @Override
    @CallSuper
    public void writeTo(FryFile fry) {
        fry.writeId(id);
        fry.writeId(user_id);
        fry.writeLong(update_time);
    }

    protected void synchronize(MySQLEntry entry) {
        if(entry.update_time > update_time) {
            update_cancel();
            sync(entry);
        }else {
            update();
        }
    }

    protected abstract void remove();

    protected abstract void addData(MySQL mysql);

    public abstract boolean canEdit();

    public abstract int getShareId();

    protected abstract void sync(MySQLEntry entry);

    protected abstract char getType();

    protected boolean mysql() {
        if(isOffline()) {
            MySQL mysql = new MySQL(getPath(), PHP_CREATE);
            addData(mysql);
            FryFile fry = mysql.execute();

            if (fry == null) {
                return false;
            }

            user_id = User.getId();

            update_time = fry.readLong();
            id = fry.readId();
            return true;
        }else {
            MySQL mysql = new MySQL(getPath(), PHP_UPDATE);
            mysql.addId("id", id);
            addData(mysql);
            FryFile fry = mysql.execute();

            if (fry == null) {
                return false;
            }

            update_time = fry.readLong();
            return true;
        }
    }

    protected final boolean isOffline() {
        return (id == 0);
    }

    protected final boolean isOnline() {
        return (id != 0);
    }

    protected final void create() {
        ConnectionManager.add(this);
    }

    protected final void update() {
        ConnectionManager.add(this);
    }

    public final void delete() {
        Logger.Log("MySQL", "delete()");
        remove();
        if(isOnline()) {
            Delete.create(getType(), id);

        }else {
            ConnectionManager.entries.remove(this);
        }
    }

    public final void update_cancel() {
        ConnectionManager.remove(this);
    }

    public final boolean isOwner() {
        Logger.Log("MySQL", "isOwner()");
        return (user_id == User.getId() || user_id == 0);
    }

    public Date getTimeStamp() {
        return new Date(update_time);
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return user_id;
    }

    protected final String getPath() {
        switch(getType()) {

            case TYPE_CONTACT:
                return PATH_CONTACT;

            case TYPE_CONTACT_GROUP:
                return PATH_CONTACT_GROUP;

            case TYPE_CONTACT_REQUEST:
                return PATH_CONTACT_REQUEST;

            case TYPE_TASKLIST:
                return PATH_TASKLIST;

            case TYPE_TASKLIST_SHARE:
                return PATH_TASKLIST_SHARE;

            case TYPE_TASKLIST_ENTRY:
                return PATH_TASKLIST_ENTRY;

            case TYPE_TIMETABLE:
                return PATH_TIMETABLE;

            case TYPE_TIMETABLE_SHARE:
                return PATH_TIMETABLE_SHARE;

            case TYPE_CATEGORY:
                return PATH_CATEGORY;

            case TYPE_CATEGORY_SHARE:
                return PATH_CATEGORY_SHARE;

            case TYPE_TIMETABLE_ENTRY:
                return PATH_TIMETABLE_ENTRY;

            case TYPE_TIMETABLE_ENTRY_SHARE:
                return PATH_TIMETABLE_ENTRY_SHARE;

            case TYPE_TAG:
                return PATH_TAG;

            default:
                return null;
        }
    }

}
