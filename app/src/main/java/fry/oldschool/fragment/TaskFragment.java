package fry.oldschool.fragment;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import fry.oldschool.utils.App;
import fry.oldschool.R;
import fry.oldschool.adapter.TaskAdapter;
import fry.oldschool.utils.ToDoList;


/**
 * Created by Edwin Pichler on 28.04.2016.
 */
public class TaskFragment extends ListFragment{

    Context ctx = App.getContext();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        setHasOptionsMenu(true);

         ToDoList.loadToDoLists();

        ListView lv = (ListView) rootView.findViewById(R.id.listview_task_id);
        TaskAdapter adapter = new TaskAdapter(ctx, R.layout.fragment_task_listtemplate, ToDoList.ToDoLists);
        lv.setAdapter(adapter);

        /*
        Button button_task_new_todolist = (Button) rootView.findViewById(R.id.button_task_new_todolist);
        button_task_new_todolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame_fragment_main, new TaskCreateFragment()).commit();
            }
        });
        */
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.task_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        FragmentManager fm = getFragmentManager();
        switch (item.getItemId()){
            case R.id.action_add:
                fm.beginTransaction().replace(R.id.frame_fragment_main, new TaskCreateFragment()).commit();
                return true;
            case R.id.action_settings:
                fm.beginTransaction().replace(R.id.frame_fragment_main, new TaskCreateFragment()).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
