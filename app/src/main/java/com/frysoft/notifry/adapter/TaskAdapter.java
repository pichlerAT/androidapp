package com.frysoft.notifry.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frysoft.notifry.R;
import com.frysoft.notifry.activity.TaskCreateActivity;
import com.frysoft.notifry.data.Contact;
import com.frysoft.notifry.data.ContactGroup;
import com.frysoft.notifry.data.Share;
import com.frysoft.notifry.data.Tasklist;
import com.frysoft.notifry.data.TasklistEntry;
import com.frysoft.notifry.utils.App;
import com.frysoft.notifry.utils.SearchableList;

import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<Tasklist>{

    public ArrayList<Tasklist> list;
    protected Context ctx;
    protected SearchableList<Contact> childList = new SearchableList<>();
    protected ArrayList<ContactGroup> groupList = new ArrayList<>();

    public TaskAdapter(Context context, int resourceID, ArrayList<Tasklist> list){
        super(context, resourceID, list);
        this.list = list;
        this.ctx = context;
        for (int i = 0; i < list.size(); ++i){
            list.get(i).drag_id = i;
        }
    }

    @Override
    public Tasklist getItem(int position){
        return list.get(position);
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= list.size()) {
            return -1;
        }
        Tasklist item = getItem(position);
        return item.drag_id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
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
        final RelativeLayout color_header = (RelativeLayout) res.findViewById(R.id.relativelayout_fragment_task_header);
        TextView header = (TextView) res.findViewById(R.id.textview_listtemplate_header);
        final Tasklist item = getItem(position);
        final String headerText = item.getName();
        header.setText(headerText);
        LinearLayout entries = (LinearLayout) res.findViewById(R.id.linearlayout_listtemplate_id);
        entries.removeAllViews(); //Change later, because this isn't the best solution

        if (item.length() > 0) {//item.length is the number of entries
            for (int i = 0; i < item.length(); i++) {
                final TasklistEntry entry = item.getEntry(i);
                CheckBox cb = new CheckBox(ctx);
                String cbText = item.getTaskName(i);
                cb.setText(cbText);
                cb.setChecked(item.isDone(i));
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        entry.setState(b);
                    }
                });
                entries.addView(cb);
            }
            int padding = App.pixelToDPScale(10);
            entries.setPadding(padding, padding, padding, padding);
        }
        else{
            entries.setPadding(0, 0, 0, 0);
        }

        Button done = (Button) res.findViewById(R.id.button_listtemplate_done);
        if (item.isDone()){
            res.setAlpha(0.65f);
            done.setBackground(ContextCompat.getDrawable(App.getContext(), R.drawable.ic_refresh));
        }
        else{
            res.setAlpha(1);
            done.setBackground(ContextCompat.getDrawable(App.getContext(), R.drawable.ic_done));
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.isDone())
                    item.setState(false);
                else
                    item.setState(true);
                notifyDataSetChanged();
            }
        });

        final Button more = (Button) res.findViewById(R.id.button_listtemplate_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(App.getContext(), more);
                popupMenu.getMenuInflater().inflate(R.menu.task_options_popup, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals(App.getContext().getResources().getString(R.string.edit))){
                            Intent intent = new Intent(ctx, TaskCreateActivity.class);
                            intent.putExtra("index", position);
                            ctx.startActivity(intent);
                        }
                        else if (menuItem.getTitle().equals(App.getContext().getResources().getString(R.string.delete))){
                            item.delete();
                            notifyDataSetChanged();
                        }

                        else if (menuItem.getTitle().equals(App.getContext().getResources().getString(R.string.share))) {
                            View taskSharelist = View.inflate(App.getContext(), R.layout.fragment_task_sharelist, null);
                            ExpandableListView lv = (ExpandableListView) taskSharelist.findViewById(R.id.listview_task_sharelist);

                            ArrayList<ContactGroup> shared_groups = item.shares.getList();
                            final TaskShareAdapter adapter = new TaskShareAdapter(shared_groups);
                            lv.setAdapter(adapter);

                            for (int i=0; i<adapter.getGroupCount(); i++){
                                lv.expandGroup(i);
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(App.getContext());
                            builder.setView(lv)
                                    .setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            for (Contact contact : adapter.getSharedContactsView()){
                                                ((Share) contact).setPermission((byte) 1);
                                            }
                                            for (Contact contact : adapter.getSharedContactsEdit()){
                                                ((Share) contact).setPermission((byte) 2);
                                            }
                                            for (Contact contact : adapter.getSharedContactsMore()){
                                                ((Share) contact).setPermission((byte) 3);
                                            }
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        Button colors = (Button) res.findViewById(R.id.button_listtemplate_color);
        colors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View colorDialog = View.inflate(App.getContext(), R.layout.color_dialog, null);
                GridView color_gridview = (GridView) colorDialog.findViewById(R.id.gridview_color_dialog);
                ColorAdapter color_adapter = new ColorAdapter(App.getContext(), R.layout.color_dialog_item, App.getContext().getResources().getStringArray(R.array.colors),
                        String.format("#%06X", (0xFFFFFF & ((ColorDrawable)color_header.getBackground()).getColor())));

                color_gridview.setAdapter(color_adapter);
                AlertDialog.Builder builder = new AlertDialog.Builder(App.getContext());
                builder.setView(color_gridview)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                final Dialog dialog = builder.create();
                dialog.show();

                color_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ImageView color_item = (ImageView) view.findViewById(R.id.color_item);
                        color_header.setBackgroundColor(((ColorDrawable)color_item.getBackground()).getColor());
                        dialog.dismiss();
                    }
                });
            }
        });
        return res;
    }
}
