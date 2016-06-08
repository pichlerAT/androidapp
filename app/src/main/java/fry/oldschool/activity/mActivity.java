package fry.oldschool.activity;

import android.app.Activity;

import fry.oldschool.utils.App;

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
