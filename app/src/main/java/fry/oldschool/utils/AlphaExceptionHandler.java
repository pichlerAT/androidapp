package fry.oldschool.utils;

import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;

import fry.oldschool.data.MySQL;

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
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"fragner.stefan95@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report: " + MySQL.USER_EMAIL);
        intent.putExtra(Intent.EXTRA_TEXT, stackTrace + "\n\n" + log);
        App.mContext.startActivity(Intent.createChooser(intent, "Send email..."));

        defaultEH.uncaughtException(thread, ex);
    }

}
