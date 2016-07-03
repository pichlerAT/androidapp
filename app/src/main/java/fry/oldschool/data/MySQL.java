package fry.oldschool.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class MySQL {

    protected static final String IP_ADDRESS = "62.47.228.187" ;
    protected static final int PORT = 80;

    protected static final String ADDRESS="http://" + IP_ADDRESS + ":" + PORT + "/android/";

    public static final String DIR_TASKLIST                 =                         "tasklist/"  ;
    public static final String DIR_TASKLIST_ENTRY           = DIR_TASKLIST          + "entry/"     ;
    public static final String DIR_TASKLIST_SHARE           = DIR_TASKLIST          + "share/"     ;
    public static final String DIR_CONTACT                  =                         "contact/"   ;
    public static final String DIR_CONTACT_GROUP            = DIR_CONTACT           + "group/"     ;
    public static final String DIR_CONTACT_REQUEST          = DIR_CONTACT           + "request/"   ;
    public static final String DIR_CALENDAR                 =                         "calendar/"  ;
    public static final String DIR_CALENDAR_SHARE           = DIR_CALENDAR          + "share/"     ;
    public static final String DIR_CALENDAR_CATEGORY        = DIR_CALENDAR          + "category/"  ;
    public static final String DIR_CALENDAR_CATEGORY_SHARE  = DIR_CALENDAR_CATEGORY + "share/"     ;
    public static final String DIR_CALENDAR_ENTRY           = DIR_CALENDAR          + "entry/"     ;
    public static final String DIR_CALENDAR_ENTRY_SHARE     = DIR_CALENDAR_ENTRY    + "share/"     ;

    public static final String S = "" + (char)0;

    public static int USER_ID = 1;
    public static String USER_EMAIL = "fragner@gmx.net";
    public static String USER_PASSWORD = "1234";


    public static final char BASETYPE           = 0x000F;

    public static final char BASETYPE_CREATE    = 0x0001;

    public static final char BASETYPE_UPDATE    = 0x0002;

    public static final char BASETYPE_DELETE    = 0x0004;

    public static final char BASETYPE_SHARE     = 0x0008;



    public static final char TYPE                   = 0xFFF0;

    public static final char TYPE_CONTACT           = 0x0010;

    public static final char TYPE_CONTACT_GROUP     = 0x0020;

    public static final char TYPE_CONTACT_REQUEST   = 0x0040;

    public static final char TYPE_TASKLIST          = 0x0080;

    public static final char TYPE_TASKLIST_ENTRY    = 0x0100;

    public static final char TYPE_CALENDAR          = 0x0200;

    public static final char TYPE_CALENDAR_CATEGORY = 0x0400;

    public static final char TYPE_CALENDAR_ENTRY    = 0x0800;



    protected static String getLine(String addr,String data) {
        try {
            URL url = new URL(ADDRESS + addr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());

            os.write("user_id=" + USER_ID + "&password=" + USER_PASSWORD + data);
            os.flush();
            con.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
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
    }



    protected char type;

    protected int id;

    protected int user_id;

    protected MySQL(char type, int id, int user_id) {
        this.type = type;
        this.id = id;
        this.user_id = user_id;
    }

    @Override
    public abstract boolean equals(Object o);

    public abstract Object backup();

    protected abstract boolean mysql();

    protected abstract void synchronize(MySQL mysql);

    public abstract boolean canEdit();

    public boolean isOwner() {
        return (user_id == USER_ID);
    }

    public char getType() {
        return (char)(type & TYPE);
    }

}
