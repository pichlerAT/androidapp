package fry.oldschool.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MySQL {

    protected static final String IP_ADDRESS = "193.81.40.139" ;

    protected static final int PORT = 80;
    protected static final String ADDRESS="http://"+IP_ADDRESS+":"+PORT+"/android/";

    public static final String DIR_TASKLIST          = "tasklist/";
    public static final String DIR_TASKLIST_ENTRY    = DIR_TASKLIST + "entry/";
    public static final String DIR_TASKLIST_SHARE    = DIR_TASKLIST + "share/";
    public static final String DIR_CONTACT           = "contact/";
    public static final String DIR_CONTACT_GROUP     = DIR_CONTACT + "group/";
    public static final String DIR_CONTACT_REQUEST   = DIR_CONTACT + "request/";

    public static final String S = "" + (char)0;

    public static int USER_ID = 1;
    public static String USER_EMAIL = "fragner@gmx.net";
    public static String USER_PASSWORD = "1234";

    protected HttpURLConnection connection;

    protected OutputStreamWriter outputStreamWriter;

    protected BufferedReader connect(String addr,String data) throws IOException {
        URL url = new URL(ADDRESS + addr);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

        outputStreamWriter.write("user_id=" + USER_ID + "&password=" + USER_PASSWORD + data);
        outputStreamWriter.flush();
        connection.connect();
        return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    protected void disconnect() throws IOException {
        outputStreamWriter.close();
        connection.disconnect();
    }

    protected String getLine(String addr,String data) {
        try {
            BufferedReader br = connect(addr,data);
            String line=br.readLine();

            if(line.contains("<br")) {
                System.out.println(line);
                String newLine;
                while((newLine=br.readLine())!=null) {
                    System.out.println(newLine);
                }
            }

            br.close();
            disconnect();

            return line;

        } catch (IOException ex) {
            ex.printStackTrace();
            return "err_MySQL#getLine";
        }
    }

    protected ArrayList<String> getLines(String addr, String data) {
        try {
            BufferedReader br = connect(addr,data);
            ArrayList<String> lines = new ArrayList<>();

            String line;
            while((line = br.readLine()) != null) {
                lines.add(line);
            }

            br.close();
            disconnect();

            return lines;

        } catch (IOException ex) {
            ex.printStackTrace();
            ArrayList<String> l = new ArrayList<>();
            l.add("err_Mysql#getLines");
            return l;
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
