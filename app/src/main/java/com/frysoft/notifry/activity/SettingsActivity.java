package com.frysoft.notifry.activity;

import android.os.Bundle;

import com.frysoft.notifry.R;
import com.frysoft.notifry.utils.App;

public class SettingsActivity extends mActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        App.setContext(this);


    }

}
