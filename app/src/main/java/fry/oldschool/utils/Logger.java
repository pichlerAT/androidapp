package fry.oldschool.utils;

import android.content.Intent;

import fry.oldschool.data.MySQL;

public class Logger {

    public static final boolean PRINT = false;

    public static final int CLASSPATH_LENGTH = 45;

    public static final int MAX_LOGS = 250;

    protected static int index = 0;

    protected static String[] logs = new String[MAX_LOGS];

    static {
        logs[0] = "Logger:" ;
    }

    public static void Log(String classPath, String method) {
        ++index;
        if (index == MAX_LOGS) {
            index = 0;
        }

        for(int i=classPath.length(); i<CLASSPATH_LENGTH; ++i) {
            classPath += " ";
        }

        logs[index] = classPath + ": " + method;

        if (PRINT) {
            System.out.println("Logger:      " + logs[index]);
        }
    }

    public static String getString() {
        String log = "";

        for(int i=index+1; i!=index; ++i) {
            if(i == MAX_LOGS) {
                i = 0;
            }
            if(logs[i] == null) {
                continue;
            }
            log += logs[i] + "\n";
        }

        return log;
    }

    public static void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"fragner.stefan95@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Generall Report: " + MySQL.USER_EMAIL);
        intent.putExtra(Intent.EXTRA_TEXT, getString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

}
