package fry.oldschool;

import android.app.Application;
import android.content.Context;

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
}
