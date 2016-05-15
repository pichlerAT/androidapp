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
        System.out.println("----- ConnectionManager#sync: pend="+(syncTask.getStatus()==AsyncTask.Status.PENDING)+", inet="+NetworkStateReciever.hasInternetConnection+", ent.s()="+entry.size());
        if(syncTask.getStatus()== AsyncTask.Status.PENDING && NetworkStateReciever.hasInternetConnection) {
            syncTask.execute();
        }else {
            NetworkStateReciever.checkInternet();
        }
    }

    public void load() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(App.getContext().getFilesDir(), App.getContext().getResources().getString(R.string.file_sync))));
            String line;
            int c=0;
            while((line=br.readLine()) != null) {
                String[] r=line.split(",");
                Entry ent = null;

                byte type=Byte.parseByte(r[0]);
                if(type == 0) {
                    ent = new TaskList(Integer.parseInt(r[1]),Integer.parseInt(r[2]),r[3]);
                }else if(type == 1) {
                    ent = new TaskListEntry(Integer.parseInt(r[1]),Integer.parseInt(r[2]),Integer.parseInt(r[3]),r[4],Byte.parseByte(r[5]));
                }

                if(ent != null) {
                    entry.add(ent);
                }
                ++c;
            }
            System.out.println("----- ConnectionManager#load: entry.size()="+entry.size()+", c="+c);
            br.close();
            sync();

        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        int c=0;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(App.getContext().getFilesDir(), App.getContext().getResources().getString(R.string.file_sync))));
            Iterator<Entry> it = entry.iterator();

            if (it.hasNext()) {
                bw.write(it.next().getString());
                ++c;
            }

            while(it.hasNext()) {
                bw.newLine();
                bw.write(it.next().getString());
                ++c;
            }

            bw.close();

        }catch(IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("----- ConnectionManager#save: c="+c);
    }

    String addText(Entry entry) {
        if(entry.user_id < 0) {
            return "delete";
        }
        if(entry.id == 0) {
            return "create";
        }
        return "update";
    }

    protected void add(Entry entry) {
        System.out.println("----- ConnectionManager#add1 "+addText(entry));
        this.entry.add(entry);
        sync();
    }
/*
    protected void add(ArrayList<Entry> entry) {
        System.out.println("----- ConnectionManager#add2");
        this.entry.addAll(entry);
        sync();
    }
*/
    protected void remove(Entry entry) {
        System.out.println("----- ConnectionManager#remove");
        this.entry.remove(entry);
    }

    protected class Sync extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            for(int i=0 ; i<entry.size() ; ) {
                System.out.println("----- ConnectionManager.Sync#doInBackground: Sync in progress");
                if(entry.get(i).mysql_update()) {
                    ++i;
                }
            }
            syncTask = new Sync();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            mysql_listener.mysql_finished("");
            System.out.println("----- ConnectionManager.Sync#onPostExecute");
        }
    }
}