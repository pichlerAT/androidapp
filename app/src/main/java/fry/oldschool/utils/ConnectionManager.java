package fry.oldschool.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import fry.oldschool.R;

public class ConnectionManager extends MySQL {

    protected ArrayList<Entry> entry = new ArrayList<>();

    protected Sync syncTask = new Sync();

    protected MySQLListener mysql_listener;

    @Override
    protected boolean mysql_update() {
        ArrayList<String> resp = connect_list("tasklist/get.php","");
        Iterator<String> it = resp.iterator();
        if(it.next().equals("suc")) {
            while(it.hasNext()) {
                TaskListManager.update(new TaskList(it.next()));
            }
            return true;
        }
        return false;
    }

    protected void setMySQLListener(MySQLListener mysql_listener) {
        this.mysql_listener = mysql_listener;
    }

    public void sync() {
        if(syncTask.getStatus() == AsyncTask.Status.PENDING && App.hasInternetConnection) {
            syncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            NetworkStateReciever.checkInternet();
        }
    }

    public void load() {
        try {
            File file = new File(App.appContext.getFilesDir(), App.appContext.getResources().getString(R.string.file_sync));
            if(!file.exists()) {
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line=br.readLine()) != null) {

                Entry ent = Entry.create(line);

                if(ent == null) {
                    continue;
                }

                entry.add(ent);
            }
            br.close();

        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(App.appContext.getFilesDir(), App.appContext.getResources().getString(R.string.file_sync))));

            for(Entry e : entry) {
                String line = e.getConManString();
                if(line != null) {
                    bw.write(line);
                    bw.newLine();
                }
            }

            bw.close();

        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    protected boolean remove(Entry entry) {
        return this.entry.remove(entry);
    }

    protected void add(Entry entry) {
        this.entry.add(entry);
        sync();
    }

    protected void notifyMySQLListener() {
        (new NotifyListener()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
    }

    protected class Sync extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            if(App.PERFORM_UPDATE) {
                App.PERFORM_UPDATE = false;
                App.conLis.mysql_update();
                mysql_update();
            }
            for(int i=0 ; i<entry.size() ; ) {
                if(entry.get(i).mysql_update()) {
                    entry.remove(i);
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
            if(entry.size() > 0) {
                //sync();
            }
        }

    }

    protected class NotifyListener extends AsyncTask<String,String,String> {

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
}