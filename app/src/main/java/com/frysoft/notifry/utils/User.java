package com.frysoft.notifry.utils;

import android.content.Context;

import com.frysoft.notifry.data.ConnectionManager;
import com.frysoft.notifry.data.MySQL;
import com.frysoft.notifry.data.NetworkStateReciever;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class User {

    public static final String LOCAL = "local";

    //private static boolean veryfied = false;

    private static boolean online = false;

    private static int id;

    private static String email = LOCAL;

    private static String name;

    private static String password;

    public static boolean isOnline() {
        return online;
    }

    public static boolean isLocal() {
        return email.equals(LOCAL);
    }

    public static int getId() {
        return id;
    }

    public static String getEmail() {
        return email;
    }

    public static String getName() {
        return name;
    }

    public static String getPassword() {
        return password;
    }

    public static void logout() {
        App.saveData();
        deleteLogin();

        id = 0;
        email = LOCAL;
        name = null;
        password = null;
        online = false;

        App.loadData();
    }

    public static void login(String email, String password) {
        User.email = email;
        User.password = password;
        App.loadData();
        NetworkStateReciever.checkInternet();
    }

    public static boolean logon() {
        String resp = MySQL.execute(MySQL.ADDRESS + "login.php", "email=" + email + "&password=" + password);

        if(resp == null) {
            return false;
        }

        if(resp.equals("err_l0")) {
            // TODO email not registered
            return false;

        }else if(resp.equals("err_l1")) {
            // TODO wrong password
            return false;
        }

        if(resp.length() > 4 && resp.substring(0,4).equals("err_")) {
            return false;
        }

        String[] r = resp.split("" + FryFile.Split.DEFAULT_SPLIT_STRING);
        if(r.length != 2) {
            return false;
        }

        try {
            id = Integer.parseInt(r[0]);
        }catch(NumberFormatException ex) {
            return false;
        }

        name = r[1];
        online = true;

        ConnectionManager.performUpdate();
        return true;
    }

    public static boolean register(String email, String name, String password) {
        String resp = MySQL.execute(MySQL.ADDRESS + "register.php", "email=" + email + "&password=" + password + "&name=" + name);
        if(resp == null) {
            return false;
        }

        if(resp.equals("err_r1")) {
            // TODO email already registered
            return false;
        }

        if(resp.length() > 4 && resp.substring(0,4).equals("err_")) {
            return false;
        }

        try {
            id = Integer.parseInt(resp);
        }catch(NumberFormatException ex) {
            return false;
        }

        User.email = email;
        User.name = name;
        User.password = password;

        return true;
    }

    public static void deleteLogin() {
        /*
        File file = new File(App.getContext().getFilesDir(),"login.fry");
        if(file.exists()) {
            file.delete();
        }
        */
        App.appContext.deleteFile("login.fry");
    }

    public static void saveLogin() {
        //File file = new File(App.appContext.getFilesDir(),"login.fry");
        FileOutputStream outputStream;
        try {
            outputStream = App.appContext.openFileOutput("login.fry", Context.MODE_PRIVATE);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }

        FryFile fry = new FryFile.Split("\n");
        fry.write("email=" + getEmail());
        fry.write("password=" + getPassword());
        fry.save(outputStream);
    }

    public static void loadLogin() {
        FileInputStream inputStream;
        try {
            inputStream = App.appContext.openFileInput("login.fry");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }
        /*
        File file = new File(App.appContext.getFilesDir(),"login.fry");
        if(!file.exists()) {
            return;
        }
        */

        FryFile fry = new FryFile.Split("\n");
        fry.load(inputStream);

        String[] data = fry.getString().split("\n");
        login(data[0].split("=")[1], data[1].split("=")[1]);

        //email = fry.getString().split("=")[1];
        //password = fry.getString().split("=")[1];
    }

}
