package fry.oldschool.adapter;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

import fry.oldschool.R;
import fry.oldschool.activity.TaskCreateActivity;
import fry.oldschool.data.ContactList;
import fry.oldschool.utils.App;
import fry.oldschool.data.Contact;
import fry.oldschool.data.ContactGroup;
import fry.oldschool.data.TaskList;
import fry.oldschool.data.TaskListEntry;

public class TaskAdapter extends ArrayAdapter<TaskList>{

    public ArrayList<TaskList> list;
    protected Context ctx;
    protected ArrayList<Contact> childList = new ArrayList<>();
    protected ArrayList<ContactGroup> groupList = new ArrayList<>();

    public TaskAdapter(Context context, int resourceID, ArrayList<TaskList> list){
        super(context, resourceID, list);
        this.list = list;
        this.ctx = context;
        for (int i = 0; i < list.size(); ++i){
            list.get(i).drag_id = i;
        }
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
        if (position < 0 || position >= list.size()) {
            return -1;
        }
        TaskList item = getItem(position);
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
        TextView header = (TextView) res.findViewById(R.id.textview_listtemplate_header);
        final TaskList item = getItem(position);
        final String headerText = item.name;
        header.setText(headerText);
        LinearLayout entries = (LinearLayout) res.findViewById(R.id.linearlayout_listtemplate_id);
        entries.removeAllViews(); //Change later, because this isn't the best solution

        if (item.length() > 0) {//item.length is the number of entries
            for (int i = 0; i < item.length(); i++) {
                final TaskListEntry entry = item.entries.get(i);
                CheckBox cb = new CheckBox(ctx);
                String cbText = item.getTaskName(i);
                cb.setText(cbText);
                cb.setChecked(item.isDone(i));
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        entry.change(b);
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
            done.setBackground(ContextCompat.getDrawable(App.mContext, R.drawable.ic_refresh));
        }
        else{
            res.setAlpha(1);
            done.setBackground(ContextCompat.getDrawable(App.mContext, R.drawable.ic_done));
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.isDone())
                    item.change(false);
                else
                    item.change(true);
                notifyDataSetChanged();
            }
        });

        final Button more = (Button) res.findViewById(R.id.button_listtemplate_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(App.mContext, more);
                popupMenu.getMenuInflater().inflate(R.menu.task_options_popup, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals(App.mContext.getResources().getString(R.string.edit))){
                            Intent intent = new Intent(ctx, TaskCreateActivity.class);
                            intent.putExtra("index", position);
                            ctx.startActivity(intent);
                        }
                        else if (menuItem.getTitle().equals(App.mContext.getResources().getString(R.string.delete))){
                            item.delete();
                            notifyDataSetChanged();
                        }

                        else if (menuItem.getTitle().equals(App.mContext.getResources().getString(R.string.share))) {
                            View taskContactList = View.inflate(App.mContext, R.layout.fragment_task_contact_list, null);
                            ExpandableListView lv = (ExpandableListView) taskContactList.findViewById(R.id.listview_task_contact_id);
                            final ContactAdapter adapter = new ContactAdapter(App.mContext, ContactList.groups, true);
                            lv.setAdapter(adapter);

                            for (int i=0; i<adapter.getGroupCount(); i++){
                                lv.expandGroup(i);
                            }

                            lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                @Override
                                public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                                    int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));

                                    if(expandableListView.isItemChecked(index)) {
                                        expandableListView.setItemChecked(index, false);
                                        view.setBackgroundColor(ContextCompat.getColor(App.mContext, R.color.colorPrimary));
                                        childList.remove(adapter.getChild(groupPosition, childPosition));
                                    }
                                    else {
                                        expandableListView.setItemChecked(index, true);
                                        view.setBackgroundColor(ContextCompat.getColor(App.mContext, R.color.colorAccent));
                                        childList.add(adapter.getChild(groupPosition, childPosition));
                                    }

                                    return false;
                                }
                            });
                            lv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                                @Override
                                public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                                    int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPosition));

                                    if(expandableListView.isItemChecked(index)) {
                                        expandableListView.setItemChecked(index, false);
                                        view.setBackgroundColor(ContextCompat.getColor(App.mContext, R.color.colorPrimary));
                                        groupList.remove(adapter.getGroup(groupPosition));
                                    }
                                    else {
                                        expandableListView.setItemChecked(index, true);
                                        view.setBackgroundColor(ContextCompat.getColor(App.mContext, R.color.colorAccent));
                                        groupList.add(adapter.getGroup(groupPosition));
                                    }

                                    return true;
                                }
                            });
                            lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                                @Override
                                public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                                }

                                @Override
                                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                                  return false;
                                }

                                @Override
                                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                                  return false;
                                }

                                @Override
                                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                                  return false;
                                }

                                @Override
                                public void onDestroyActionMode(ActionMode actionMode) {

                                }
                            });

                            AlertDialog.Builder builder = new AlertDialog.Builder(App.mContext);
                            builder.setView(taskContactList)
                                    .setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //item.addShare(childList); TODO Edwin: adapt
                                            for (ContactGroup group : groupList) {
                                                //item.addShare(group.contacts); TODO Edwin: adapt
                                            }
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                   .show();
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        return res;
    }
}
