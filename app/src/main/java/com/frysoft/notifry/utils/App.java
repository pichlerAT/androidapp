package com.frysoft.notifry.utils;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.ConnectionManager;
import com.frysoft.notifry.data.ContactList;
import com.frysoft.notifry.data.MySQL;
import com.frysoft.notifry.data.MySQLListener;
import com.frysoft.notifry.data.NetworkStateReciever;
import com.frysoft.notifry.data.TasklistManager;
import com.frysoft.notifry.data.Timetable;
import com.frysoft.notifry.data.Updater;

public class App extends Application {

    public static boolean hasInternetConnection = false;

    public static boolean isAppActive = true;

    protected static Context mContext;

    protected static Context appContext;

    protected static Thread.UncaughtExceptionHandler defaultEH;

    @Override
    public void onCreate() {
        Logger.Log("App", "onCreate()");
        super.onCreate();

        defaultEH = new AlphaExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(defaultEH);

        appContext = this;
        load();
        NetworkStateReciever.checkInternet();
    }

    public static Context getContext() {
        Logger.Log("App", "getContext()");
        if(mContext == null) {
            return appContext;
        }
        return mContext;
    }

    public static void setContext(Context mContext) {
        Logger.Log("App", "setContext(Context)");
        App.mContext = mContext;


        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof AlphaExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(defaultEH);
        }

    }

    public static void setMySQLListener(MySQLListener mysql_Listener) {
        Logger.Log("App", "setMySQLListener(MySQLListener)");
        ConnectionManager.setMySQLListener(mysql_Listener);
    }

    public static void errorDialog(String title,String message) {
        Logger.Log("App", "errorDialog(String,String)");
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
        Logger.Log("App", "onPause()");
        isAppActive = false;
        Updater.stop();
        save();
    }

    public static void onResume() {
        Logger.Log("App", "onResume()");
        isAppActive = true;
        Updater.start();
    }

    public static void load() {
        Logger.Log("App", "load()");
        File file = new File(appContext.getFilesDir(),getFileName());
        if(!file.exists()) {
            return;
        }

        FryFile fry = new FryFile.Compact();
        if(!fry.load(file)) {
            Logger.Log("App#load()","Could not load local file");
            // TODO could not load local file
        }

        ContactList.readFrom(fry);
        TasklistManager.readFrom(fry);
        Timetable.readFrom(fry);
        ConnectionManager.readFrom(fry);
    }

    public static void save() {
        Logger.Log("App", "save()");
        FryFile fry = new FryFile.Compact();

        ContactList.writeTo(fry);
        TasklistManager.writeTo(fry);
        Timetable.writeTo(fry);
        ConnectionManager.writeTo(fry);

        if(!fry.save(new File(App.appContext.getFilesDir(),getFileName()))) {
            Logger.Log("App#save()","Could not save local file");
            // TODO could not save local file
        }
    }

    public static String getFileName() {
        Logger.Log("App", "getFileName()");
        return MySQL.USER_EMAIL.replace(".","_") + ".fry";
    }

    public static int pixelToDPScale(int dp){
        //Logger.Log("App", "pixelToDPScale(int)");
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }

    public static String getMonthName(int month, Resources resources){
        String month_name = null;

        if (month == 0)
            month_name = resources.getString(R.string.month_jan);
        else if (month == 1)
            month_name = resources.getString(R.string.month_feb);
        else if (month == 2)
            month_name = resources.getString(R.string.month_mar);
        else if (month == 3)
            month_name = resources.getString(R.string.month_apr);
        else if (month == 4)
            month_name = resources.getString(R.string.month_may);
        else if (month == 5)
            month_name = resources.getString(R.string.month_jun);
        else if (month == 6)
            month_name = resources.getString(R.string.month_jul);
        else if (month == 7)
            month_name = resources.getString(R.string.month_aug);
        else if (month == 8)
            month_name = resources.getString(R.string.month_sep);
        else if (month == 9)
            month_name = resources.getString(R.string.month_oct);
        else if (month == 10)
            month_name = resources.getString(R.string.month_nov);
        else if (month == 11)
            month_name = resources.getString(R.string.month_dec);

        return month_name;
    }

    public static int getColorFromID(int colorID){
        return ContextCompat.getColor(App.getContext(), colorID);
    }

    public static int getColorFromDrawable(Drawable drawable){
        return ((ColorDrawable) drawable).getColor();
    }

    public static String formatDate(int day, int month, int year){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        //Format different if it's set in the settings
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        return sdf.format(cal.getTime());
    }

    public static String formatTime(int hour, int minute){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, minute);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

        return sdf.format(cal.getTime());
    }
}