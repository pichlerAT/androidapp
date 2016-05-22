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

public class ConnectionManager {

    protected ArrayList<Entry> entry = new ArrayList<>();

    protected Sync syncTask = new Sync();

    protected MySQLListener mysql_listener;

    protected void setMySQLListener(MySQLListener mysql_listener) {
        this.mysql_listener = mysql_listener;
    }

    public void sync() {
        if(syncTask.getStatus()== AsyncTask.Status.PENDING && App.hasInternetConnection) {
            syncTask.execute();
        }else {
            NetworkStateReciever.checkInternet();
        }
    }

    protected Entry createEntry(String[] r) {
        byte type = Byte.parseByte(r[0]);
        switch(type) {
            case Entry.TYPE_CONTACT: return new Contact(Integer.parseInt(r[1]),r[2],r[3]);
            case Entry.TYPE_CONTACTREQUEST_SEND: return new ContactRequest.Send(r[1]);
            case Entry.TYPE_CONTACTREQUEST_ACCEPT: return new ContactRequest.Accept(new Contact(Integer.parseInt(r[1]),r[2],r[3]));
            case Entry.TYPE_CONTACTREQUEST_DECLINE: return new ContactRequest.Decline(Integer.parseInt(r[1]));
            case Entry.TYPE_TASKLIST: return new TaskList(Integer.parseInt(r[1]),Integer.parseInt(r[2]),r[3]);
            case Entry.TYPE_TASKLISTENTRY: return new TaskListEntry(Integer.parseInt(r[1]),Integer.parseInt(r[2]),Integer.parseInt(r[3]),r[4],Byte.parseByte(r[5]));
            case Entry.TYPE_CONTACTGROUP: return new ContactGroup(Integer.parseInt(r[0]),r);
            default: return null;
        }
    }

    public void load() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(App.mContext.getFilesDir(), App.mContext.getResources().getString(R.string.file_sync))));
            String line;
            int c=0;
            while((line=br.readLine()) != null) {
                String[] r=line.split(",");

                Entry ent = createEntry(r);

                if(ent != null) {
                    entry.add(ent);
                }
                ++c;
            }
            br.close();
            sync();

        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        int c=0;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(App.mContext.getFilesDir(), App.mContext.getResources().getString(R.string.file_sync))));
            Iterator<Entry> it = entry.iterator();

            if (it.hasNext()) {
                bw.write(it.next().getConnectionManagerString());
                ++c;
            }

            while(it.hasNext()) {
                bw.newLine();
                bw.write(it.next().getConnectionManagerString());
                ++c;
            }

            bw.close();

        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void add(Entry entry) {
        this.entry.add(entry);
        sync();
    }

    protected void remove(Entry entry) {
        this.entry.remove(entry);
    }

    protected class Sync extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            if(App.PERFORM_UPDATE) {
                App.PERFORM_UPDATE = false;
                App.conLis.mysql_update();
            }
            for(int i=0 ; i<entry.size() ; ) {
                if(!entry.get(i).mysql_update()) {
                    ++i;
                }
            }
            syncTask = new Sync();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if(mysql_listener != null) {
                mysql_listener.mysql_finished();
            }
        }
    }
}