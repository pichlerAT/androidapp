package fry.oldschool.adapter;

import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fry.oldschool.R;
import fry.oldschool.activity.TaskCreateActivity;
import fry.oldschool.utils.TaskList;

public class TaskAdapter extends ArrayAdapter<TaskList>{

    public ArrayList<TaskList> list = null;
    protected Context ctx;

    public TaskAdapter(Context context, int resourceID, ArrayList<TaskList> list){
        super(context, resourceID, list);
        this.list = list;
        this.ctx = context;
    }

    @Override
    public TaskList getItem(int position){
        return list.get(position);
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View res;
        if (convertView == null){
            res = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_listtemplate, parent, false);
        }
        else{
            res = convertView;
        }

        final TaskList item = getItem(position);

        TextView header = (TextView) res.findViewById(R.id.textview_listtemplate_header);
        final String headerText = item.name;
        header.setText(headerText);
        if (position == 0)
            header.setBackgroundColor(Color.rgb(20, 150, 20));
        else
            header.setBackgroundColor(Color.rgb(60, 60, 180));

        LinearLayout entries = (LinearLayout) res.findViewById(R.id.linearlayout_listtemplate_id);
        entries.removeAllViews(); //Change later, because this isn't the best solution
        for (int i=0; i<item.length(); i++){
            CheckBox cb = new CheckBox(ctx);
            String cbText = item.getTaskName(i);
            cb.setText(cbText);
            cb.setChecked(item.done(i));
            entries.addView(cb);
        }

        ImageButton delete = (ImageButton) res.findViewById(R.id.imagebutton_listtemplate_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.delete();
            }
        });

        ImageButton edit = (ImageButton) res.findViewById(R.id.imagebutton_listtemplate_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, TaskCreateActivity.class);
                intent.putExtra("index", position);
                ctx.startActivity(intent);
            }
        });

        //Set optical design elements for list item (view)
        res.setBackgroundResource(R.drawable.listview_style);

        return res;
    }
}
