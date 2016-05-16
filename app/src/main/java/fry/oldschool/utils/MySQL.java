package fry.oldschool.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class MySQL {

    public static final String IP_ADDRESS = "91.114.246.54" ;

    public static final String ADDRESS="http://"+IP_ADDRESS+"/Oldschool/";

    public static final String SEP_0 = ";" ;
    public static final String SEP_1 = "," ;

    public static int USER_ID = 1;
    public static String USER_EMAIL = "fragner@gmx.net";
    public static String USER_PASSWORD = "Marmor";

    protected String connect(String addr,String data) {
        try {
            URL url = new URL(ADDRESS + addr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());

            os.write("user_id=" + USER_ID + "&password=" + USER_PASSWORD + data);
            os.flush();
            os.close();
            con.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line=br.readLine();
            br.close();
            con.disconnect();

            if(line.substring(0,3).equals("suc")) {
                return line.substring(3);
            }
            return null;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected String getString() {
        return ( "" + getType() );
    }

    protected abstract byte getType();

    protected abstract boolean mysql_update();

}
