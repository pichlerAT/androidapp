package fry.oldschool.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import fry.oldschool.R;
import fry.oldschool.adapter.TaskCreateAdapter;
import fry.oldschool.fragment.TaskFragment;
import fry.oldschool.utils.ToDoList;

/**
 * Created by Edwin Pichler on 04.05.2016.
 */
public class TaskCreateActivity extends AppCompatActivity{

    private ViewPager pager = null;
    private TaskCreateAdapter adapter = null;

    protected Context ctx = this;
    protected ArrayList<Integer> index_list = new ArrayList<Integer>();
    protected ArrayList<RelativeLayout> layouts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);

        //Initialize variables
        layouts = new ArrayList<RelativeLayout>();

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
        for(ToDoList tdl : ToDoList.ToDoLists){
            taskView = (RelativeLayout) inflater.inflate(R.layout.activity_task_pagertemplate, null);
            taskEntries = (TableLayout) taskView.findViewById(R.id.tablelayout_task_entries);
            taskName = (EditText) taskView.findViewById(R.id.edittext_task_name);
            taskName.setText(tdl.name);
            layouts.add(taskView);

            byte[] checked = tdl.state;
            String[] entries = tdl.task;

            for(int i=0; i<checked.length; i++){
                entryRow = new TableRow(ctx);
                entryState = new CheckBox(ctx);
                entryName = createEntryName();
                entryName.setText(entries[i]);
                entryListeners(taskEntries, entryName, entryRow);
                if (checked[i] == 0)
                    entryState.setChecked(true);

                entryRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                entryRow.addView(entryState);
                entryRow.addView(entryName);
                taskEntries.addView(entryRow);
            }
            adapter.addView(taskView);
            adapter.notifyDataSetChanged();
            if (args != null && tdl == ToDoList.ToDoLists.get(index)){
                setCurrentPage(taskView);
            }
        }

    }

    //This method saves the current task to the database
    protected void saveList(){
        //Get data from the current displayed view
        int position = adapter.getItemPosition(getCurrentPage()) - 1;
        RelativeLayout currentView = (RelativeLayout) getCurrentPage();
        ToDoList tdl = null;
        if (position >= 0)
             tdl = ToDoList.ToDoLists.get(position);
        EditText header = (EditText) currentView.findViewById(R.id.edittext_task_name);
        TableLayout taskEntries = (TableLayout) currentView.findViewById(R.id.tablelayout_task_entries);
        int length = taskEntries.getChildCount();

        //When no task is found, then it creates a new one, otherwise the name of the task will be changed
        if (tdl == null) {
            if (tdl == null) {
                tdl = ToDoList.create(header.getText().toString(), length);
            } else if (length != tdl.length()) {
                tdl.setLength(length);
            }
        }
        else {
            tdl.name = header.getText().toString();
        }

        //In this loop task entries are created, or changed if they already exist
        for (int i = 0; i < length; i++) {
            View table_view = taskEntries.getChildAt(i);
            if (table_view instanceof TableRow) {
                TableRow row = (TableRow) table_view;

                View view_checkbox = row.getChildAt(0);
                View view_edittext = row.getChildAt(1);

                CheckBox checkbox = (CheckBox) view_checkbox;
                EditText edittext = (EditText) view_edittext;
                String entry = edittext.getText().toString();
                if (i >= tdl.length())
                    tdl.addEntry(entry, checkbox.isChecked());
                else
                    tdl.setAtPosition(i, entry, checkbox.isChecked());
            }

        }
        //Updates the task in the database and closes the activity
        tdl.update();
        finish();
    }

    //This method adds a new empty entry to the actual task
    protected void entryListeners(final TableLayout taskEntries, final EditText entry, final TableRow curRow){
        entry.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    TableRow entryRow = new TableRow(ctx);
                    entryRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    CheckBox entryState = new CheckBox(ctx);
                    EditText entryName = createEntryName();

                    entryRow.addView(entryState);
                    entryRow.addView(entryName);
                    taskEntries.addView(entryRow);

                    //Adds a new listener to the new created entry
                    entryListeners(taskEntries, entryName, entryRow);
                    return true;
                }

                else if(keyCode == KeyEvent.KEYCODE_DEL){
                    if (entry.getText().toString().matches(""))
                        taskEntries.removeView(curRow);
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
                this.finish();
                return true;
            case R.id.action_done:
                saveList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
