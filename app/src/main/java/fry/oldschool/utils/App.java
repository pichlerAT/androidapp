package fry.oldschool.utils;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.io.IOException;

import fry.oldschool.R;
import fry.oldschool.data.ConnectionManager;
import fry.oldschool.data.Contact;
import fry.oldschool.data.ContactGroup;
import fry.oldschool.data.ContactList;
import fry.oldschool.data.MySQL;
import fry.oldschool.data.MySQLListener;
import fry.oldschool.data.NetworkStateReciever;
import fry.oldschool.data.Tasklist;
import fry.oldschool.data.TasklistManager;
import fry.oldschool.data.Timetable;
import fry.oldschool.data.TimetableCategory;
import fry.oldschool.data.TimetableEntry;
import fry.oldschool.data.Updater;

public class App extends Application {

    public static boolean hasInternetConnection = false;

    public static boolean isAppActive = true;

    public static Context mContext;

    public static Context appContext;

    protected static Thread.UncaughtExceptionHandler defaultEH;

    @Override
    public void onCreate() {
        Logger.Log("App#onCreate()");
        super.onCreate();

        defaultEH = new AlphaExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(defaultEH);

        appContext = this;
        load();
        NetworkStateReciever.checkInternet();
    }

    public static Context getContext() {
        if(mContext == null) {
            return appContext;
        }
        return mContext;
    }

    public static void setContext(Context mContext) {
        Logger.Log("App#setContext(Context)");
        App.mContext = mContext;


        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof AlphaExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(defaultEH);
        }

    }

    public static void setMySQLListener(MySQLListener mysql_Listener) {
        Logger.Log("App#setMySQLListener(MySQLListener)");
        ConnectionManager.setMySQLListener(mysql_Listener);
    }

    public static void errorDialog(String title,String message) {
        Logger.Log("App#errorDialog(String,String)");
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
        Logger.Log("App#onPause()");
        isAppActive = false;
        Updater.stop();
        save();
    }

    public static void onResume() {
        Logger.Log("App#onResume()");
        isAppActive = true;
        Updater.start();
    }

    static void print(String s) {
        System.out.println("::::: "+s);
    }

    public static void fragmentChanged(int id) {
        switch(id) {
            case R.id.nav_contacts:
                print("SOF Contacts");
                print("");
                print("- Contacts:");
                for(Contact c : ContactList.getAllContacts()) {
                    print(c.getEmail());
                }
                print("");
                print("- ContactGroups:");
                for(ContactGroup g : ContactList.getGroups()) {
                    print(g.getName());
                }
                print("");
                print("EOF Contacts");
                //throw new ArrayIndexOutOfBoundsException("TEST TEST TEST");
                break;

            case R.id.nav_tasks:
                print("SOF Tasks");
                print("");
                print("- Tasklists:");
                for(Tasklist t : TasklistManager.getTasklists()) {
                    print(t.getName());
                }
                print("");
                print("EOF Tasks");
                break;

            case R.id.nav_timetable:
                print("SOF Calendar");
                print("");
                print("- Categories:");
                for(TimetableCategory c : Timetable.getCategories()) {
                    print(c.getName());
                }
                print("");
                print("- Entries:");
                for(TimetableEntry e : Timetable.getEntries()) {
                    print(e.getTitle());
                }
                print("");
                print("EOF Calendar");
                break;
        }
    }

    public static void load() {
        Logger.Log("App#load()");
        try{
            File file = new File(appContext.getFilesDir(),getFileName());
            if(!file.exists()) {
                return;
            }

            FryFile fry = new FryFile();
            fry.loadUTF8(file);

            ContactList.readFrom(fry);
            ConnectionManager.readFrom(fry);
            TasklistManager.readFrom(fry);
            Timetable.readFrom(fry);

        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void save() {
        Logger.Log("App#save()");
        FryFile fry = new FryFile();

        ContactList.writeTo(fry);
        ConnectionManager.writeTo(fry);
        TasklistManager.writeTo(fry);
        Timetable.writeTo(fry);

        try {
            fry.saveUTF8(new File(App.appContext.getFilesDir(),getFileName()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getFileName() {
        Logger.Log("App#getFileName()");
        return MySQL.USER_EMAIL.replace(".","_") + ".fry";
    }

    public static int pixelToDPScale(int dp){
        Logger.Log("App#pixelToDPScale(int)");
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }

}