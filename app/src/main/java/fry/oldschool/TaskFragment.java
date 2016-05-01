package fry.oldschool;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Created by Edwin Pichler on 28.04.2016.
 */
public class TaskFragment extends ListFragment{
    Context ctx = App.getContext();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);

        ToDoList item1 = ToDoList.create("ToDo list #1", 1);
        item1.setAtPosition(0, "Buy bananas from the store", (byte) 1);

        ToDoList item2 = ToDoList.create("ToDo list #2", 1);
        item2.setAtPosition(0, "Call my mum", (byte) 0);
        ToDoList[] list = {item1, item2};

        ListView lv = (ListView) rootView.findViewById(R.id.listview_task_id);
        TaskAdapter adapter = new TaskAdapter(list);
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
}
