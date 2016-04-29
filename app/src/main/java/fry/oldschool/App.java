package fry.oldschool;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Stefan on 28.04.2016.
 */
public class App extends Application {

    protected static Context mContext;

    public App() {
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
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

}
