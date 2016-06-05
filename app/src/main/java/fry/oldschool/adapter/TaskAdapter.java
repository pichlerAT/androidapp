package fry.oldschool.adapter;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

import fry.oldschool.R;
import fry.oldschool.activity.TaskCreateActivity;
import fry.oldschool.utils.App;
import fry.oldschool.utils.Contact;
import fry.oldschool.utils.ContactGroup;
import fry.oldschool.utils.TaskList;

public class TaskAdapter extends ArrayAdapter<TaskList>{

    public ArrayList<TaskList> list;
    protected Context ctx;
    protected ArrayList<Contact> childList = new ArrayList<>();
    protected ArrayList<ContactGroup> groupList = new ArrayList<>();

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

        TextView header = (TextView) res.findViewById(R.id.textview_listtemplate_header);
        final TaskList item = getItem(position);
        final String headerText = item.name;
        header.setText(headerText);
        LinearLayout entries = (LinearLayout) res.findViewById(R.id.linearlayout_listtemplate_id);
        entries.removeAllViews(); //Change later, because this isn't the best solution

        for (int i=0; i<item.length(); i++){
            CheckBox cb = new CheckBox(ctx);
            String cbText = item.getTaskName(i);
            cb.setText(cbText);
            cb.setChecked(item.done(i));
            entries.addView(cb);
        }

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
                            final ContactAdapter adapter = new ContactAdapter(App.mContext, App.conLis.groups, true);
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
                                            item.addShare(childList);
                                            for (ContactGroup group : groupList) {
                                                item.addShare(group.contacts);
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


        //Set optical design elements for list item (view)
        res.setBackgroundResource(R.drawable.listview_style);

        return res;
    }
}
