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

    public static final String IP_ADDRESS = "193.81.251.174" ;

    public static final String ADDRESS="http://"+IP_ADDRESS+"/Oldschool/";

    public static int USER_ID = 1;
    //public static String USER_EMAIL = "fragner@gmx.net";
    public static String USER_PASSWORD = "1234";

    protected abstract boolean mysql_update();

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

            return line;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected ArrayList<String> connect_list(String addr, String data) {
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
            ArrayList<String> lines = new ArrayList<>();

            String line;
            while((line = br.readLine()) != null) {
                lines.add(line);
            }

            br.close();
            con.disconnect();

            return lines;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected void error(String code) {
        (new Error()).execute(code);
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
