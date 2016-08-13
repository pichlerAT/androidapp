package com.frysoft.notifry.data;

import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.FryFile;
import com.frysoft.notifry.utils.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.regex.Pattern;

public class User {

    public static final int SUCCESS = 0 ;

    public static final int ERR_NO_INTERNET = 1 ;

    public static final int ERR_WRONG_PASSWORD = 2 ;

    public static final int ERR_INVALID_PASSWORD = 3 ;

    public static final int ERR_INVALID_NAME = 4 ;

    public static final int ERR_INVALID_EMAIL = 5 ;

    public static final int ERR_NO_OFFLINE_FILE = 6 ;



    protected static final Pattern emailPattern = Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);

    private static final String CODE = "xQjQEFdcSMmdvlYCcuxsayrty6O2HqQridfuOpnl";

    private static boolean online = false;

    private static boolean loggedIn = false;

    private static boolean local = true;

    private static int id;

    private static String email = null;

    private static String name = null;

    private static String password = null;

    private static int UserTimezoneOffset;

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static boolean isOnline() {
        return (online && App.hasInternetConnection);
    }

    public static boolean isLocal() {
        return local;
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

    public static void logout() {
        Data.save();
        deleteLogin();
        MySQL.setLoginData(0, "");

        local = true;
        id = 0;
        email = null;
        name = null;
        password = null;
        online = false;
        loggedIn = false;

        Data.load();
    }

    public static int login(String email, String password) {
        User.email = email;
        User.password = password;
        local = false;

        FileInputStream inputStream;
        try {
            inputStream = App.getFileInputStream(User.getFileName());
        }catch(FileNotFoundException ex) {

            System.out.println("# NO USER FILE: " + User.getEmail());
            //ex.printStackTrace();
            NetworkStateReciever.checkInternet();
            return ERR_NO_OFFLINE_FILE;
        }

        FryFile fry = new FryFile.Compact();
        if(!fry.loadFromStream(inputStream)) {
            Logger.Log("App#load()","Could not load local file");
            // TODO could not load local file
            NetworkStateReciever.checkInternet();
            return ERR_NO_OFFLINE_FILE;
        }

        if(!User.decode(fry)) {
            return ERR_WRONG_PASSWORD;
        }

        System.out.println("# LOADED " + email + " DATA");

        loggedIn = true;
        Data.load();
        NetworkStateReciever.checkInternet();

        return SUCCESS;
    }

    protected static boolean logon() {
        String resp = MySQL.execute(MySQL.ADR_USER + "login.php", "email=" + email + "&password=" + password);

        if(resp == null) {
            logout();
            return false;
        }

        if(resp.equals("err_l0")) {
            // TODO Message: email not registered
            logout();
            return false;

        }else if(resp.equals("err_l1")) {
            // TODO Message: email not veryfied
            logout();
            return false;

        }else if(resp.equals("err_l2")) {
            // TODO Message: wrong password
            logout();
            return false;
        }

        if(resp.length() > 4 && resp.substring(0,4).equals("err_")) {
            logout();
            return false;
        }

        String[] r = resp.split("" + (char)0);
        if(r.length != 2) {
            logout();
            return false;
        }

        try {
            id = Integer.parseInt(r[0]);
        }catch(NumberFormatException ex) {
            logout();
            return false;
        }

        MySQL.setLoginData(id, password);

        name = r[1];
        online = true;
        loggedIn = true;

        ConnectionManager.performUpdate();

        return true;
    }
/*
    protected static void equalsOfflinePassword(String password) {
        FileInputStream inputStream;
        try {
            inputStream = App.getFileInputStream(User.getFileName());
        }catch(FileNotFoundException ex) {
            return;
        }

        FryFile fry = new FryFile.Compact();
        if(!fry.loadFromStream(inputStream)) {
            return;
        }

        if(User.decode(fry)) {
            return;
        }

        FryFile newFry = new FryFile.Compact();
        newFry.writeChar(Data.MOST_RECENT_SAVE_FILE_VERSION);
        newFry.writeEncoded(email, CODE, 0);
        newFry.writeEncoded(password, CODE, email.length());

        while(fry.hasNext()) {
            newFry.writeChar(fry.getChar());
        }

        try {
            FileOutputStream outputStream = App.getFileOutputStream(User.getFileName());
            if (!fry.saveToStream(outputStream)) {
                // TODO could not save local file
            }
        }catch (FileNotFoundException ex) {
            //ex.printStackTrace();
            // TODO could not save local file
        }
    }
*/
    protected static void deleteLogin() {
        App.tryDeleteFile("login.fry");
    }

    protected static void saveLogin() {
        FileOutputStream outputStream;
        try {
            outputStream = App.getFileOutputStream("login.fry");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }

        FryFile fry = new FryFile.Split("\n");
        fry.writeString("email=" + email);
        fry.writeString("password=" + password);
        fry.saveToStream(outputStream);
    }

    protected static void loadLogin() {
        FileInputStream inputStream;
        try {
            inputStream = App.getFileInputStream("login.fry");
        } catch (FileNotFoundException ex) {
            System.out.println("# LOGGED IN AS local");
            //ex.printStackTrace();
            return;
        }

        FryFile fry = new FryFile.Split("\n");
        fry.loadFromStream(inputStream);

        email = fry.getString().split("=")[1];
        password = fry.getString().split("=")[1];

        System.out.println("# LOGGED IN AS "+email);
    }

    protected static String getFileName() {
        Logger.Log("User", "getFileName()");
        return (email + ".fry");
    }

    protected static void encode(FryFile fry) {
        fry.writeEncoded(email, CODE, 0);
        fry.writeEncoded(password, CODE, email.length());
    }

    protected static boolean decode(FryFile fry) {
        return (email.equals(fry.getDecoded(CODE, 0)) && password.equals(fry.getDecoded(CODE, email.length())));
    }

    public static int register(String email, String name, String password) {
        if(!online) {
            return ERR_NO_INTERNET;
        }
        if(email == null || email.isEmpty() || !emailPattern.matcher(email).matches()) {
            return ERR_INVALID_EMAIL;
        }

        ConnectionManager.add(new user.register(email, name ,password));
        return SUCCESS;
    }

    public static int changeEmail(String password, String newEmail) {
        if(!online) {
            return ERR_NO_INTERNET;
        }
        if(newEmail == null || newEmail.isEmpty() || !emailPattern.matcher(newEmail).matches()) {
            return ERR_INVALID_EMAIL;
        }
        if(!User.password.equals(password)) {
            return ERR_WRONG_PASSWORD;
        }

        ConnectionManager.add(new user.email(newEmail));
        return SUCCESS;
    }

    public static int changeName(String password, String newName) {
        if(!online) {
            return ERR_NO_INTERNET;
        }
        if(newName == null || newName.isEmpty()) {
            return ERR_INVALID_NAME;
        }
        if(!User.password.equals(password)) {
            return ERR_WRONG_PASSWORD;
        }

        ConnectionManager.add(new user.name(newName));
        return SUCCESS;
    }

    public static int changePassword(String password_1, String password_2, String newPassword) {
        if(!online) {
            return ERR_NO_INTERNET;
        }
        if(newPassword == null || newPassword.isEmpty()) {
            return ERR_INVALID_PASSWORD;
        }
        if(!password_1.equals(password_2) || !User.password.equals(password_1)) {
            return ERR_WRONG_PASSWORD;
        }

        ConnectionManager.add(new user.password(newPassword));
        return SUCCESS;
    }

    private static abstract class user extends MySQL {

        protected user() {
            super(BASETYPE_UPDATE, 0, 0);
        }

        @Override
        protected boolean mysql_create() {
            return false;
        }

        @Override
        protected boolean mysql_delete() {
            return false;
        }

        protected static class register extends user {

            private String email;

            private String name;

            private String password;

            protected register(String email, String name, String password) {
                super();
                this.email = email;
                this.name = name;
                this.password = password;
            }

            @Override
            protected boolean mysql_update() {
                String resp = MySQL.execute(MySQL.ADR_REGISTER + "register.php", "email=" + email + "&password=" + password + "&name=" + name);
                if(resp == null) {
                    return false;
                }

                if(resp.equals("err_r1")) {
                    // TODO Message: email already registered
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
        }

        protected static class email extends user {

            private String email;

            protected email(String email) {
                super();
                this.email = email;
            }

            @Override
            protected boolean mysql_update() {
                String resp = execute(MySQL.ADR_USER_CHANGE + "email.php", "user_id=" + id + "&password=" + password + "&email=" + email);
                if(resp == null || !resp.equals("success")) {
                    // TODO Message: error when trying to change email

                }else {
                    User.email = email;
                    // TODO Message: email change successfull
                }
                return true;
            }

        }

        protected static class name extends user {

            private String name;

            protected name(String name) {
                super();
                this.name = name;
            }

            @Override
            protected boolean mysql_update() {
                String resp = execute(MySQL.ADR_USER_CHANGE + "email.php", "user_id=" + id + "&password=" + password + "&name=" + name);
                if(resp == null || !resp.equals("success")) {
                    // TODO Message: error when trying to change name

                }else {
                    User.name = name;
                    // TODO Message: name change successfull
                }
                return true;
            }

        }

        protected static class password extends user {

            private String password;

            protected password(String password) {
                super();
                this.password = password;
            }

            @Override
            protected boolean mysql_update() {
                String resp = execute(MySQL.ADR_USER_CHANGE + "password.php", "user_id=" + id + "&password=" + User.password + "&new_password=" + password);
                if(resp == null || !resp.equals("success")) {
                    // TODO Message: error when trying to change password

                }else {
                    User.password = password;
                    // TODO Message: password change successfull
                }
                return true;
            }

        }

        protected static class delete extends user {

            protected delete() {
                super();
            }

            @Override
            protected boolean mysql_update() {
                String resp = execute(MySQL.ADR_USER + "delete.php", "user_id=" + id + "&password=" + User.password + "&cmd=Delete");
                if(resp == null || !resp.equals("success")) {
                    // TODO Message: error when trying to delete account

                }else {
                    logout();
                    // TODO Message: account deletion successfull
                }
                return true;
            }

        }
    }

}
