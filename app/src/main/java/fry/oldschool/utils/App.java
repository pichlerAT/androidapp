package fry.oldschool.utils;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import fry.oldschool.R;

public class App extends Application {

    protected static boolean PERFORM_UPDATE;

    protected static boolean DELETE_LOCAL_FILES = false;

    public static boolean hasInternetConnection = false;

    public static ArrayList<TaskList> TaskLists=new ArrayList<>();

    public static ContactList conLis;

    public static TaskListManager taskMan = new TaskListManager();

    public static ConnectionManager conMan = new ConnectionManager();

    public static Context mContext;

    public static void setContext(Context mContext) {
        App.mContext = mContext;
    }

    public static void setMySQLListener(MySQLListener mysql_Listener) {
        conMan.setMySQLListener(mysql_Listener);
    }

    public static void errorDialog(String title,String message) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.error_message_neutral_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        PERFORM_UPDATE = true;
        load();
        NetworkStateReciever.checkInternet();
    }

    protected void load() {
        if(DELETE_LOCAL_FILES) {
            delete_local_files();
        }
        load_settings();
        conMan.load();
        TaskList.load();
    }

    public static void save() {
        System.out.println("--------- App#save ----------");
        save_settings();
        conMan.save();
        TaskList.save();
    }

    protected void delete_local_files() {
        int[] files = {R.string.file_settings,R.string.file_sync,R.string.file_tasklist};
        for(int i : files) {
            File f = new File(mContext.getFilesDir(), mContext.getResources().getString(i));
            f.delete();
        }
    }

    protected void load_settings() {
        try{
            File file = new File(mContext.getFilesDir(),mContext.getResources().getString(R.string.file_settings));
            if(!file.exists()) {
                conLis = new ContactList();
                return;
            }

            BufferedReader br=new BufferedReader(new FileReader(file));

            String line=br.readLine();
            if(line == null) {
                conLis = new ContactList();
                return;
            }

            conLis = new ContactList(line.split(";"));

            while((line = br.readLine()) != null) {
                conLis.groups.add(conLis.groups.size()-1,new ContactGroup(line));
            }

        }catch (IOException ex) {
            conLis=new ContactList();
            ex.printStackTrace();
        }
    }

    public static void save_settings() {
        try{
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(mContext.getFilesDir(),mContext.getResources().getString(R.string.file_settings))));

            for(Contact c : conLis.groups.get(conLis.groups.size()-1).contacts ) {
                bw.write(c.id + ";" + c.email + ";" + c.name + ";");
            }

            Iterator<ContactGroup> it = conLis.groups.iterator();
            if(it.hasNext()) {
                ContactGroup g = it.next();
                while(it.hasNext()) {
                    bw.newLine();
                    bw.write(g.id + ";" + g.name + ";" + g.getContactsString());
                    g = it.next();
                }
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int pixelToDPScale(int dp){
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }

}