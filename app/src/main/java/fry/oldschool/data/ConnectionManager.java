package fry.oldschool.data;

import android.os.AsyncTask;

import java.util.ArrayList;

import fry.oldschool.utils.App;
import fry.oldschool.utils.FryFile;
import fry.oldschool.utils.Logger;

public class ConnectionManager {

    protected static boolean PERFORM_UPDATE = true;

    protected static boolean sync_contact = false;

    protected static boolean sync_requests = false;

    protected static boolean sync_calendar = false;

    protected static boolean sync_tasklist = false;

    protected static Sync syncTask = new Sync();

    protected static MySQLListener mysql_listener;

    protected static ArrayList<MySQL> entries = new ArrayList<>();

    public static void setMySQLListener(MySQLListener listener) {
        Logger.Log("ConnectionManager", "setMySQLListener(MySQLListener)");
        mysql_listener = listener;
    }

    protected static void add(MySQL entry) {
        Logger.Log("ConnectionManager", "add(MySQL)");
        if(entry.id == 0 || !hasEntry(entry.type, entry.id)) {
            entries.add(entry);
            sync();
        }
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

    public static boolean hasEntry(char type,int id) {
        Logger.Log("ConnectionManager", "hasEntry(char,int)");
        for(int i=0; i<entries.size(); ++i) {
            MySQL ent = entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                return true;
            }
        }
        return false;
    }

    protected static boolean remove(MySQL entry) {
        Logger.Log("ConnectionManager", "remove(MySQL)");
        return entries.remove(entry);
    }

    protected static void remove(char type,int id) {
        Logger.Log("ConnectionManager", "remove(char,int)");
        for(int i=0; i<entries.size(); ++i) {
            MySQL ent = entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                entries.remove(i);
            }
        }
    }

    protected static void sync() {
        Logger.Log("ConnectionManager", "sync()");
        if(syncTask.getStatus() == AsyncTask.Status.PENDING && App.hasInternetConnection) {
            syncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            NetworkStateReciever.checkInternet();
        }
    }

    public static void writeTo(FryFile fry) {
        Logger.Log("ConnectionManager", "writeTo(FryFile)");
        ArrayList<MySQL> writeList = new ArrayList<>(entries.size());
        for(MySQL m : entries) {
            if(m instanceof MySQLEntry && m.id > 0) {
                writeList.add(m);
            }
        }
        fry.write((char)writeList.size());
        for(MySQL m : writeList) {
            fry.write(m.type);
            fry.write(m.id);
        }
    }

    public static void readFrom(FryFile fry) {
        Logger.Log("ConnectionManager", "readFrom(FryFile)");
        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            MySQL entry = MySQLEntry.load(fry.getChar(),fry.getInt());
            if(entry != null) {
                entries.add(entry);
            }
        }
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
            performUpdate();
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

        protected void performUpdate() {
            Logger.Log("ConnectionManager$Sync", "performUpdate()");
            if(PERFORM_UPDATE) {
                PERFORM_UPDATE = false;
                sync_contact = true;
                sync_requests = true;
                sync_calendar = true;
                sync_tasklist = true;
            }
            if(sync_contact) {
                sync_contact = !sync_contact();
            }
            if(sync_requests) {
                sync_requests = !sync_request();
            }
            if(sync_calendar) {
                sync_calendar = sync_calendar();
            }
            if(sync_tasklist) {
                sync_tasklist = !sync_tasklist();
            }
        }

        protected boolean sync_contact() {
            Logger.Log("ConnectionManager$Sync", "sync_contact()");
            String resp = MySQL.getLine(MySQL.DIR_CONTACT + "get.php","");
            if(resp != null) {
                ContactList.synchronizeContactsFromMySQL(resp.split(MySQL.S));
                return true;
            }
            return false;
        }

        protected boolean sync_request() {
            Logger.Log("ConnectionManager$Sync", "sync_request()");
            String resp = MySQL.getLine(MySQL.DIR_CONTACT_REQUEST + "get.php","");
            if(resp != null) {
                ContactList.synchronizeContactRequestsFromMySQL(resp.split(MySQL.S));
                return true;
            }
            return false;
        }

        protected boolean sync_calendar() {
            Logger.Log("ConnectionManager$Sync", "sync_calendar()");
            String resp = MySQL.getLine(MySQL.DIR_CALENDAR + "get.php","");
            if(resp != null) {
                Timetable.synchronizeFromMySQL(resp.split(MySQL.S));
            }
            return false;
        }

        protected boolean sync_tasklist() {
            Logger.Log("ConnectionManager$Sync", "sync_tasklist()");
            String resp = MySQL.getLine(MySQL.DIR_TASKLIST + "get.php","");
            if(resp != null) {
                TasklistManager.synchronizeTasklistsFromMySQL(resp.split(MySQL.S));
            }
            return false;
        }

    }

}