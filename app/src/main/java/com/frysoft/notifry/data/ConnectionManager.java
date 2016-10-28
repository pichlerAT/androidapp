package com.frysoft.notifry.data;

import android.os.AsyncTask;

import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;

import java.util.ArrayList;

public class ConnectionManager {

    public static long last_update = 0;

    private static boolean ready = false;

    protected static boolean PERFORM_UPDATE = false;

    protected static boolean SYNC_ANDROID = false;

    protected static Sync syncTask = new Sync();

    protected static MySQLListener mysql_listener;

    protected static ArrayList<MySQLEntry> entries = new ArrayList<>();

    public static void setMySQLListener(MySQLListener listener) {
        Logger.Log("ConnectionManager", "setMySQLListener(MySQLListener)");
        mysql_listener = listener;
    }

    protected static void setReady(boolean ready) {
        ConnectionManager.ready = ready;
        if(ready) {
            sync();
        }
    }

    public static void add(MySQLEntry entry) {
        Logger.Log("ConnectionManager", "add(MySQL)");
        if(entry instanceof Category) {
            entries.add(0, entry);

        }else {
            entries.add(entry);
        }
        sync();
    }

    protected static void remove(MySQLEntry entry) {
        entries.remove(entry);
    }

    protected static void notifyMySQLListener() {
        Logger.Log("ConnectionManager", "notifyMySQLListener()");
        (new NotifyListener()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected static void synchronizeAndroidCalendar() {
        PERFORM_UPDATE = true;
        SYNC_ANDROID = true;
        sync();
    }

    public static void performUpdate() {
        Logger.Log("ConnectionManager", "performUpdate()");
        PERFORM_UPDATE = true;
        sync();
    }

    public static boolean hasEntry(MySQLEntry entry, char type) {
        Class clazz  = entry.getClass();
        for(MySQLEntry ent : entries) {
            if(ent == entry || (ent.id == entry.id && ent.getClass().equals(clazz))) {
                return true;
            }
        }
        return false;
    }

    protected static void sync() {
        Logger.Log("ConnectionManager", "sync()");
        if(ready && User.isOnline()) {
            if (syncTask.getStatus() == AsyncTask.Status.PENDING && App.hasInternetConnection) {
                syncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                NetworkStateReciever.checkInternet();
            }
        }else if(!App.hasInternetConnection) {
            NetworkStateReciever.checkInternet();
        }
    }

    protected static void writeTo(FryFile fry) {
        int length = 0;
        char[] types = new char[entries.size()];
        int[] ids = new int[entries.size()];

        for(MySQLEntry ent : entries) {
            if(ent instanceof Delete) {
                types[length] = ent.getType();
                ids[length++] = ent.id;
            }
        }

        fry.writeArrayLength(length);
        for(int i=0; i<length; ++i) {
            fry.writeChar(types[i]);
            fry.writeId(ids[i]);
        }
    }

    protected static void readFrom(FryFile fry) {
        int length = fry.readArrayLength();
        for(int i=0; i<length; ++i) {
            Delete.create(fry.readChar(), fry.readId());
        }
    }

    private static boolean synchronizeAll(FryFile fry) {
        if(fry.size() < 8) {
            return false;
        }

        //ContactList.synchronizeContactsFromMySQL(fry);
        //ContactList.synchronizeContactGroupsFromMySQL(fry);
        //ContactList.synchronizeContactRequestsFromMySQL(fry);
        ContactList.synchronizeFromMySQL(fry);
        Data.synchronizeFromMySQL(fry);
        return true;
    }

    protected static class NotifyListener extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... args) {
            Logger.Log("ConnectionManager$NotifyListener", "doInBackground(String...)");
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            Logger.Log("ConnectionManager$NotifyListener", "onPostExecute(String)");
            if(mysql_listener != null) {
                mysql_listener.mysql_finished(/*message*/);
            }
        }
    }

    protected static class Sync extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            Logger.Log("ConnectionManager$Sync", "doInBackground(String...)");
            if(PERFORM_UPDATE) {
                PERFORM_UPDATE = !sync_all();
                if(SYNC_ANDROID && !PERFORM_UPDATE) {
                    Data.synchronizeAndroidCalendar();
                }
            }

            int index = 0;
            while(index < entries.size()) {
                if(entries.get(index).mysql()) {
                    entries.remove(index);
                    notifyMySQLListener();
                }else {
                    ++index;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Logger.Log("ConnectionManager$Sync", "onPostExecute(String)");
            syncTask = new Sync();
        }

        protected boolean sync_all() {
            Logger.Log("ConnectionManager$Sync", "sync_all()");
            FryFile fry = (new MySQL(MySQLEntry.PATH_MYSQL, MySQLEntry.PHP_GET)).execute();
            if(fry != null) {
                long update_time = fry.readLong();
                if(synchronizeAll(fry)) {
                    last_update = update_time;
                    return true;
                }
            }
            return false;
        }
    }

}