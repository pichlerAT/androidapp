package com.frysoft.notifry.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.frysoft.notifry.R;
import com.frysoft.notifry.data.User;
import com.frysoft.notifry.fragment.ContactFragment;
import com.frysoft.notifry.fragment.TaskFragment;
import com.frysoft.notifry.fragment.TestFragment;
import com.frysoft.notifry.fragment.TestFragmentEdwin;
import com.frysoft.notifry.fragment.TimetableFragment;
import com.frysoft.notifry.fragment.TimetableSlideFragment;
import com.frysoft.notifry.utils.App;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static FragmentManager fm;
    public static FloatingActionButton fab;
    public static TextView TOOLBAR_TITLE;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.setContext(this);
        fm = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TOOLBAR_TITLE = (TextView) toolbar.findViewById(R.id.toolbar_title);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView user = (TextView) headerView.findViewById(R.id.textview_nav_username);
        TextView email = (TextView) headerView.findViewById(R.id.textview_nav_email);

        if (User.isLoggedIn()){
            user.setText(User.getName());
            email.setText(User.getEmail());
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (getSupportFragmentManager().findFragmentByTag("timetable") != null) {
            getSupportFragmentManager().popBackStackImmediate("timetable", 0);
        }
        else if (getSupportFragmentManager().findFragmentByTag("task") != null) {
            getSupportFragmentManager().popBackStackImmediate("task", 0);
        }
        else if (getSupportFragmentManager().findFragmentByTag("contact") != null) {
            getSupportFragmentManager().popBackStackImmediate("contact", 0);
        }
        else if (getSupportFragmentManager().findFragmentByTag("test") != null) {
            getSupportFragmentManager().popBackStackImmediate("test", 0);
        }
        else
            super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fm.popBackStack();
        FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        if (id == R.id.nav_timetable) {
            ft.replace(R.id.frame_fragment_main, new TimetableSlideFragment());
            ft.addToBackStack("timetable");
        } else if (id == R.id.nav_tasks) {
            ft.replace(R.id.frame_fragment_main, new TaskFragment());
            ft.addToBackStack("task");
        } else if (id == R.id.nav_contacts) {
            ft.replace(R.id.frame_fragment_main, new ContactFragment());
            ft.addToBackStack("contact");
        } else if (id == R.id.nav_info) {

        } else if (id == R.id.nav_settings) {

        }
        else if (id == R.id.nav_test) {
            ft.replace(R.id.frame_fragment_main, new TestFragment());
            ft.addToBackStack("test");
        }

        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
