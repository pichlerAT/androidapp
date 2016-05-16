package fry.oldschool.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fry.oldschool.R;
import fry.oldschool.adapter.ContactAdapter;
import fry.oldschool.utils.App;

/**
 * Created by Edwin Pichler on 28.04.2016.
 */
public class ContactFragment extends Fragment {

    protected SearchView mSearch;
    protected ContactAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        setHasOptionsMenu(true);

        //Test data
        final List<String> header_list = new ArrayList<String>();
        List<String> friend_list = new ArrayList<String>();
        List<String> all_list = new ArrayList<String>();
        HashMap<String, List<String>> contact_list = new HashMap<>();
        header_list.add("Friends");
        header_list.add("All contacts");

        friend_list.add("Edwin Pichler");
        friend_list.add("Kevin Feierfeil");
        friend_list.add("Stefan Fragner");

        all_list.add("Edwin Pichler");
        all_list.add("Huan Son");
        all_list.add("Kevin Feierfeil");
        all_list.add("Pidsch Plees");
        all_list.add("Stefan Fragner");

        contact_list.put(header_list.get(0), friend_list);
        contact_list.put(header_list.get(1), all_list);

        final ExpandableListView lv = (ExpandableListView) rootView.findViewById(R.id.listview_contact_id);
        adapter = new ContactAdapter(App.mContext, header_list, contact_list);

        ViewTreeObserver vto = lv.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lv.setIndicatorBounds(lv.getRight()- pixelScale(40), lv.getWidth());
            }
        });
        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));

                if(expandableListView.isItemChecked(index)) {
                    expandableListView.setItemChecked(index, false);
                    view.setBackgroundColor(ContextCompat.getColor(App.mContext, R.color.colorPrimary));
                }
                else {
                    expandableListView.setItemChecked(index, true);
                    view.setBackgroundColor(ContextCompat.getColor(App.mContext, R.color.colorAccent));
                }

                return false;
            }
        });
        lv.setMultiChoiceModeListener(new ExpandableListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                final int checkedCount = lv.getCheckedItemCount();
                switch (checkedCount) {
                    case 0:
                        actionMode.setTitle(null);
                        break;
                    case 1:
                        actionMode.setTitle("1 contact selected");
                        break;
                    default:
                        actionMode.setTitle("" + checkedCount + " contacts selected");
                        break;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.contact_item_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                final SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
                switch(menuItem.getItemId()){
                    case R.id.action_contact_delete:
                        for(int i=checkedItems.size()-1 ;i>= 0;i--){

                            if(checkedItems.valueAt(i)){
                                //Remove item from Dataobject (Contact class) and call adapter.notifyDataSetChanged()
                            }
                        }
                        return true;
                    case R.id.action_assign_to_group:
                        View requestView = View.inflate(App.mContext, R.layout.fragment_contact_groupassign, null);
                        final LinearLayout layout = (LinearLayout) requestView.findViewById(R.id.linearlayout_contact_groupassign);

                        for(int i=0; i < adapter.getGroupCount()-1; i++){// -1 because user shouldn't assign contact to 'all contacts'
                            String groupName = adapter.getGroup(i).toString();
                            CheckBox cb = new CheckBox(App.mContext);
                            cb.setText(groupName);
                            layout.addView(cb);
                        }

                        AlertDialog.Builder requestBuilder = new AlertDialog.Builder(App.mContext);
                        requestBuilder.setTitle(R.string.assign_to_group)
                            .setView(requestView)
                            .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    List<String> newHeaderList = new ArrayList<String>();
                                    for (int j=0; j<layout.getChildCount(); i++){
                                        CheckBox cb = (CheckBox)layout.getChildAt(i);
                                        if(cb.isChecked()){
                                            newHeaderList.add(cb.getText().toString());
                                        }
                                    }
                                    for(int k=checkedItems.size()-1 ;k>= 0;k--){

                                        if(checkedItems.valueAt(k)){
                                            adapter.assignChildToGroups(newHeaderList, checkedItems.indexOfKey(k));
                                        }
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();

                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        lv.setAdapter(adapter);
        //Search listener for the listview
        SearchManager searchManager = (SearchManager) App.mContext.getSystemService(Context.SEARCH_SERVICE);
        mSearch = (SearchView) rootView.findViewById(R.id.searchview_contact_id);
        mSearch.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearch.setIconifiedByDefault(false);
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                adapter.filterContacts(search);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                adapter.filterContacts(search);
                return false;
            }
        });

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.contact_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_contact_request:
                View requestView = View.inflate(App.mContext, R.layout.fragment_contact_request, null);
                AlertDialog.Builder requestBuilder = new AlertDialog.Builder(App.mContext);
                requestBuilder.setTitle(R.string.contact_request)
                    .setView(requestView)
                    .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
                return true;
            case R.id.action_new_group:
                final View groupView = View.inflate(App.mContext, R.layout.fragment_contact_request, null);
                AlertDialog.Builder newGroupBuilder = new AlertDialog.Builder(App.mContext);
                newGroupBuilder.setTitle(R.string.new_group)
                    .setView(groupView)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String groupName = ((EditText) groupView.findViewById(R.id.edittext_contact_email)).getText().toString();
                            adapter.addGroup(groupName);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected int pixelScale(float pixels) {
        // Get the screen's density scale
        final float scale = App.mContext.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
}
