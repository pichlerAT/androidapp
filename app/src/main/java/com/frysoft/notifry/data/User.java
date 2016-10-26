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

    private static final String LOCAL = "Local";

    private static long logon_time;

    private static boolean online = false;

    private static boolean loggedIn = false;

    private static boolean local = true;

    private static int id;

    private static String email = LOCAL;

    private static String name = null;

    private static String password = null;

    private static short TimezoneOffset;

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

    private static void setId(int id) {
        User.id = id;
        Data.Timetable.id = id;
    }

    public static void logout() {
        Data.save();
        deleteLogin();
        MySQL.setLoginData("", "");

        local = true;
        setId(0);
        email = LOCAL;
        name = null;
        password = null;
        online = false;
        loggedIn = false;

        Data.load();
    }

    public static int login(String email, String password) {
        if(!local) {
            logout();
        }

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
            logout();
            return ERR_WRONG_PASSWORD;
        }

        System.out.println("# LOADED " + email + " DATA");

        Data.load();
        loggedIn = true;
        NetworkStateReciever.checkInternet();

        return SUCCESS;
    }

    protected static boolean logon() {
        MySQL mysql = new MySQL(MySQLEntry.PATH_USER, "login.php");
        mysql.addString("email", email);
        mysql.addString("password", password);
        FryFile fry = mysql.execute();

        if(fry == null) {
            logout();
            return false;
        }

        String resp = fry.readString();

        if(resp.equals("err_l0")) {
            // TODO Message: email not registered
            System.out.println("# LOGON FAILED");
            logout();
            return false;

        }else if(resp.equals("err_l1")) {
            // TODO Message: email not veryfied
            System.out.println("# LOGON FAILED");
            logout();
            return false;

        }else if(resp.equals("err_l2")) {
            // TODO Message: wrong password
            System.out.println("# LOGON FAILED");
            logout();
            return false;
        }

        if(resp.length() > 4 && resp.substring(0,4).equals("err_")) {
            logout();
            return false;
        }

        try {
            logon_time = Long.parseLong(resp);
        }catch(NumberFormatException ex) {
            logout();
            return false;
        }
        setId(fry.readId());

        MySQL.setLoginData(email, password);

        name = fry.readString();
        online = true;
        loggedIn = true;

        ConnectionManager.performUpdate();

        System.out.println("# LOGON SUCCESSFULL: "+email);
        return true;
    }

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

        if(fry.size() < 2) {
            return;
        }

        local = false;
        email = fry.readString().split("=")[1];
        password = fry.readString().split("=")[1];

        System.out.println("# LOGGED IN AS "+email);
    }

    protected static String getFileName() {
        Logger.Log("User", "getFileName()");
        if(local) {
            return (email + ".fry");
        }
        return (LOCAL + ".fry");
    }

    protected static void encode(FryFile fry) {
        fry.writeEncoded(email, CODE, 0);
        fry.writeEncoded(password, CODE, email.length());
    }

    protected static boolean decode(FryFile fry) {
        return (email.equals(fry.readDecoded(CODE, 0)) && password.equals(fry.readDecoded(CODE, email.length())));
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

    private static abstract class user extends MySQLEntry {

        protected user() {
            super(0, 0, 0);
        }

        @Override
        protected void addData(MySQL mysql) {
        }

        @Override
        protected void remove() {
        }

        @Override
        public boolean canEdit() {
            return false;
        }

        @Override
        public int getShareId() {
            return 0;
        }

        @Override
        protected void sync(MySQLEntry entry) {
        }

        @Override
        protected char getType() {
            return 0;
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
            protected boolean mysql() {
                MySQL mysql = new MySQL(PATH_REGISTER, "register.php");
                mysql.addString("email", email);
                mysql.addString("password", password);
                mysql.addString("name", name);
                FryFile fry = mysql.execute();

                if(fry == null) {
                    return false;
                }

                String resp = fry.readString();
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
            protected boolean mysql() {
                MySQL mysql = new MySQL(PATH_USER_CHANGE, "email.php");
                mysql.addString("new_email", this.email);
                FryFile fry = mysql.execute();

                if(fry == null) {
                    // TODO Message: error when trying to change email

                }else {
                    User.email = this.email;
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
            protected boolean mysql() {
                MySQL mysql = new MySQL(PATH_USER_CHANGE, "name.php");
                mysql.addString("name", this.name);
                FryFile fry = mysql.execute();

                if(fry == null) {
                    // TODO Message: error when trying to change name

                }else {
                    User.name = this.name;
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
            protected boolean mysql() {
                MySQL mysql = new MySQL(PATH_USER_CHANGE, "password.php");
                mysql.addString("new_password", this.password);
                FryFile fry = mysql.execute();

                if(fry == null) {
                    // TODO Message: error when trying to change password

                }else {
                    User.password = this.password;
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
            protected boolean mysql() {
                MySQL mysql = new MySQL(PATH_USER, "delete.php");
                mysql.addString("cmd", "Delete");
                FryFile fry = mysql.execute();

                if(fry == null) {
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
