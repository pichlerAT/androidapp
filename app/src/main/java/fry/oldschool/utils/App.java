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

import fry.oldschool.R;
import fry.oldschool.data.ConnectionManager;
import fry.oldschool.data.ContactGroup;
import fry.oldschool.data.ContactList;
import fry.oldschool.data.MySQL;
import fry.oldschool.data.MySQLListener;
import fry.oldschool.data.Tasklist;
import fry.oldschool.data.TasklistManager;

public class App extends Application {

    public static boolean PERFORM_UPDATE = false;

    public static boolean hasInternetConnection = false;

    public static boolean isAppActive = true;

    public static Context mContext;

    public static Context appContext;

    public static ArrayList<Tasklist> Tasklists = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        init();
        load();
        PERFORM_UPDATE = true;
        NetworkStateReciever.checkInternet();
    }

    public static void init() {
        ContactList.groups.add(new ContactGroup("All Contacts"));
    }

    public static void setContext(Context mContext) {
        App.mContext = mContext;
    }

    public static void setMySQLListener(MySQLListener mysql_Listener) {
        ConnectionManager.setMySQLListener(mysql_Listener);
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

    public static void onPause() {
        isAppActive = false;
        Updater.stop();
        save();
    }

    public static void onResume() {
        isAppActive = true;
        Updater.start();
    }

    public static void performUpdate() {
        PERFORM_UPDATE = true;
        ConnectionManager.sync();
    }

    public static void fragmentChanged(int id) {
        switch(id) {
            case R.id.nav_contacts: break;
            case R.id.nav_tasks: break;
            case R.id.nav_timetable: break;
        }
    }

    public static void load() {
        try{
            File file = new File(appContext.getFilesDir(),getFileName());
            if(!file.exists()) {
                return;
            }

            BufferedReader br=new BufferedReader(new FileReader(file));

            ContactList.recieveLocalSaveString(br.readLine());
            ConnectionManager.recieveLocalSaveString(br.readLine());
            TasklistManager.recieveLocalSaveString(br.readLine());
            //Timetable.recieveLocalSaveString(br.readLine().toCharArray());

        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void save() {
        try{
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(appContext.getFilesDir(),getFileName())));

            bw.write(ContactList.getLocalSaveString());
            bw.newLine();
            bw.write(ConnectionManager.getLocalSaveString());
            bw.newLine();
            bw.write(TasklistManager.getLocalSaveString());
            //bw.newLine();
            //bw.write(Timetable.getLocalSaveString());

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileName() {
        return MySQL.USER_EMAIL.replace(".","_") + ".fry";
    }

    public static int pixelToDPScale(int dp){
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }

}