package fry.oldschool.data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Iterator;

import fry.oldschool.utils.App;
import fry.oldschool.utils.NetworkStateReciever;
import fry.oldschool.utils.FryFile;

public class ConnectionManager {

    protected static boolean sync_contact = false;

    protected static boolean sync_requests = false;

    protected static boolean sync_tasklist = false;

    protected static Sync syncTask = new Sync();

    protected static MySQLListener mysql_listener;

    protected static ArrayList<OnlineEntry> online_entries = new ArrayList<>();

    protected static ArrayList<OfflineEntry> offline_entries = new ArrayList<>();

    public static void setMySQLListener(MySQLListener listener) {
        mysql_listener = listener;
    }

    protected static void add(OnlineEntry entry) {
        online_entries.add(entry);
        sync();
    }

    protected static void add(OfflineEntry entry) {
        offline_entries.add(entry);
        sync();
    }

    protected static void notifyMySQLListener() {
        (new NotifyListener()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static boolean hasEntry(int type,int id) {
        for(int i=0; i<online_entries.size(); ++i) {
            OnlineEntry ent = online_entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                return true;
            }
        }
        for(int i=0; i<offline_entries.size(); ++i) {
            OnlineEntry ent = offline_entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                return true;
            }
        }
        return false;
    }

    protected static boolean remove(OnlineEntry entry) {
        return online_entries.remove(entry);
    }

    protected static boolean remove(OfflineEntry entry) {
        return offline_entries.remove(entry);
    }

    protected static void remove(char type,int id) {
        for(int i=0; i<online_entries.size(); ++i) {
            OnlineEntry ent = online_entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                online_entries.remove(i);
            }
        }
        for(int i=0; i<offline_entries.size(); ++i) {
            OnlineEntry ent = offline_entries.get(i);
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

    public static void writeTo(FryFile file) {
        file.write(offline_entries.toArray());
    }

    public static void readFrom(FryFile file) {
        int NoEntries = file.getChar();
        for(int i=0; i<NoEntries; ++i) {
            OfflineEntry ent = OfflineEntry.create(file.getChar(),file.getInt());
            if(ent != null) {
                offline_entries.add(ent);
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
                sync_tasklist = true;
            }
            if(sync_contact) {
                sync_contact = !sync_contact();
            }
            if(sync_requests) {
                sync_requests = !sync_request();
            }
            if(sync_tasklist) {
                sync_tasklist = !sync_tasklist();
            }
        }

        protected boolean sync_contact() {
            MySQL mysql = new MySQL();
            ArrayList<String> resp = mysql.getLines(MySQL.DIR_CONTACT + "get.php","");
            Iterator<String> it = resp.iterator();
            if(it.hasNext()) {
                String line = it.next();
                if(line.substring(0,3).equals("suc")) {
                    if(line.length() > 3) {
                        ContactList.updateContacts(line.substring(3).split(MySQL.S));
                    }
                    while(it.hasNext()) {
                        ContactList.updateContactGroup(it.next().split(MySQL.S));
                    }
                    return true;
                }
            }
            return false;
        }

        protected boolean sync_request() {
            MySQL mysql = new MySQL();
            String resp = mysql.getLine(MySQL.DIR_CONTACT_REQUEST + "get.php","");
            if(resp.substring(0,3).equals("suc")) {
                if(resp.length() > 3) {
                    ContactList.setContactRequests(resp.substring(3).split(MySQL.S));
                }
                return true;
            }
            return false;
        }

        protected boolean sync_tasklist() {
            MySQL mysql = new MySQL();
            ArrayList<String> resp = mysql.getLines(MySQL.DIR_TASKLIST + "get.php","");
            Iterator<String> it = resp.iterator();
            if(it.hasNext() && it.next().equals("suc")) {
                while(it.hasNext()) {
                    TaskistManager.updateTasklists(it.next().split(MySQL.S));
                }
                return true;
            }
            return false;
        }

    }

}