package fry.oldschool.activity;

import android.support.v7.app.AppCompatActivity;

import fry.oldschool.utils.App;

public abstract class mAppCompatActivity extends AppCompatActivity {

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
