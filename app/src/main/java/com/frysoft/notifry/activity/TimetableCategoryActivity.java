package com.frysoft.notifry.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.utils.App;

/**
 * Created by Edwin Pichler on 31.07.2016.
 */
public class TimetableCategoryActivity extends mAppCompatActivity {

    protected AppCompatEditText mCategoryName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_create);
        App.setContext(this);

        //Initialize toolbar and set the back button within it
        Toolbar toolbar = (Toolbar) findViewById(R.id.category_create_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mCategoryName = (AppCompatEditText) findViewById(R.id.edittext_category_create);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:
                String category_name = mCategoryName.getText().toString();
                if (category_name != "")
                    Data.create.Category(category_name, 0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
