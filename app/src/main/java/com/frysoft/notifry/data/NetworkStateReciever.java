package com.frysoft.notifry.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.frysoft.notifry.activity.MainActivity;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkStateReciever extends BroadcastReceiver {

    protected static boolean checkingForInternet = false;

    protected static LogonUser logonUser = new LogonUser();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.Log("NetworkStateReciever", "onReceive(Context,Intent)");
        if(App.isAppActive && intent.getExtras() != null) {

            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {

                checkInternet();

            }else {
                App.hasInternetConnection = false;
            }
        }
    }

    public static void checkInternet() {
        Logger.Log("NetworkStateReciever", "checkInternet()");
        if(!checkingForInternet) {

            if(!App.hasInternetConnection) {
                checkingForInternet = true;
                (new CheckInternetConnection()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }else {
                if(logonUser.getStatus() == AsyncTask.Status.PENDING) {
                    logonUser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }
    }

    protected static class CheckInternetConnection extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            Logger.Log("NetworkStateReciever$CheckInternetConnection", "doInBackground(String...)");
            App.hasInternetConnection = hasActiveInternetConnection();

            if(App.hasInternetConnection) {
                logonUser.doUserStuff();

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

    public static class LogonUser extends AsyncTask<String,String,String>  {

        @Override
        protected String doInBackground(String... params) {
            Logger.Log("NetworkStateReciever$LogonUser", "doInBackground(String...)");
            if(App.hasInternetConnection) {
                doUserStuff();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            Logger.Log("NetworkStateReciever$LogonUser", "onPostExecute(String)");
            logonUser = new LogonUser();
        }

        public void doUserStuff() {
            if(!User.isLocal() && !User.isOnline()) {
                if (User.logon()){
                    Intent intent = new Intent(App.getContext(), MainActivity.class);
                    App.getContext().startActivity(intent);
                }
            }

            if(User.isOnline()) {
                ConnectionManager.sync();
            }
        }

    }
}
