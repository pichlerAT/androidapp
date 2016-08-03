package com.frysoft.notifry.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.User;

public class NetworkStateReciever extends BroadcastReceiver {

    protected static boolean checkingForInternet = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.Log("NetworkStateReciever", "onReceive(Context,Intent)");
        if(intent.getExtras() != null) {

            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {

                checkInternet();
                return;
            }
        }
        App.hasInternetConnection = false;
    }

    public static void checkInternet() {
        Logger.Log("NetworkStateReciever", "checkInternet()");
        if(!App.hasInternetConnection && !checkingForInternet) {

            checkingForInternet = true;
            (new CheckInternetConnection()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    protected static class CheckInternetConnection extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            Logger.Log("NetworkStateReciever$CheckInternetConnection", "doInBackground(String...)");
            App.hasInternetConnection = hasActiveInternetConnection();

            if(App.hasInternetConnection) {
                if(!User.isLocal() && !User.isOnline()) {

                    User.logon();
                }

                if(User.isOnline()) {
                    ConnectionManager.sync();
                }

                if(App.isAppActive) {
                    Updater.start();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            Logger.Log("NetworkStateReciever$CheckInternetConnection", "onPostExecute(String)");
            checkingForInternet = false;
        }

        protected boolean hasActiveInternetConnection() {
            Logger.Log("NetworkStateReciever$CheckInternetConnection", "hasActiveInternetConnection()");
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
