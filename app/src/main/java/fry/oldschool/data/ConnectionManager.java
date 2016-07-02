package fry.oldschool.data;

import android.os.AsyncTask;

import java.util.ArrayList;

import fry.oldschool.utils.App;
import fry.oldschool.utils.NetworkStateReciever;
import fry.oldschool.utils.FryFile;

public class ConnectionManager {

    protected static boolean sync_contact = false;

    protected static boolean sync_requests = false;

    protected static boolean sync_calendar = false;

    protected static boolean sync_tasklist = false;

    protected static Sync syncTask = new Sync();

    protected static MySQLListener mysql_listener;

    protected static ArrayList<MySQL> online_entries = new ArrayList<>();

    protected static ArrayList<OfflineEntry> offline_entries = new ArrayList<>();

    public static void setMySQLListener(MySQLListener listener) {
        mysql_listener = listener;
    }

    public static void add(MySQL entry) {
        if(entry.id == 0 || !hasOnlineEntry(entry.type, entry.id)) {
            online_entries.add(entry);
            sync();
        }
    }

    public static void add(OfflineEntry entry) {
        if(entry.id == 0 || !hasOfflineEntry(entry.type, entry.id)) {
            offline_entries.add(entry);
            sync();
        }
    }

    protected static void notifyMySQLListener() {
        (new NotifyListener()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static boolean hasOnlineEntry(int type,int id) {
        for(int i=0; i<online_entries.size(); ++i) {
            MySQL ent = online_entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasOfflineEntry(int type,int id) {
        for(int i=0; i<offline_entries.size(); ++i) {
            OfflineEntry ent = offline_entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasEntry(int type,int id) {
        return (hasOfflineEntry(type,id) || hasOnlineEntry(type,id));
    }

    public static boolean remove(MySQL entry) {
        return online_entries.remove(entry);
    }

    public static boolean remove(OfflineEntry entry) {
        return offline_entries.remove(entry);
    }

    public static void remove(char type,int id) {
        for(int i=0; i<online_entries.size(); ++i) {
            MySQL ent = online_entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                online_entries.remove(i);
            }
        }
        for(int i=0; i<offline_entries.size(); ++i) {
            OfflineEntry ent = offline_entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                offline_entries.remove(i);
            }
        }
    }

    public static void sync() {
        if(syncTask.getStatus() == AsyncTask.Status.PENDING && App.hasInternetConnection) {
            syncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            NetworkStateReciever.checkInternet();
        }
    }

    public static void writeTo(FryFile fry) {
        fry.write((char)offline_entries.size());
        for(OfflineEntry ent : offline_entries) {
            fry.write(ent.type);
            fry.write(ent.id);
        }
    }

    public static void readFrom(FryFile fry) {
        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            offline_entries.add(new OfflineEntry(fry.getChar(),fry.getInt()));
        }
    }

    public static class NotifyListener extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... args) {
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if(mysql_listener != null) {
                mysql_listener.mysql_finished(/*message*/);
            }
        }
    }

    public static class Sync extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            performUpdate();
            int on_index = 0;
            int off_index = 0;
            do {
                while(on_index < online_entries.size()) {
                    if(online_entries.get(on_index).mysql()) {
                        online_entries.remove(on_index);
                        notifyMySQLListener();
                    }else {
                        ++on_index;
                    }
                }
                while(off_index < offline_entries.size()) {
                    if(offline_entries.get(off_index).mysql()) {
                        offline_entries.remove(off_index);
                        notifyMySQLListener();
                    }else {
                        ++off_index;
                    }
                }
            } while(on_index < online_entries.size() || off_index < offline_entries.size());
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            syncTask = new Sync();
        }

        protected void performUpdate() {
            if(App.PERFORM_UPDATE) {
                App.PERFORM_UPDATE = false;
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
            String resp = MySQL.getLine(MySQL.DIR_CONTACT + "get.php","");
            if(resp != null) {
                ContactList.synchronizeContactsFromMySQL(resp.split(MySQL.S));
                return true;
            }
            return false;
        }

        protected boolean sync_request() {
            String resp = MySQL.getLine(MySQL.DIR_CONTACT_REQUEST + "get.php","");
            if(resp != null) {
                ContactList.synchronizeContactRequestsFromMySQL(resp.split(MySQL.S));
                return true;
            }
            return false;
        }

        protected boolean sync_calendar() {
            String resp = MySQL.getLine(MySQL.DIR_CALENDAR + "get.php","");
            if(resp != null) {
                Timetable.synchronizeFromMySQL(resp.split(MySQL.S));
            }
            return false;
        }

        protected boolean sync_tasklist() {
            String resp = MySQL.getLine(MySQL.DIR_TASKLIST + "get.php","");
            if(resp != null) {
                TasklistManager.synchronizeTasklistsFromMySQL(resp.split(MySQL.S));
            }
            return false;
        }

    }

}