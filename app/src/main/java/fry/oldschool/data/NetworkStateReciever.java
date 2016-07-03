package fry.oldschool.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import fry.oldschool.utils.App;

public class NetworkStateReciever extends BroadcastReceiver {

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
        /*
        if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            // no network connectivity
        }
        */
        App.hasInternetConnection = false;
    }

    public static void checkInternet() {
        if(!App.hasInternetConnection && !checkingForInternet) {
            checkingForInternet = true;
            (new CheckInternetConnection()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    protected static class CheckInternetConnection extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            App.hasInternetConnection = hasActiveInternetConnection();
            if(App.hasInternetConnection) {
                ConnectionManager.sync();
                if(App.isAppActive) {
                    Updater.start();
                }
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
                return false;
            }
        }
    }
}
