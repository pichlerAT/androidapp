package fry.oldschool.utils;

import android.os.AsyncTask;

public class Updater extends AsyncTask<String,String,String> {

    public static final int UPDATE_INTERVAL_TIME = 5 * 60 * 1000;

    protected static Updater updater = new Updater();

    protected boolean update = false;

    @Override
    protected String doInBackground(String... params) {
        while(App.hasInternetConnection && update) {

            App.performUpdate();

            try {
                Thread.sleep(UPDATE_INTERVAL_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
                update = false;
                break;
            }
        }
        updater = new Updater();
        return null;
    }

    protected static void start() {
        if(updater.getStatus() == Status.PENDING) {
            updater.update = true;
            updater.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    protected static void stop() {
        updater.update = false;
    }

}