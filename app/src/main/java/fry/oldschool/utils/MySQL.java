package fry.oldschool.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public abstract class MySQL extends AsyncTask<String,String,String> {

    public static final String ADDRESS="http://62.46.11.178/Oldschool/";

    public static int USER_ID = 9;
    public static String USER_EMAIL = "pichler.edwin@rk.at";
    public static String USER_PASSWORD = "1234";

    protected static MySQLListener listener;

    protected boolean errorDialog=false;
    protected String errorTitle="ERROR";
    protected String errorMessage="";

    public static void setListener(MySQLListener listener) {
        MySQL.listener = listener;
    }

    public static void synchronize(String arg) {
        (new Synchronize()).execute(arg);
    }

    @Override
    protected void onPostExecute(String file_url) {
        if(errorDialog) {
            App.errorDialog(errorTitle,errorMessage);
            if(listener != null) listener.mysql_finished("error");
        }else {
            if(listener != null) listener.mysql_finished(response());
        }
    }

    protected void error(String errorMessage) {
        this.errorMessage = errorMessage;
        errorDialog = true;
    }

    protected boolean hasInternet() {
        try{
            return !InetAddress.getByName("google.com").equals("");
        }catch(Exception ex) {
            return false;
        }
    }

    protected String connect(String address,String post) {
        try {
            URL url = new URL(ADDRESS + address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
            os.write("user_id=" + USER_ID + "&password=" + USER_PASSWORD + post);
            os.flush();
            os.close();
            con.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line=br.readLine();
            br.close();
            con.disconnect();

            if(line.substring(0,3).equals("suc")) {
                return line.substring(3);
            }else {
                error(line);
            }
        }catch(IOException ex) {
            error("cannot connect to server");
            ex.printStackTrace();
        }
        return null;
    }

    protected String response() {
        try {
            return get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getErrorMessage(String errorCode) {
        switch(errorCode) {
            case "err_con0":
            case "err_con1":
            case "err_con2":
            case "err_tc0":
            case "err_tc1":
            case "err_tu0":
            case "err_tu1":
            case "err_tg0":
            case "err_td0":
            case "err_td1":
            case "err_tec0":
            case "err_tec1":
            case "err_teu0":
            case "err_teu1":
            case "err_teg0":
            case "err_teg1":
            case "err_ted0":
            case "err_ted1":
        }
        return null;
    }

    protected static class Synchronize extends MySQL {

        @Override
        protected String doInBackground(String... args) {
            switch(args[0]) {
                case "all": sync_all(); break;
                case "tdl": sync_tdl(); break;
                default: return "Unknown Command: sync_"+args[0];
            }
            return "sync_"+args[0];
        }

        protected void sync_all() {
            sync_tdl();
        }

        protected void sync_tdl() {
            for(ToDoList tdl : ToDoList.ToDoLists) {
                tdl.update();
            }
        }
    }
}
