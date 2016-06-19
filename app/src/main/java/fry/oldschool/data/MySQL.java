package fry.oldschool.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MySQL {

    protected static final String IP_ADDRESS = "212.183.125.199" ;
    protected static final int PORT = 80;

    protected static final String ADDRESS="http://" + IP_ADDRESS + ":" + PORT + "/android/";

    public static final String DIR_TASKLIST          =                "tasklist/";
    public static final String DIR_TASKLIST_ENTRY    = DIR_TASKLIST + "entry/";
    public static final String DIR_TASKLIST_SHARE    = DIR_TASKLIST + "share/";
    public static final String DIR_CONTACT           =                "contact/";
    public static final String DIR_CONTACT_GROUP     = DIR_CONTACT +  "group/";
    public static final String DIR_CONTACT_REQUEST   = DIR_CONTACT +  "request/";

    public static final String S = "" + (char)0;

    public static int USER_ID = 1;
    public static String USER_EMAIL = "fragner@gmx.net";
    public static String USER_PASSWORD = "1234";

    protected String getLine(String addr,String data) {
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
            if(line.substring(0,4).equals("err_")) {
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

/*
    protected void error(String code) {
        (new Error()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected class Error extends AsyncTask<String,String,String> {

        protected String msg;

        @Override
        protected String doInBackground(String... args) {
            msg = args[0];
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            App.errorDialog("error",msg);
        }
    }
    */
}
