package fry.oldschool;

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


/**
 * Created by Edwin Pichler on 28.04.2016.
 */
public class TaskFragment extends ListFragment{

    Context ctx = App.getContext();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        setHasOptionsMenu(true);

        ToDoList item1 = ToDoList.create("ToDo list #1", 2);
        item1.setAtPosition(0, "Buy 5 bananas", (byte) 1);
        item1.setAtPosition(1, "Wash the car", (byte) 0);

        ToDoList item2 = ToDoList.create("ToDo list #2", 4);
        item2.setAtPosition(0, "Call my mother", (byte) 0);
        item2.setAtPosition(1, "Go to the gym", (byte) 0);
        item2.setAtPosition(2, "Wanking on porns", (byte) 1);
        item2.setAtPosition(3, "Stefan lutscht Schwänze", (byte) 1);

        ArrayList<ToDoList> list = new ArrayList<ToDoList>();
        list.add(item1);
        list.add(item2);

        ListView lv = (ListView) rootView.findViewById(R.id.listview_task_id);
        TaskAdapter adapter = new TaskAdapter(ctx, R.layout.fragment_task_listtemplate, list);
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
