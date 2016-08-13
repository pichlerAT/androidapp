package com.frysoft.notifry.data;

import android.os.AsyncTask;

import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;

import java.util.ArrayList;

public class ConnectionManager {

    protected static boolean PERFORM_UPDATE = false;

    protected static Sync syncTask = new Sync();

    protected static MySQLListener mysql_listener;

    protected static ArrayList<MySQL> entries = new ArrayList<>();

    public static void setMySQLListener(MySQLListener listener) {
        Logger.Log("ConnectionManager", "setMySQLListener(MySQLListener)");
        mysql_listener = listener;
    }

    public static void add(MySQL entry) {
        Logger.Log("ConnectionManager", "add(MySQL)");
        /*
        if(entry.isOffline() || !hasEntry(entry)) {
            entries.add(entry);
            sync();
        }
        */
        if(entry instanceof Category) {
            entries.add(0, entry);
        }
        entries.add(entry);
        sync();
    }

    protected static void notifyMySQLListener() {
        Logger.Log("ConnectionManager", "notifyMySQLListener()");
        (new NotifyListener()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void performUpdate() {
        Logger.Log("ConnectionManager", "performUpdate()");
        PERFORM_UPDATE = true;
        sync();
    }

    public static boolean hasEntry(MySQL entry, char type) {
        for(MySQL ent : entries) {
            if(ent == entry || (ent.id == entry.id && ent.type == type)) {
                return true;
            }
        }
        return false;
    }
/*
    public static boolean hasEntry(MySQL entry) {
        return hasEntry(entry, entry.type);
    }
*/
    /*
    protected static boolean remove(MySQL entry) {
        Logger.Log("ConnectionManager", "remove(MySQL)");
        return entries.remove(entry);
    }

    protected static void remove(char type,int id) {
        Logger.Log("ConnectionManager", "remove(char,int)");
        for(int i=0; i<entries.size(); ++i) {
            MySQL ent = entries.get(i);
            if(ent.id == id && ent.type == type) {
                entries.remove(i);
            }
        }
    }
*/
    protected static void sync() {
        Logger.Log("ConnectionManager", "sync()");
        if(User.isOnline()) {
            if (syncTask.getStatus() == AsyncTask.Status.PENDING && App.hasInternetConnection) {
                syncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                NetworkStateReciever.checkInternet();
            }
        }else if(!App.hasInternetConnection) {
            NetworkStateReciever.checkInternet();
        }
    }
/*
    public static void writeTo(FryFile fry) {
        Logger.Log("ConnectionManager", "writeTo(FryFile)");
        ArrayList<MySQL> writeList = new ArrayList<>(entries.size());
        for(MySQL m : entries) {
            if(m instanceof MySQLEntry && m.isOnline()) {
                writeList.add(m);
            }
        }
        fry.writeArrayLength(writeList.size());
        for(MySQL m : writeList) {
            fry.writeChar(m.type);
            fry.writeUnsignedInt(m.id);
        }
    }
/*
    public static void readFrom(FryFile fry) {
        Logger.Log("ConnectionManager", "readFrom(FryFile)");
        entries = new ArrayList<>();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            MySQL entry = MySQLEntry.load(fry.getChar(),fry.getInt());
            if(entry != null) {
                entries.add(entry);
            }
        }
    }
*/
    public static String[] split(String str) {
        String buffer = "";
        ArrayList<String> list = new ArrayList<>();

        for(int k=0; k<str.length(); ++k) {
            char c = str.charAt(k);

            if(c == 0) {
                if(buffer.length() == 0) {
                    buffer += c;

                }else {
                    list.add(buffer);
                    buffer = "";
                }
            }else {
                buffer += c;
            }
        }

        return  list.toArray(new String[list.size()]);
    }

    public static boolean synchronizeAll(String resp) {
        FryFile fry = new FryFile.Split((char)0);
        fry.loadFromString(resp);
        if(fry.size() < 8) {
            return false;
        }

        ContactList.synchronizeContactsFromMySQL(fry);
        ContactList.synchronizeContactGroupsFromMySQL(fry);
        ContactList.synchronizeContactRequestsFromMySQL(fry);
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
            String resp = MySQL.executeMySQL("get.php","");
            if(resp == null) {
                return false;
            }
            return synchronizeAll(resp);
        }
    }

}