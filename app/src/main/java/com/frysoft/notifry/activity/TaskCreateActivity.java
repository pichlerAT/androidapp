package com.frysoft.notifry.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import com.frysoft.notifry.R;
import com.frysoft.notifry.adapter.ColorAdapter;
import com.frysoft.notifry.adapter.TaskCreateAdapter;
import com.frysoft.notifry.data.Data;
import com.frysoft.notifry.data.Tasklist;
import com.frysoft.notifry.data.TasklistEntry;
import com.frysoft.notifry.utils.App;

/**
 * Created by Edwin Pichler on 04.05.2016.
 */
public class TaskCreateActivity extends mAppCompatActivity {

    private ViewPager pager = null;
    private TaskCreateAdapter adapter = null;

    protected Context ctx = this;
    protected ArrayList<LinearLayout> layouts;
    protected int lastPos = 0;
    protected boolean swipeSave = false;
    protected int mCurrentColor = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        App.setContext(this);

        //Initialize variables
        layouts = new ArrayList<>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_task_create);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveList();
                finish();
            }
        });

        //Initialize toolbar and set the back button within it
        Toolbar toolbar = (Toolbar) findViewById(R.id.task_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Check if any arguements are passed to the activity
        Bundle args = getIntent().getExtras();
        int index = 0;
        if (args != null)
            index = args.getInt("index");

        //Initialize adapter and set to viewpager
        adapter = new TaskCreateAdapter();
        pager = (ViewPager) findViewById(R.id.viewpager_task_id);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (lastPos != -1) {
                    swipeSave = true;
                    saveList();
                }
                lastPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Initialize the first view for viewpager, which is an empty task
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout taskView = (LinearLayout) inflater.inflate(R.layout.activity_task_pagertemplate, null);
        TableLayout taskEntries = (TableLayout) taskView.findViewById(R.id.tablelayout_task_entries);
        AppCompatEditText taskName = (AppCompatEditText) taskView.findViewById(R.id.edittext_task_name);
        layouts.add(taskView);

        //Add an empty entry for the empty task
        TableRow entryRow = new TableRow(ctx);
        entryRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        CheckBox entryState = new CheckBox(ctx);
        EditText entryName = createEntryName();
        entryRow.addView(entryState);
        entryRow.addView(entryName);
        taskEntries.addView(entryRow);

        //Add the empty task to the viewpager as the first position
        adapter.addView(taskView);
        adapter.notifyDataSetChanged();

        //Set listener for the first entry
        entryListeners(taskEntries, entryName, entryRow);

        //Add all active tasks from the database of the current user to the viewpager
        for (Tasklist tdl : Data.Tasklists.getList()) {

            taskView = (LinearLayout) inflater.inflate(R.layout.activity_task_pagertemplate, null);
            taskEntries = (TableLayout) taskView.findViewById(R.id.tablelayout_task_entries);
            taskName = (AppCompatEditText) taskView.findViewById(R.id.edittext_task_name);
            taskName.setText(tdl.getName());
            layouts.add(taskView);

            ImageButton buttonColor = (ImageButton) taskView.findViewById(R.id.button_task_pagertemplate_color);
            buttonColor.setColorFilter(tdl.getColor(), PorterDuff.Mode.SRC_ATOP);

            if (tdl.getNoEntries() > 0) {
                for (TasklistEntry ent : tdl.getEntries()) {
                    entryRow = new TableRow(ctx);
                    entryState = new CheckBox(ctx);
                    entryName = createEntryName();
                    entryName.setText(ent.getDescription());
                    entryListeners(taskEntries, entryName, entryRow);
                    entryState.setChecked(ent.isDone());

                    entryRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    entryRow.addView(entryState);
                    entryRow.addView(entryName);
                    taskEntries.addView(entryRow);
                }
            }
            //Add an empty entry if no entires are available
            else {
                entryRow = new TableRow(ctx);
                entryRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                entryState = new CheckBox(ctx);
                entryName = createEntryName();

                entryRow.addView(entryState);
                entryRow.addView(entryName);
                taskEntries.addView(entryRow);
                entryListeners(taskEntries, entryName, entryRow);
            }

            adapter.addView(taskView);
            adapter.notifyDataSetChanged();
            if (args != null && tdl == Data.Tasklists.getList().get(index)) {
                setCurrentPage(taskView);
            }
        }


    }

    public void onClickColor(final View v){
        View colorDialog = View.inflate(App.getContext(), R.layout.color_dialog, null);
        GridView color_gridview = (GridView) colorDialog.findViewById(R.id.gridview_color_dialog);
        ColorAdapter color_adapter = new ColorAdapter(App.getContext(), R.layout.color_dialog_item, App.getContext().getResources().getStringArray(R.array.colors), "");

        color_gridview.setAdapter(color_adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(App.getContext());
        builder.setView(color_gridview)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        final Dialog dialog = builder.create();
        dialog.show();

        color_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView color_item = (ImageView) view.findViewById(R.id.color_item);
                int color = App.getColorFromDrawable(color_item.getBackground());
                //App.getDrawableFromID(R.drawable.ic_color).setColorFilter(App.getColorFromDrawable(color_item.getBackground()), PorterDuff.Mode.SRC_ATOP);
                ((ImageButton) v).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                mCurrentColor = color;
                dialog.dismiss();
            }
        });
    }

    protected void taskList(Tasklist task, int viewPage) {
        LinearLayout currentView = (LinearLayout) adapter.getView(viewPage);
        EditText header = (EditText) currentView.findViewById(R.id.edittext_task_name);
        String headerString = header.getText().toString();

        if (mCurrentColor == 0 && task != null){
            mCurrentColor = task.getColor();
        }
        else if (mCurrentColor == 0){
            mCurrentColor = App.getColorFromID(R.color.colorDefault);
        }

        if (!headerString.matches("")) {
            TableLayout taskEntries = (TableLayout) currentView.findViewById(R.id.tablelayout_task_entries);
            //When no task is found, then it creates a new one, otherwise the name of the task will be changed
            if (task == null) {
                task = Data.create.Tasklist(null, headerString, mCurrentColor); // TODO: Category
            } else {
                task.setName(headerString);
                task.setColor(mCurrentColor);
            }

            //In this loop task entries are created, or changed if they already exist
            for (int i = 0; i < taskEntries.getChildCount(); i++) {

                View table_view = taskEntries.getChildAt(i);
                if (table_view instanceof TableRow) {

                    TableRow row = (TableRow) table_view;
                    CheckBox checkbox = (CheckBox) row.getChildAt(0);
                    EditText edittext = (EditText) row.getChildAt(1);

                    String entryText = edittext.getText().toString();

                    if (!entryText.isEmpty()) {
                        TasklistEntry entry = null;
                        if (task.getNoEntries() > i)
                            entry = task.getEntry(i);
                        if (entry != null && (!entry.getDescription().equals(entryText) || entry.isDone() != checkbox.isChecked()))
                            entry.set(entryText, checkbox.isChecked());
                        else if (entry == null)
                            task.addEntry(entryText, checkbox.isChecked());
                    }
                }

            }
        }
        mCurrentColor = 0;
    }

    //This method saves the current task to the database
    protected void saveList() {
        //Get data from the current displayed view
        int position = 0;
        if (swipeSave)
            position = lastPos - 1;
        else
            position = adapter.getItemPosition(getCurrentPage()) - 1;

        if (position >= 0) {
            Tasklist tdl = Data.Tasklists.getList().get(position);
            taskList(tdl, position + 1);
        } else
            taskList(null, 0);

        swipeSave = false;
    }

    //This method adds a new empty entry to the actual task
    protected void entryListeners(final TableLayout taskEntries, final EditText entry, final TableRow curRow) {
        entry.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int curIndex = taskEntries.indexOfChild(curRow);
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    TableRow entryRow = new TableRow(ctx);
                    entryRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    CheckBox entryState = new CheckBox(ctx);
                    EditText entryName = createEntryName();

                    entryRow.addView(entryState);
                    entryRow.addView(entryName);
                    taskEntries.addView(entryRow, curIndex + 1);

                    //Adds a new listener to the new created entry
                    entryListeners(taskEntries, entryName, entryRow);
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (entry.getText().toString().matches("")) {
                        if (curIndex != 0) {
                            TableRow aboveRow = (TableRow) taskEntries.getChildAt(curIndex - 1);
                            EditText aboveText = (EditText) aboveRow.getChildAt(1);

                            taskEntries.removeView(curRow);
                            aboveText.setFocusableInTouchMode(true);
                            aboveText.requestFocus();
                        }
                    }

                }
                return false;
            }
        });

    }

    protected EditText createEntryName() {
        EditText text = new EditText(ctx);
        text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        text.setBackgroundColor(0);
        text.setFocusableInTouchMode(true);
        text.requestFocus();

        return text;
    }

    /*
        public void addView(View v){
            int index = adapter.addView(v);

            //if view is a new list
            //pager.setCurrentItem(index, true
        }

        public void removeView(View v){
            int index = adapter.removeView(pager, v);
            if (index == adapter.getCount())
                index--;
            pager.setCurrentItem(index);
        }
    */
    public View getCurrentPage() {
        return adapter.getView(pager.getCurrentItem());
    }

    public void setCurrentPage(View v) {
        pager.setCurrentItem(adapter.getItemPosition(v), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:
                saveList();
                //setResult(Activity.RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

