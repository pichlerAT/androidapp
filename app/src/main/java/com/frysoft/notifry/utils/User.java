package com.frysoft.notifry.utils;

import com.frysoft.notifry.data.ConnectionManager;
import com.frysoft.notifry.data.MySQL;
import com.frysoft.notifry.data.NetworkStateReciever;

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

    protected static final Pattern emailPattern = Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);

    public static final String LOCAL = "local";

    private static final String CODE = "xQjQEFdcSMmdvlYCcuxsayrty6O2HqQridfuOpnl";

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

    public static void logout() {
        Utils.saveData();
        deleteLogin();
        MySQL.setLoginData(0, "");

        id = 0;
        email = LOCAL;
        name = null;
        password = null;
        online = false;

        Utils.loadData();
    }

    public static void login(String email, String password) {
        User.email = email;
        User.password = password;
        Utils.loadData();
        NetworkStateReciever.checkInternet();
    }

    public static boolean logon() {
        String resp = MySQL.execute(MySQL.ADR_USER + "login.php", "email=" + email + "&password=" + password);

        if(resp == null) {
            return false;
        }

        if(resp.equals("err_l0")) {
            // TODO Message: email not registered
            return false;

        }else if(resp.equals("err_l1")) {
            // TODO Message: email not veryfied
            return false;

        }else if(resp.equals("err_l2")) {
            // TODO Message: wrong password
            return false;
        }

        if(resp.length() > 4 && resp.substring(0,4).equals("err_")) {
            return false;
        }

        String[] r = resp.split("" + (char)0);
        if(r.length != 2) {
            return false;
        }

        try {
            id = Integer.parseInt(r[0]);
        }catch(NumberFormatException ex) {
            return false;
        }

        MySQL.setLoginData(id, password);

        name = r[1];
        online = true;

        ConnectionManager.performUpdate();
        return true;
    }

    public static boolean register(String email, String name, String password) {
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

    public static void deleteLogin() {
        App.tryDeleteFile("login.fry");
    }

    public static void saveLogin() {
        FileOutputStream outputStream;
        try {
            outputStream = App.getFileOutputStream("login.fry");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }

        FryFile fry = new FryFile.Split("\n");
        fry.write("email=" + email);
        fry.write("password=" + password);
        fry.save(outputStream);
    }

    public static void loadLogin() {
        FileInputStream inputStream;
        try {
            inputStream = App.getFileInputStream("login.fry");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }

        FryFile fry = new FryFile.Split("\n");
        fry.load(inputStream);

        email = fry.getString().split("=")[1];
        password = fry.getString().split("=")[1];

        System.out.println("Fry-Soft: Succesfully loaded login data");
        System.out.println("Fry-Soft: email = "+email);
    }

    public static String getFileName() {
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

        ConnectionManager.add(new Change.Email(newEmail));
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

        ConnectionManager.add(new Change.Name(newName));
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

        ConnectionManager.add(new Change.Password(newPassword));
        return SUCCESS;
    }

    private static abstract class Change extends MySQL {

        protected Change() {
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

        protected static class Email extends Change {

            private String email;

            protected Email(String email) {
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

        protected static class Name extends Change {

            private String name;

            protected Name(String name) {
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

        protected static class Password extends Change {

            private String password;

            protected Password(String password) {
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

        protected static class Delete extends Change {

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
