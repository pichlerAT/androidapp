package com.frysoft.notifry.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Fryable;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.User;

public abstract class MySQL implements Fryable {

    public static final String ADDRESS = "http://www.notifry.com/android/";

    public static final String DIR_TASKLIST                 =                         "tasklist/" ;
    public static final String DIR_TASKLIST_ENTRY           = DIR_TASKLIST          + "entry/"    ;
    public static final String DIR_TASKLIST_SHARE           = DIR_TASKLIST          + "share/"    ;
    public static final String DIR_CONTACT                  =                         "contact/"  ;
    public static final String DIR_CONTACT_GROUP            = DIR_CONTACT           + "group/"    ;
    public static final String DIR_CONTACT_REQUEST          = DIR_CONTACT           + "request/"  ;
    public static final String DIR_CALENDAR                 =                         "calendar/" ;
    public static final String DIR_CALENDAR_SHARE           = DIR_CALENDAR          + "share/"    ;
    public static final String DIR_CALENDAR_CATEGORY        = DIR_CALENDAR          + "category/" ;
    public static final String DIR_CALENDAR_CATEGORY_SHARE  = DIR_CALENDAR_CATEGORY + "share/"    ;
    public static final String DIR_CALENDAR_ENTRY           = DIR_CALENDAR          + "entry/"    ;
    public static final String DIR_CALENDAR_ENTRY_SHARE     = DIR_CALENDAR_ENTRY    + "share/"    ;

    public static final String S = "" + (char)0;

    //public static int USER_ID = 1;
    //public static String USER_EMAIL = "stefan.fragner@rk.at";
    //public static String USER_PASSWORD = "1234";


    public static final char BASETYPE           = 0x000F;

    public static final char BASETYPE_CREATE    = 0x0001;

    public static final char BASETYPE_UPDATE    = 0x0002;

    public static final char BASETYPE_DELETE    = 0x0004;



    public static final char TYPE                   = 0xFFF0;

    public static final char TYPE_CONTACT           = 0x0010;

    public static final char TYPE_CONTACT_GROUP     = 0x0020;

    public static final char TYPE_CONTACT_REQUEST   = 0x0040;

    public static final char TYPE_TASKLIST          = 0x0080;

    public static final char TYPE_TASKLIST_ENTRY    = 0x0100;

    public static final char TYPE_CALENDAR          = 0x0200;

    public static final char TYPE_CALENDAR_CATEGORY = 0x0400;

    public static final char TYPE_CALENDAR_ENTRY    = 0x0800;

    public static String execute(String addr, String data) {
        Logger.Log("MySQL", "execute(String,String)");
        try {
            URL url = new URL(addr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());

            os.write(data);
            os.flush();
            con.connect();

            BufferedReader br;
            try {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }catch(FileNotFoundException ex) {
                ex.printStackTrace();
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                System.out.println("------------------------");
                System.out.println("addr = "+addr);
                System.out.println("------------------------");
                String line;
                while((line=br.readLine())!=null) {
                    System.out.println(line);
                    System.out.println("------------------------");
                }
                return null;
            }
            String line=br.readLine();

            if(line == null) {
                return null;
            }

            if(line.contains("<br")) {
                System.out.println(line);
                String newLine;
                while((newLine=br.readLine())!=null) {
                    System.out.println(newLine);
                }
                return null;
            }

            br.close();
            os.close();
            con.disconnect();

            return line;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String executeAndroid(String addr, String data) {
        Logger.Log("MySQL", "executeAndroid(String,String)");
        String resp = execute(ADDRESS + addr, "user_id=" + User.getId() + "&password=" + User.getPassword() + data);

        if(resp != null && resp.length()>3 && resp.substring(0,4).equals("err_")) {
            return null;
        }

        return resp;
        /*
        try {
            URL url = new URL(ADDRESS + addr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());

            os.write("user_id=" + USER_ID + "&password=" + USER_PASSWORD + data);
            os.flush();
            con.connect();

            BufferedReader br;
            try {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }catch(FileNotFoundException ex) {
                ex.printStackTrace();
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                System.out.println("------------------------");
                System.out.println("addr = "+addr);
                System.out.println("------------------------");
                String line;
                while((line=br.readLine())!=null) {
                    System.out.println(line);
                    System.out.println("------------------------");
                }
                return null;
            }
            String line=br.readLine();

            if(line == null) {
                return "";
            }
            if(line.length()>3 && line.substring(0,4).equals("err_")) {
                return null;
            }
            if(line.contains("<br")) {
                System.out.println(line);
                String newLine;
                while((newLine=br.readLine())!=null) {
                    System.out.println(newLine);
                }
                return null;
            }

            br.close();
            os.close();
            con.disconnect();

            return line;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        */
    }



    protected char type;

    public int id;

    protected int user_id;

    protected MySQL(char type, int id, int user_id) {
        Logger.Log("MySQL", "MySQL(char,int,int)");
        this.type = type;
        this.id = id;
        this.user_id = user_id;
    }

    protected MySQL(FryFile fry) {
        this(fry.getChar(), fry.getInt(), fry.getInt());
        Logger.Log("MySQL", "MySQL(FryFile)");
    }

    @Override
    public void writeTo(FryFile fry) {
        Logger.Log("MySQL", "writeTo(FryFile)");
        fry.write(type);
        fry.write(id);
        fry.write(user_id);
    }

    protected abstract boolean mysql_create();

    protected abstract boolean mysql_update();

    protected abstract boolean mysql_delete();

    protected final boolean mysql() {
        Logger.Log("MySQL", "mysql()");

        if((type & BASETYPE_CREATE) > 0) {
            if(mysql_create()) {
                type = (char)(type & TYPE);
                return true;
            }
            return false;

        }else if((type & BASETYPE_UPDATE) > 0) {
            if(mysql_update()) {
                type = (char)(type & TYPE);
                return true;
            }
            return false;

        }else if((type & BASETYPE_DELETE) > 0) {
            if(mysql_delete()) {
                type = (char)(type & TYPE);
                return true;
            }
            return false;

        }

        return true;
    }

    protected final void create() {
        Logger.Log("MySQL", "create()");
        type = (char)((type & TYPE) | BASETYPE_CREATE);
        ConnectionManager.add(this);
    }

    protected final void update() {
        Logger.Log("MySQL", "update()");
        if(id != 0) {
            type = (char)((type & TYPE) | BASETYPE_UPDATE);
            ConnectionManager.add(this);
        }
    }

    public void delete() {
        Logger.Log("MySQL", "delete()");
        if(id != 0) {
            type = (char)((type & TYPE) | BASETYPE_DELETE);
            ConnectionManager.add(this);
        }
    }

    public final boolean isOwner() {
        Logger.Log("MySQL", "isOwner()");
        return (user_id == User.getId());
    }

    public final char getBaseType() {
        Logger.Log("MySQL", "getBaseType()");
        return (char)(type & BASETYPE);
    }

    public final char getType() {
        Logger.Log("MySQL", "getType()");
        return (char) (type & TYPE);
    }

    public int getUserID(){
        return this.user_id;
    }

}
