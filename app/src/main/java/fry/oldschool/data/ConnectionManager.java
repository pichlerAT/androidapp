package fry.oldschool.data;

import android.os.AsyncTask;

import java.util.ArrayList;

import fry.oldschool.utils.App;
import fry.oldschool.utils.FryFile;

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
        mysql_listener = listener;
    }

    protected static void add(MySQL entry) {
        if(entry.id == 0 || !hasEntry(entry.type, entry.id)) {
            entries.add(entry);
            sync();
        }
    }

    protected static void notifyMySQLListener() {
        (new NotifyListener()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void performUpdate() {
        PERFORM_UPDATE = true;
        sync();
    }

    public static boolean hasEntry(int type,int id) {
        for(int i=0; i<entries.size(); ++i) {
            MySQL ent = entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                return true;
            }
        }
        return false;
    }

    protected static boolean remove(MySQL entry) {
        return entries.remove(entry);
    }

    protected static void remove(char type,int id) {
        for(int i=0; i<entries.size(); ++i) {
            MySQL ent = entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                entries.remove(i);
            }
        }
    }

    protected static void sync() {
        if(syncTask.getStatus() == AsyncTask.Status.PENDING && App.hasInternetConnection) {
            syncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            NetworkStateReciever.checkInternet();
        }
    }

    public static void writeTo(FryFile fry) {
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
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if(mysql_listener != null) {
                mysql_listener.mysql_finished(/*message*/);
            }
        }
    }

    protected static class Sync extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
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
            syncTask = new Sync();
        }

        protected void performUpdate() {
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