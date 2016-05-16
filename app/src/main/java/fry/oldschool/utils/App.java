package fry.oldschool.utils;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import fry.oldschool.R;

public class App extends Application {

    protected static boolean PERFORM_UPDATE;

    public static ContactList conLis;
    public static ConnectionManager conMan = new ConnectionManager();

    public static Context mContext;

    public static void setContext(Context mContext) {
        App.mContext = mContext;
    }

    public static void setMySQLListener(MySQLListener mysql_Listener) {
        conMan.setMySQLListener(mysql_Listener);
    }

    public static void errorDialog(String title,String message) {
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

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        PERFORM_UPDATE = true;
        load_settings();
    }

    protected void load_settings() {
        try{
            BufferedReader br=new BufferedReader(new FileReader(new File(App.mContext.getFilesDir(),App.mContext.getResources().getString(R.string.file_settings))));

            String line=br.readLine();
            conLis=new ContactList(line.split(";"));


        }catch (IOException ex) {
            conLis=new ContactList();

            ex.printStackTrace();
        }
    }

    public static void save_settings() {
        try{
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(App.mContext.getFilesDir(),App.mContext.getResources().getString(R.string.file_settings))));

            Iterator<Contact> it = conLis.groups.get(0).contacts.iterator();
            if(it.hasNext()) {
                bw.write(it.next().getString());
                while(it.hasNext()) {
                    bw.write(";" + it.next().getString());
                }
            }


            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}