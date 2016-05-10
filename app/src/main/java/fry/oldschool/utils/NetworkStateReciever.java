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

    @Override
    public void onReceive(Context context, Intent intent) {
        // network connectivity change
        if(intent.getExtras() != null) {
            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                // network ni.getTypeName() connected
                (new CheckInternetConnection()).execute();
                return;
            }
        }
        if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            // no network connectivity
        }
        hasInternetConnection = false;
    }

    public static class CheckInternetConnection extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            boolean internet = hasActiveInternetConnection();
            if(internet) {
                MySQL.sync();
            }
            hasInternetConnection = internet;
            return null;
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
