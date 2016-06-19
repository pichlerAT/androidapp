package fry.oldschool.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import fry.oldschool.R;
import fry.oldschool.adapter.TaskCreateAdapter;
import fry.oldschool.data.Tasklist;
import fry.oldschool.data.TasklistEntry;
import fry.oldschool.utils.App;

/**
 * Created by Edwin Pichler on 04.05.2016.
 */
public class TaskCreateActivity extends mAppCompatActivity{

    private ViewPager pager = null;
    private TaskCreateAdapter adapter = null;

    protected Context ctx = this;
    protected ArrayList<RelativeLayout> layouts;
    protected int lastPos = 0;
    protected boolean swipeSave = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);

        //Initialize variables
        layouts = new ArrayList<>();

        //Initialize toolbar and set the back button within it
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_task_create);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        RelativeLayout taskView = (RelativeLayout) inflater.inflate(R.layout.activity_task_pagertemplate, null);
        TableLayout taskEntries = (TableLayout) taskView.findViewById(R.id.tablelayout_task_entries);
        EditText taskName = (EditText) taskView.findViewById(R.id.edittext_task_name);
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
        for(Tasklist tdl : App.Tasklists){
            taskView = (RelativeLayout) inflater.inflate(R.layout.activity_task_pagertemplate, null);
            taskEntries = (TableLayout) taskView.findViewById(R.id.tablelayout_task_entries);
            taskName = (EditText) taskView.findViewById(R.id.edittext_task_name);
            taskName.setText(tdl.name);
            layouts.add(taskView);

            if (tdl.entries.size() > 0) {
                for (TasklistEntry ent : tdl.entries) {
                    entryRow = new TableRow(ctx);
                    entryState = new CheckBox(ctx);
                    entryName = createEntryName();
                    entryName.setText(ent.description);
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
            if (args != null && tdl == App.Tasklists.get(index)){
                setCurrentPage(taskView);
            }
        }

    }

    protected void taskList(Tasklist task, int viewPage){
        RelativeLayout currentView = (RelativeLayout) adapter.getView(viewPage);
        EditText header = (EditText) currentView.findViewById(R.id.edittext_task_name);

        if (!header.getText().toString().matches("")) {
            TableLayout taskEntries = (TableLayout) currentView.findViewById(R.id.tablelayout_task_entries);
            //When no task is found, then it creates a new one, otherwise the name of the task will be changed
            if (task == null) {
                task = new Tasklist(header.getText().toString());
                App.Tasklists.add(task);
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
                        if (task.entries.size() > i)
                            entry = task.entries.get(i);
                        if (entry != null && (!entry.description.equals(entryText) || entry.isDone() != checkbox.isChecked()))
                            entry.change(entryText, checkbox.isChecked());
                        else if (entry == null)
                            task.addEntry(entryText, checkbox.isChecked());
                    }
                }

            }
        }
    }

    //This method saves the current task to the database
    protected void saveList(){
        //Get data from the current displayed view
        int position = 0;
        if (swipeSave)
            position = lastPos - 1;
        else
            position = adapter.getItemPosition(getCurrentPage()) -1;

        if (position >= 0) {
            Tasklist tdl = App.Tasklists.get(position);
            taskList(tdl, position+1);
        }
        else
            taskList(null, 0);

        swipeSave = false;
    }

    //This method adds a new empty entry to the actual task
    protected void entryListeners(final TableLayout taskEntries, final EditText entry, final TableRow curRow){
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
                    taskEntries.addView(entryRow, curIndex+1);

                    //Adds a new listener to the new created entry
                    entryListeners(taskEntries, entryName, entryRow);
                    return true;
                }

                else if(keyCode == KeyEvent.KEYCODE_DEL){
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

    protected EditText createEntryName(){
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
    public View getCurrentPage(){
        return adapter.getView(pager.getCurrentItem());
    }

    public void setCurrentPage(View v){
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

}
