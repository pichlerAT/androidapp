package com.frysoft.notifry.data;

import android.support.annotation.Nullable;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class MySQL implements Fryable {

    private static final String ADDRESS = "http://www.notifry.com/notifry/";
    private static final String ADR_MYSQL            = ADDRESS               + "mysql/"    ;

    static final String ADR_REGISTER                 = ADDRESS               + "register/" ;
    static final String ADR_USER                     = ADDRESS               + "user/"     ;
    static final String ADR_USER_CHANGE              = ADR_USER              + "change/"   ;

    static final String DIR_TASKLIST                 =                         "tasklist/" ;
    static final String DIR_TASKLIST_ENTRY           = DIR_TASKLIST          + "entry/"    ;
    static final String DIR_TASKLIST_SHARE           = DIR_TASKLIST          + "share/"    ;
    static final String DIR_CONTACT                  =                         "contact/"  ;
    static final String DIR_CONTACT_GROUP            = DIR_CONTACT           + "group/"    ;
    static final String DIR_CONTACT_REQUEST          = DIR_CONTACT           + "request/"  ;
    static final String DIR_CALENDAR                 =                         "calendar/" ;
    static final String DIR_CALENDAR_SHARE           = DIR_CALENDAR          + "share/"    ;
    static final String DIR_CATEGORY                 =                         "category/" ;
    static final String DIR_CATEGORY_SHARE           = DIR_CATEGORY          + "share/"    ;
    static final String DIR_CALENDAR_ENTRY           = DIR_CALENDAR          + "entry/"    ;
    static final String DIR_CALENDAR_ENTRY_SHARE     = DIR_CALENDAR_ENTRY    + "share/"    ;
    static final String DIR_TAG                      =                         "tag/"      ;

    public static final String S = "" + (char)0;

    private static String UserIdAndPassword = "";



    protected static final byte BASETYPE_NOTHING   = 0;

    protected static final byte BASETYPE_CREATE    = 1;

    protected static final byte BASETYPE_UPDATE    = 2;

    protected static final byte BASETYPE_DELETE    = 3;



    static final byte TYPE_CONTACT              = 1;

    static final byte TYPE_CONTACT_GROUP        = 2;

    static final byte TYPE_CONTACT_REQUEST      = 3;

    static final byte TYPE_TASKLIST             = 4;

    static final byte TYPE_TASKLIST_ENTRY       = 5;

    static final byte TYPE_TASKLIST_SHARE       = 6;

    static final byte TYPE_CALENDAR             = 7;

    static final byte TYPE_CALENDAR_SHARE       = 8;

    static final byte TYPE_CATEGORY             = 9;

    static final byte TYPE_CATEGORY_SHARE       = 10;

    static final byte TYPE_CALENDAR_ENTRY       = 11;

    static final byte TYPE_CALENDAR_ENTRY_SHARE = 12;

    static final byte TYPE_TAG                  = 13;

    @Nullable
    public static FryFile execute(String addr, String data) {
        Logger.Log("MySQL", "execute(String,String)");
        try {
            URL url = new URL(addr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //con.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

            //if(!data.isEmpty()) {

                //con.setRequestMethod("POST");
                con.setDoOutput(true);
                OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
                os.write(data);
                os.flush();
                //os.close();
            //}

            con.connect();

            FryFile fry = new FryFile.Split("" + (char)0);
            fry.loadFromStream(con.getInputStream());

            int code = con.getResponseCode();
            if(code != HttpURLConnection.HTTP_OK) {
                System.out.println("------------------------");
                System.out.println(con.getResponseCode());
                System.out.println("addr = " + addr);
                System.out.println("------------------------");
                InputStream is = con.getErrorStream();
                if (is != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        System.out.println("------------------------");
                    }
                }
                return null;
            }

            os.close();
            con.disconnect();

            return fry;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static FryFile executeMySQL(String addr, String data) {
        Logger.Log("MySQL", "executeMySQL(String,String)");
        FryFile fry = execute(ADR_MYSQL + addr, UserIdAndPassword + data);

        if(fry == null) {
            return null;
        }

        String resp = fry.asdf();
        if(resp == null) {
            return null;
        }

        if(resp.length()>3 && resp.substring(0,4).equals("err_")) {
            System.out.println("# MYSQLI - ERROR: " + fry.getString());
            FryFile f = new FryFile.Split("\n");
            f.loadFromString(fry.getString());
            while(f.hasNext()) {
                System.out.println("# " + f.getString());
            }
            return null;
        }

        if(resp.contains("<br")) {
            String newLine;
            while((newLine = fry.getString())!=null) {
                System.out.println("# fry: " + newLine);
            }
            return null;
        }

        return fry;
    }

    public static void setLoginData(String email, String password) {
        UserIdAndPassword = "email=" + email + "&password=" + password;
    }

    public static short signed(byte b) {
        return (short)(b + (b < 0 ? 256 : 0));
    }

    public static int signed(short s) {
        return (s + (s < 0 ? 65536 : 0));
    }

    public static long signed(int i) {
        return (i + (i < 0 ? 4294967296L : 0L));
    }



    private byte type;

    protected int id;

    protected int user_id;

    protected MySQL(int id, int user_id) {
        Logger.Log("MySQL", "MySQL(char,int,int)");
        type = BASETYPE_NOTHING;
        this.id = id;
        this.user_id = user_id;
    }

    protected MySQL(FryFile fry) {
        this(fry.getUnsignedInt(), fry.getUnsignedInt());
        Logger.Log("MySQL", "MySQL(FryFile,boolean)");

        if(user_id == 0) {
            create();
        }
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("MySQL", "writeTo(FryFile)");
        fry.writeUnsignedInt(id);
        fry.writeUnsignedInt(user_id);
    }

    protected abstract void remove();

    protected abstract boolean mysql_create();

    protected abstract boolean mysql_update();

    protected abstract byte getType();

    protected abstract String getPath();

    protected final boolean mysql() {
        Logger.Log("MySQL", "mysql()");

        if((type & BASETYPE_CREATE) > 0) {
            if(mysql_create()) {
                type = BASETYPE_NOTHING;
                user_id = User.getId();
                return true;
            }
            return false;

        }else if((type & BASETYPE_UPDATE) > 0) {
            if(mysql_update()) {
                type = BASETYPE_NOTHING;
                return true;
            }
            return false;

        }

        return true;
    }

    protected byte getBaseType() {
        return type;
    }

    protected boolean isOffline() {
        return (user_id == 0);
    }

    protected boolean isOnline() {
        return (user_id != 0);
    }

    protected final void create() {
        Logger.Log("MySQL", "create()");
        type = BASETYPE_CREATE;
        ConnectionManager.add(this);
    }

    protected final void update() {
        Logger.Log("MySQL", "update()");
        if(isOnline()) {
            type = BASETYPE_UPDATE;
            ConnectionManager.add(this);
        }
    }

    public final void delete() {
        Logger.Log("MySQL", "delete()");
        remove();
        if(isOnline()) {
            Delete.create(getType(), id);
        }
    }

    public final boolean isOwner() {
        Logger.Log("MySQL", "isOwner()");
        return (user_id == User.getId() || user_id == 0);
    }

    public int getUserID(){
        return this.user_id;
    }

    protected String getPath(byte type) {
        switch(type) {

            case TYPE_CONTACT:
                return DIR_CONTACT;

            case TYPE_CONTACT_GROUP:
                return DIR_CONTACT_GROUP;

            case TYPE_CONTACT_REQUEST:
                return DIR_CONTACT_REQUEST;

            case TYPE_TASKLIST:
                return DIR_TASKLIST;

            case TYPE_TASKLIST_SHARE:
                return DIR_TASKLIST_SHARE;

            case TYPE_TASKLIST_ENTRY:
                return DIR_TASKLIST_ENTRY;

            case TYPE_CALENDAR:
                return DIR_CALENDAR;

            case TYPE_CALENDAR_SHARE:
                return DIR_CALENDAR_SHARE;

            case TYPE_CATEGORY:
                return DIR_CATEGORY;

            case TYPE_CATEGORY_SHARE:
                return DIR_CATEGORY_SHARE;

            case TYPE_CALENDAR_ENTRY:
                return DIR_CALENDAR_ENTRY;

            case TYPE_CALENDAR_ENTRY_SHARE:
                return DIR_CALENDAR_ENTRY_SHARE;

            case TYPE_TAG:
                return DIR_TAG;

            default:
                return null;
        }
    }

    protected static MySQL getMySQL(byte type, int id) {
        switch(type) {

            case TYPE_CONTACT_GROUP:
                return ContactList.getContactGroupById(id);

            case TYPE_TASKLIST:
                return Data.Tasklists.getById(id);

            case TYPE_TASKLIST_SHARE:
                return Data.getTasklistShareById(id);

            case TYPE_TASKLIST_ENTRY:
                return Data.getTasklistEntryById(id);

            case TYPE_CALENDAR_SHARE:
                return Data.getTimetableShareById(id);

            case TYPE_CATEGORY:
                return Data.Categories.getById(id);

            case TYPE_CATEGORY_SHARE:
                return Data.getCategoryShareById(id);

            case TYPE_CALENDAR_ENTRY:
                return Data.Timetable.Entries.getById(id);

            case TYPE_CALENDAR_ENTRY_SHARE:
                return Data.getTimetableEntryShareById(id);

            case TYPE_TAG:
                return Data.Tags.getById(id);

            default:
                return null;

        }
    }

}
