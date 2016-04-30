package fry.oldschool;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Edwin Pichler on 28.04.2016.
 */
public class TaskFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);


        Button button_task_new_todolist = (Button) rootView.findViewById(R.id.button_task_new_todolist);
        button_task_new_todolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame_fragment_main, new TaskCreateFragment()).commit();
            }
        });

        return rootView;
    }
}
