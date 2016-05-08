package fry.oldschool.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

import fry.oldschool.activity.MainActivity;
import fry.oldschool.activity.TaskCreateActivity;
import fry.oldschool.utils.App;
import fry.oldschool.R;
import fry.oldschool.adapter.TaskAdapter;
import fry.oldschool.utils.MySQL;
import fry.oldschool.utils.MySQLListener;
import fry.oldschool.utils.ToDoList;

public class TaskFragment extends ListFragment{

    Context ctx = App.getContext();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        setHasOptionsMenu(true);
        final ListView lv = (ListView) rootView.findViewById(R.id.listview_task_id);

        final TaskAdapter adapter = new TaskAdapter(ctx, R.layout.fragment_task_listtemplate, ToDoList.createToDoLists());
        lv.setAdapter(adapter);

        MySQL.setListener(new MySQLListener() {
            @Override
            public void mysql_finished(String arg) {
                //TaskAdapter adapter = new TaskAdapter(ctx, R.layout.fragment_task_listtemplate, ToDoList.ToDoLists);
                if (arg.equals("tdl_load") || arg.equals("tdl_delete") || arg.equals("error")){
                    //lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else if (ToDoList.ToDoLists.size() == 0){
                    TextView text = new TextView(ctx);
                    text.setText("No tasks found");

                }

            }
        });
        ToDoList.loadToDoLists();

        return rootView;
    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            Fragment taskFragment = new TaskFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(taskFragment).attach(taskFragment).commit();
        }
    }
*/
    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
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
