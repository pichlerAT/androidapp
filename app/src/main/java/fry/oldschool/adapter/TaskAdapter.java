package fry.oldschool.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.media.Image;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fry.oldschool.MainActivity;
import fry.oldschool.R;
import fry.oldschool.fragment.TaskCreateFragment;
import fry.oldschool.utils.App;
import fry.oldschool.utils.ToDoList;

public class TaskAdapter extends ArrayAdapter<ToDoList>{

    public ArrayList<ToDoList> list;
    protected Context ctx;
    protected FragmentManager fm;

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
    public View getView(final int position, View convertView, ViewGroup parent){
        View res;
        if (convertView == null){
            res = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_listtemplate, parent, false);
        }
        else{
            res = convertView;
        }

        final ToDoList item = getItem(position);

        TextView header = (TextView) res.findViewById(R.id.textview_listtemplate_header);
        final String headerText = item.name + " (" + item.id + ")";
        header.setText(headerText);

        LinearLayout entries = (LinearLayout) res.findViewById(R.id.linearlayout_listtemplate_id);
        entries.removeAllViews(); //Change later, because this isn't the best solution
        for (int i=0; i<item.length(); i++){
            CheckBox cb = new CheckBox(ctx);
            String cbText = item.task[i] + " (" + item.entry_id[i] + ") [" + item.user_id[i] + "]";
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
                Fragment fragment = new TaskCreateFragment();
                Bundle args = new Bundle();
                args.putString("header", headerText);
                args.putByteArray("checked", item.state);
                args.putStringArray("entries", item.task);
                fragment.setArguments(args);
                MainActivity.fm.beginTransaction().replace(R.id.frame_fragment_main, fragment).commit();
            }
        });

        //Set optical design elements for list item (view)
        res.setBackgroundResource(R.drawable.listview_style);

        return res;
    }
}
