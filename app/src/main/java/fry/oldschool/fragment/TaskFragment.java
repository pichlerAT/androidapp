package fry.oldschool.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import fry.oldschool.activity.TaskCreateActivity;
import fry.oldschool.utils.App;
import fry.oldschool.R;
import fry.oldschool.adapter.TaskAdapter;
import fry.oldschool.utils.MySQLListener;
import fry.oldschool.utils.TaskList;

public class TaskFragment extends ListFragment{

    protected Context ctx = App.mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        setHasOptionsMenu(true);
        final ListView lv = (ListView) rootView.findViewById(android.R.id.list);

        final TaskAdapter adapter = new TaskAdapter(ctx, R.layout.fragment_task_listtemplate, App.TaskLists);
        lv.setAdapter(adapter);

        App.setMySQLListener(new MySQLListener() {
            @Override
            public void mysql_finished() {
                if (App.TaskLists.size() == 0){
                    TextView text = (TextView) rootView.findViewById(R.id.textview_task_message);
                    text.setText("No tasks found");

                }
                else{
                    adapter.notifyDataSetChanged();
                }

            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.task_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_add:
                Intent intent = new Intent(ctx, TaskCreateActivity.class);
                //startActivityForResult(intent, 10001);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}