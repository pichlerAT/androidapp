package com.frysoft.notifry.utils;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.ConnectionManager;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.data.MySQLListener;
import com.frysoft.notifry.data.NetworkStateReciever;
import com.frysoft.notifry.data.Updater;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class App extends Application {

    public static boolean isAppActive = true;

    public static boolean hasInternetConnection = false;

    protected static Context activityContext;

    protected static Context appContext;

    protected static Thread.UncaughtExceptionHandler defaultEH;

    @Override
    public void onCreate() {
        Logger.Log("App", "onCreate()");
        super.onCreate();
        appContext = this;

        //** -----------------------------------------------------
        /*** TODO DEVLOG: these two lines are for the test versions only !
        /**/ defaultEH = new AlphaExceptionHandler();
        /**/ Thread.setDefaultUncaughtExceptionHandler(defaultEH);
        //** -----------------------------------------------------

    }

    public static void load() {
        Data.load();
        NetworkStateReciever.checkInternet();
    }

    public static void setMySQLListener(MySQLListener mysql_Listener) {
        Logger.Log("App", "setMySQLListener(MySQLListener)");
        ConnectionManager.setMySQLListener(mysql_Listener);
    }

    public static void onPause() {
        Logger.Log("App", "onPause()");
        isAppActive = false;
        Updater.stop();
        Data.save();
    }

    public static void onResume() {
        Logger.Log("App", "onResume()");
        isAppActive = true;
        NetworkStateReciever.checkInternet();
    }

    public static Context getContext() {
        Logger.Log("App", "getContext()");
        if(activityContext == null) {
            return appContext;
        }
        return activityContext;
    }

    public static boolean hasActivityContext() {
        return (activityContext != null);
    }

    public static void setContext(Context mContext) {
        Logger.Log("App", "setContext(Context)");
        App.activityContext = mContext;

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof AlphaExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(defaultEH);
        }
    }

    public static boolean tryDeleteFile(String filename) {
        return appContext.deleteFile(filename);
    }

    public static FileOutputStream getFileOutputStream(String filename) throws FileNotFoundException {
        return appContext.openFileOutput(filename, Context.MODE_PRIVATE);
    }

    public static FileInputStream getFileInputStream(String filename) throws FileNotFoundException {
        return App.appContext.openFileInput(filename);
    }

    public static void errorDialog(String title,String message) {
        Logger.Log("App", "errorDialog(String,String)");
        new AlertDialog.Builder(activityContext)
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

    public static int pixelToDPScale(int dp){
        //Logger.Log("App", "pixelToDPScale(int)");
        float scale = activityContext.getResources().getDisplayMetrics().density;
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

    public static Drawable getDrawableFromID(int resourceID){
        return ContextCompat.getDrawable(getContext(), resourceID);
    }


    public static boolean containsCaseInsensitive(String s, List<String> l){
        for (String string : l){
            if (string.equalsIgnoreCase(s)){
                return true;
            }
        }
        return false;
    }

    public static String getStringFromResource(int resource){
        return getContext().getResources().getString(resource);
    }

    public static void loginFailed(String message){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Login failed");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    public static class Settings {

        public static final int SHOW_CONTACT_NAME_PRIMARY_EMAIL_SECONDARY = 0;

        public static final int SHOW_CONTACT_EMAIL_PRIMARY_NAME_SECONDARY = 1;

        public static final int SHOW_CONTACT_NAME_ONLY = 2;

        public static final int SHOW_CONTACT_EMAIL_ONLY = 3;

        private static int displayContactsMode = 0;


        public static final int THEME_BRIGHT = 0;

        public static final int THEME_DARK = 1;

        private static int theme = 0;


        private static boolean moveFinishedTasklistToBottom = false;

        private static boolean notifyWhenTasklistFinished = false;

        private static boolean notifyOnOwnTimetableEvent = true;

        private static boolean notifyOnAllTimetableEvent = false;

        private static boolean showAllContactsGroupAtBottom = true;


        public static void setDisplayContactsMode(int mode) {
            displayContactsMode = mode;
        }

        public static void setTheme(int theme) {
            Settings.theme = theme;
        }

        public static void setMoveFinishedTasklistToBottom(boolean moveFinishedTasklistToBottom) {
            Settings.moveFinishedTasklistToBottom = moveFinishedTasklistToBottom;
        }

        public static void setNotifyWhenTasklistFinished(boolean notifyWhenTasklistFinished) {
            Settings.notifyWhenTasklistFinished = notifyWhenTasklistFinished;
        }

        public static void setNotifyOnOwnTimetableEvent(boolean notifyOnOwnTimetableEvent) {
            Settings.notifyOnOwnTimetableEvent = notifyOnOwnTimetableEvent;
        }

        public static void setNotifyOnAllTimetableEvent(boolean notifyOnAllTimetableEvent) {
            Settings.notifyOnAllTimetableEvent = notifyOnAllTimetableEvent;
        }

        public static void setShowAllContactsGroupAtBottom(boolean showAllContactsGroupAtBottom) {
            Settings.showAllContactsGroupAtBottom = showAllContactsGroupAtBottom;
        }


        public static boolean moveFinishedTasklistToBottom() {
            return moveFinishedTasklistToBottom;
        }

        public static boolean notifyWhenTasklistFinished() {
            return notifyWhenTasklistFinished;
        }

        public static boolean notifyOnOwnTimetableEvent() {
            return notifyOnOwnTimetableEvent;
        }

        public static boolean notifyOnAllTimetableEvent() {
            return notifyOnAllTimetableEvent;
        }

        public static boolean showAllContactsGroupAtBottom() {
            return showAllContactsGroupAtBottom;
        }

        public static int getDisplayContactsMode() {
            return displayContactsMode;
        }

        public static int getTheme() {
            return theme;
        }

        public static void readFrom(FryFile fry) {
            displayContactsMode = fry.getByte();
            theme = fry.getByte();

            byte bools = fry.getByte();
            moveFinishedTasklistToBottom    = ((bools & 0x01) > 0);
            notifyWhenTasklistFinished      = ((bools & 0x02) > 0);
            notifyOnOwnTimetableEvent       = ((bools & 0x04) > 0);
            notifyOnAllTimetableEvent       = ((bools & 0x08) > 0);
            showAllContactsGroupAtBottom    = ((bools & 0x20) > 0);
        }

        public static void writeTo(FryFile fry) {
            fry.writeByte((byte)displayContactsMode);
            fry.writeByte((byte)theme);

            byte bools = 0;

            if(moveFinishedTasklistToBottom) {
                bools |= 0x01;
            }
            if(notifyWhenTasklistFinished) {
                bools |= 0x02;
            }
            if(notifyOnOwnTimetableEvent) {
                bools |= 0x04;
            }
            if(notifyOnAllTimetableEvent) {
                bools |= 0x08;
            }
            if(showAllContactsGroupAtBottom) {
                bools |= 0x10;
            }

            fry.writeByte(bools);
        }

    }

}