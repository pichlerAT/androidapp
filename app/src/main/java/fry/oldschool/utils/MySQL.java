package fry.oldschool.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public abstract class MySQL {

    public static final String S = "" + (char)0;

    public static final String IP_ADDRESS = "93.82.47.101" ;

    public static final int PORT = 80;

    public static final String DIRECTORY = "android";

    protected static final String ADDRESS="http://"+IP_ADDRESS+":"+PORT+"/"+DIRECTORY+"/";

    public static int USER_ID = 1;
    //public static String USER_EMAIL = "fragner@gmx.net";
    public static String USER_PASSWORD = "1234";

    protected HttpURLConnection connection;

    protected OutputStreamWriter outputStreamWriter;

    protected abstract boolean mysql();

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
}
