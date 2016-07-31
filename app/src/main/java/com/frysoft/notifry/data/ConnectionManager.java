package com.frysoft.notifry.data;

import android.os.AsyncTask;

import java.util.ArrayList;

import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;
import com.frysoft.notifry.utils.User;

public class ConnectionManager {

    protected static boolean PERFORM_UPDATE = false;

    protected static boolean sync_contacts = false;

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
        entries = new ArrayList<>();

        int NoEntries = fry.getChar();
        for(int i=0; i<NoEntries; ++i) {
            MySQL entry = MySQLEntry.load(fry.getChar(),fry.getInt());
            if(entry != null) {
                entries.add(entry);
            }
        }
    }

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
                PERFORM_UPDATE = !sync_all();
            }
            if(sync_contacts) {
                sync_contacts = !sync_contacts();
            }
            if(sync_calendar) {
                sync_calendar = sync_calendar();
            }
            if(sync_tasklist) {
                sync_tasklist = !sync_tasklist();
            }
        }

        protected boolean sync_all() {
            Logger.Log("ConnectionManager$Sync", "sync_all()");
            String resp = MySQL.executeAndroid("request.php","");
            if(resp == null) {
                return false;
            }
            FryFile fry = new FryFile.Split();
            fry.load(resp);
            if(fry.size() < 7) {
                return false;
            }

            ContactList.synchronizeContactsFromMySQL(fry);
            ContactList.synchronizeContactGroupsFromMySQL(fry);
            ContactList.synchronizeContactRequestsFromMySQL(fry);
            TasklistManager.synchronizeTasklistsFromMySQL(fry);
            Timetable.synchronizeSharesFromMySQL(fry);
            Timetable.synchronizeCategoriesFromMySQL(fry);
            Timetable.synchronizeEntriesFromMySQL(fry);
            return true;
        }

        protected boolean sync_contacts() {
            Logger.Log("ConnectionManager$Sync", "sync_contact()");
            String resp = MySQL.executeAndroid(MySQL.DIR_CONTACT + "get.php","");
            if(resp == null) {
                return false;
            }
            FryFile fry = new FryFile.Split();
            fry.load(resp);
            if(fry.size() < 3) {
                return false;
            }

            ContactList.synchronizeContactsFromMySQL(fry);
            ContactList.synchronizeContactGroupsFromMySQL(fry);
            ContactList.synchronizeContactRequestsFromMySQL(fry);
            return true;
        }

        protected boolean sync_calendar() {
            Logger.Log("ConnectionManager$Sync", "sync_calendar()");
            String resp = MySQL.executeAndroid(MySQL.DIR_CALENDAR + "request.php","");
            if(resp == null) {
                return false;
            }
            FryFile fry = new FryFile.Split();
            fry.load(resp);
            if(fry.size() < 3) {
                return false;
            }

            Timetable.synchronizeSharesFromMySQL(fry);
            Timetable.synchronizeCategoriesFromMySQL(fry);
            Timetable.synchronizeEntriesFromMySQL(fry);
            return true;
        }

        protected boolean sync_tasklist() {
            Logger.Log("ConnectionManager$Sync", "sync_tasklist()");
            String resp = MySQL.executeAndroid(MySQL.DIR_TASKLIST + "request.php","");
            if(resp == null) {
                return false;
            }
            FryFile fry = new FryFile.Split();
            fry.load(resp);
            if(fry.size() < 1) {
                return false;
            }
            TasklistManager.synchronizeTasklistsFromMySQL(fry);
            return true;
        }

    }

}