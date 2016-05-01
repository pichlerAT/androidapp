package fry.oldschool;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Edwin Pichler on 01.05.2016.
 */
public class TaskAdapter extends ArrayAdapter<ToDoList>{

    public ArrayList<ToDoList> list;
    Context ctx;

    public TaskAdapter(Context context, int resourceID, ArrayList<ToDoList> list){
        super(context, resourceID, list);
        this.list = list;
        this.ctx = context;
    }

    @Override
    public ToDoList getItem(int position){
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View res;
        if (convertView == null){
            res = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_listtemplate, parent, false);
        }
        else{
            res = convertView;
        }

        ToDoList item = getItem(position);

        TextView header = (TextView) res.findViewById(R.id.textview_listtemplate_header);
        header.setText(item.name + " [" + item.id + "]");

        LinearLayout entries = (LinearLayout) res.findViewById(R.id.linearlayout_listtemplate_id);
        entries.removeAllViews(); //Change later, because this isn't the best solution
        for (int i=0; i<item.length(); i++){
            CheckBox cb = new CheckBox(ctx);
            cb.setText(item.task[i] + " (" + item.entry_id[i] + ")");
            if (item.state[i] == 1)
                cb.setChecked(true);
            else
                cb.setChecked(false);
            entries.addView(cb);
        }
        res.setBackgroundResource(R.drawable.listview_style);
        return res;
    }
}
