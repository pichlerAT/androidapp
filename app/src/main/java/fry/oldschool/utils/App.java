package fry.oldschool.utils;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.io.File;
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
import fry.oldschool.data.Timetable;
import fry.oldschool.data.TimetableCategory;
import fry.oldschool.data.TimetableEntry;

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
            case R.id.nav_timetable:
                System.out.println("::::: ------- Categories -------");
                for(TimetableCategory c : Timetable.categories) {
                    System.out.println("::::: "+c.name+"["+c.id+"]");
                }
                System.out.println("::::: -------  Entries   -------");
                for(TimetableEntry e : Timetable.getEntries()) {
                    System.out.println(":::::"+e.title+"["+e.id+"], cat="+e.category_id);
                }
                System.out.println("::::: --------------------------");

/*
                Time time = new Time(60*12);
                DateTime datetime= new DateTime(Date.getToday(),time);
                time = new Time(58);

                TimetableCategory cat = TimetableCategory.create("off cat");

                TimetableEntry.create("test1","te1dis",datetime,time,(byte)0,cat);
                TimetableEntry.create("test2","blablabla",datetime,time,(byte)0,cat);
*/

                break;
        }
    }

    public static void load() {
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
        return MySQL.USER_EMAIL.replace(".","_") + ".fry";
    }

    public static int pixelToDPScale(int dp){
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }

}