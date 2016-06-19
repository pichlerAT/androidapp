package fry.oldschool.data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Iterator;

import fry.oldschool.utils.App;
import fry.oldschool.utils.NetworkStateReciever;

public class ConnectionManager {

    protected static boolean sync_contact = false;

    protected static boolean sync_requests = false;

    protected static boolean sync_tasklist = false;

    protected static Sync syncTask = new Sync();

    protected static MySQLListener mysql_listener;

    protected static ArrayList<Entry> entries = new ArrayList<>();

    public static void setMySQLListener(MySQLListener listener) {
        mysql_listener = listener;
    }

    protected static void add(Entry entry) {
        entries.add(entry);
        sync();
    }

    protected static void notifyMySQLListener() {
        (new NotifyListener()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected static boolean remove(Entry entry) {
        return entries.remove(entry);
    }

    protected static void remove(char type,int id) {
        for(int i=0; i<entries.size(); ++i) {
            Entry ent = entries.get(i);
            if(ent.id == id && (ent.type & type) == type) {
                entries.remove(i);
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

    public static String getLocalSaveString() {
        if(entries.size() == 0) {
            return ("" + (char)0);
        }

        String line = "";
        for(Entry ent : entries) {
            String str = ent.getConManString();
            if(str != null) {
                line += str;
            }
        }
        return line;
    }

    public static void recieveLocalSaveString(String line) {
        if(line == null) {
            return;
        }
        char[] charArray = line.toCharArray();

        for(int i=2; i<charArray.length; i+=3) {
            Entry ent = Entry.create(charArray[i-2],charArray[i-1],charArray[i]);
            if(ent != null) {
                entries.add(ent);
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
            for(int i=0 ; i< entries.size() ; ) {
                if(entries.get(i).mysql()) {
                    entries.remove(i);
                    notifyMySQLListener();
                }else {
                    ++i;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            syncTask = new Sync();
            if(entries.size() > 0) {
                //sync();
            }
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
            ArrayList<String> resp = mysql.getLines("contact/get.php","");
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
                    TasklistManager.updateTasklists(it.next().split(MySQL.S));
                }
                return true;
            }
            return false;
        }

    }

}