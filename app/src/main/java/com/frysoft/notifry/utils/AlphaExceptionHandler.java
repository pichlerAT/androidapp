package com.frysoft.notifry.utils;

import android.content.Intent;

import com.frysoft.notifry.data.User;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AlphaExceptionHandler implements Thread.UncaughtExceptionHandler {

    protected Thread.UncaughtExceptionHandler defaultEH;

    protected AlphaExceptionHandler() {
        defaultEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        StringWriter result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        ex.printStackTrace(printWriter);
        String stackTrace = result.toString();
        printWriter.close();

        String log = Logger.getString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@notifry.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report: " + User.getEmail());
        intent.putExtra(Intent.EXTRA_TEXT, stackTrace + "\n\n" + log);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //App.getContext().startActivity(Intent.createChooser(intent, "Send Bugreport"));
        //App.getContext().startActivity(intent);

        defaultEH.uncaughtException(thread, ex);
    }

}