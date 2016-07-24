package com.frysoft.notifry.activity;

import android.app.Activity;

import com.frysoft.notifry.utils.App;

public abstract class mActivity extends Activity {

    @Override
    public void onPause() {
        super.onPause();
        App.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        App.onResume();
    }

}
