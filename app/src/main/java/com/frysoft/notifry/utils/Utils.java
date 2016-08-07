package com.frysoft.notifry.utils;

import android.content.Context;

import com.frysoft.notifry.data.ConnectionManager;
import com.frysoft.notifry.data.ContactList;
import com.frysoft.notifry.data.MySQLListener;
import com.frysoft.notifry.data.NetworkStateReciever;
import com.frysoft.notifry.data.Tags;
import com.frysoft.notifry.data.TasklistManager;
import com.frysoft.notifry.data.Timetable;
import com.frysoft.notifry.data.Updater;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Utils {

    public static final char SAVE_FILE_VERSION = 1;

    private static char saveFileVersion = 0;

    public static boolean isAppActive = true;

    public static boolean hasInternetConnection = false;

    public static void loadData() {
        Logger.Log("App", "loadData()");

        FryFile fry = getFryFile();
        if(fry == null || fry.size() <= 0) {
            return;
        }

        ContactList.readFrom(fry);
        Tags.static_readFrom(fry);
        TasklistManager.readFrom(fry);
        Timetable.readFrom(fry);
        ConnectionManager.readFrom(fry);
    }

    public static FryFile getFryFile() {
        Logger.Log("App", "getFryFile()");
        if(User.isLocal()) {
            return getLocalFryFile();
        }

        FileInputStream inputStream;
        try {
            inputStream = App.getFileInputStream(User.getFileName());
        }catch(FileNotFoundException ex) {
            ex.printStackTrace();
            return getLocalFryFile();
        }

        FryFile fry = new FryFile.Compact();
        if(!fry.load(inputStream)) {
            Logger.Log("App#load()","Could not load local file");
            // TODO could not load local file
            return getLocalFryFile();
        }

        saveFileVersion = fry.getChar();

        if(User.decode(fry)) {

            System.out.println("Fry-Soft: Succesfully loaded user data");
            System.out.println("Fry-Soft: email = "+User.getEmail());

            return fry;
        }

        return getLocalFryFile();
    }

    protected static FryFile getLocalFryFile() {
        try {
            FileInputStream inputStream = App.getFileInputStream(User.getFileName());

            FryFile fry = new FryFile.Compact();
            if (fry.load(inputStream)) {

                saveFileVersion = fry.getChar();

                System.out.println("Fry-Soft: Succesfully loaded local data");

                return fry;
            }
        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void saveData() {
        Logger.Log("App", "saveData()");
        FryFile fry = new FryFile.Compact();

        fry.write(SAVE_FILE_VERSION);

        if(User.isLocal()) {
            User.deleteLogin();

        }else {
            User.saveLogin();
            User.encode(fry);
        }

        ContactList.writeTo(fry);
        Tags.static_writeTo(fry);
        TasklistManager.writeTo(fry);
        Timetable.writeTo(fry);
        ConnectionManager.writeTo(fry);

        try {
            FileOutputStream outputStream = App.getFileOutputStream(User.getFileName());
            if (!fry.save(outputStream)) {
                Logger.Log("App#save()", "Could not save local file: FryFile.save() = false");
                // TODO could not save local file

            }
        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Logger.Log("App#save()", "Could not save local file: file not found");
            // TODO could not save local file
        }
    }

    public static void setMySQLListener(MySQLListener mysql_Listener) {
        Logger.Log("App", "setMySQLListener(MySQLListener)");
        ConnectionManager.setMySQLListener(mysql_Listener);
    }

    public static void onPause() {
        Logger.Log("App", "onPause()");
        isAppActive = false;
        Updater.stop();
        saveData();
    }

    public static void onResume() {
        Logger.Log("App", "onResume()");
        isAppActive = true;
        NetworkStateReciever.checkInternet();
    }

}
