package fry.oldschool.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkStateReciever extends BroadcastReceiver {

    protected static boolean hasInternetConnection = false;

    protected static boolean checkingForInternet = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        // network connectivity change
        if(intent.getExtras() != null) {
            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                // network ni.getTypeName() connected
                checkInternet();
                return;
            }
        }
        if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            // no network connectivity
        }
        hasInternetConnection = false;
    }

    public static void checkInternet() {
        if(!hasInternetConnection && !checkingForInternet) {
            checkingForInternet = true;
            (new CheckInternetConnection()).execute();
            System.out.println("----- NetworkStateReciever#checkInternet");
        }
    }

    protected static class CheckInternetConnection extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            hasInternetConnection = hasActiveInternetConnection();
            if(hasInternetConnection) {
                App.conMan.sync();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            checkingForInternet = false;
        }

        protected boolean hasActiveInternetConnection() {
            try{
                URL url = new URL("http://www.google.com");
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestProperty("User-Agent","Test");
                con.setRequestProperty("Connection","close");
                con.setConnectTimeout(1500);
                con.connect();
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            }catch (IOException ex) {
                //ex.printStackTrace();
                return false;
            }
        }
    }
}