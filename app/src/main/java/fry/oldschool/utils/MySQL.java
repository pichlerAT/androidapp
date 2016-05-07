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

import fry.oldschool.R;

public abstract class MySQL extends AsyncTask<String,String,String> {

    public static final String ADDRESS="http://212.183.67.41/Oldschool/";

    public static final int WAIT_FOR_CONNECTION_SLEEP = 2 * 60 * 1000;

    public static int USER_ID = 9;
    public static String USER_EMAIL = "pichler.edwin@rk.at";
    public static String USER_PASSWORD = "1234";

    protected static MySQLListener listener;

    protected boolean errorDialog=false;
    protected String errorTitle="ERROR";
    protected String errorCode="err_";

    public static void setListener(MySQLListener listener) {
        MySQL.listener = listener;
    }

    public static void synchronize() {
        (new Synchronize()).execute();
    }

    @Override
    protected void onPostExecute(String file_url) {
        if(errorDialog) {
            App.errorDialog(errorTitle,getErrorMessage(errorCode));
            if(listener != null) listener.mysql_finished("error");
        }else {
            if(listener != null) listener.mysql_finished(response());
        }
    }

    protected void error(String errorMessage) {
        this.errorCode = errorMessage;
        errorDialog = true;
    }

    protected boolean waitForConnection() {
        try {
            while(!connected()) {

                Thread.sleep(WAIT_FOR_CONNECTION_SLEEP);

            }
            System.out.println("---------- HAS CONNECTION !");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean connected() {
        try {
            URL url = new URL(ADDRESS + "test_connection.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();
            return (HttpURLConnection.HTTP_OK == con.getResponseCode());
        }catch (IOException ex) {
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
            //br.close();
            //con.disconnect();

            if(line.substring(0,3).equals("suc")) {
                return line.substring(3);
            }else {
                System.out.println("---------"+line);
                String line1;
                while((line1=br.readLine()) != null) {
                    System.out.println("---------"+line1);
                }
                error(line);
            }
            br.close();
            con.disconnect();
        }catch(IOException ex) {
            error("cannot connect to server");
            ex.printStackTrace();
        }
        return null;
    }

    protected String response() {
        try {
            return get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getErrorMessage(String errorCode) {
        switch(errorCode) {
            case "err_con0": return App.getContext().getResources().getString(R.string.err_con0);
            case "err_con1": return App.getContext().getResources().getString(R.string.err_con1);
            case "err_con2": return App.getContext().getResources().getString(R.string.err_con2);
            case "err_reg0": return App.getContext().getResources().getString(R.string.err_reg0);
            case "err_reg1": return App.getContext().getResources().getString(R.string.err_reg1);
            case "err_reg2": return App.getContext().getResources().getString(R.string.err_reg2);
            case "err_tc0": return App.getContext().getResources().getString(R.string.err_tc0);
            case "err_tc1": return App.getContext().getResources().getString(R.string.err_tc1);
            case "err_tu0": return App.getContext().getResources().getString(R.string.err_tu0);
            case "err_tu1": return App.getContext().getResources().getString(R.string.err_tu1);
            case "err_tg0": return App.getContext().getResources().getString(R.string.err_tg0);
            case "err_td0": return App.getContext().getResources().getString(R.string.err_td0);
            case "err_td1": return App.getContext().getResources().getString(R.string.err_td1);
            case "err_tec0": return App.getContext().getResources().getString(R.string.err_tec0);
            case "err_tec1": return App.getContext().getResources().getString(R.string.err_tec1);
            case "err_teu0": return App.getContext().getResources().getString(R.string.err_teu0);
            case "err_teu1": return App.getContext().getResources().getString(R.string.err_teu1);
            case "err_teg0": return App.getContext().getResources().getString(R.string.err_teg0);
            case "err_teg1": return App.getContext().getResources().getString(R.string.err_teg1);
            case "err_ted0": return App.getContext().getResources().getString(R.string.err_ted0);
            case "err_ted1": return App.getContext().getResources().getString(R.string.err_ted1);
            case "err_": return App.getContext().getResources().getString(R.string.err_);
            default: return ( App.getContext().getResources().getString(R.string.err) + errorCode );
        }
    }

    protected static class Synchronize extends MySQL {

        @Override
        protected String doInBackground(String... args) {

            if(!connected()) {
                waitForConnection();
            }

            sync_all();

            return "synchronize";
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
