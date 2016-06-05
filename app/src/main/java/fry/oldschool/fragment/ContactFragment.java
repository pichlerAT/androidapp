package fry.oldschool.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import fry.oldschool.R;
import fry.oldschool.activity.MainActivity;
import fry.oldschool.adapter.ContactAdapter;
import fry.oldschool.utils.App;
import fry.oldschool.utils.Contact;
import fry.oldschool.utils.ContactGroup;

/**
 * Created by Edwin Pichler on 28.04.2016.
 */
public class ContactFragment extends Fragment{

    protected SearchView mSearch;
    protected ContactAdapter adapter;
    protected ArrayList<Contact> childList;
    protected int request_number = 0;
    private TextView request_number_text = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        setHasOptionsMenu(true);

        childList = new ArrayList<>();
        request_number = App.conLis.contactRequests.size();

        ArrayList<ContactGroup> contactGroupList = new ArrayList<>(App.conLis.groups);

        final ExpandableListView lv = (ExpandableListView) rootView.findViewById(R.id.listview_contact_id);
        adapter = new ContactAdapter(App.mContext, contactGroupList, false);

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
            public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
                final SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
                switch(menuItem.getItemId()){
                    case R.id.action_contact_delete:
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(App.mContext);
                        deleteBuilder.setTitle(R.string.warning)
                            .setMessage(R.string.delete_message)
                            .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    adapter.removeChilds(childList);
                                    onDestroyActionMode(actionMode);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();

                        return true;
                    case R.id.action_assign_to_group:
                        View requestView = View.inflate(App.mContext, R.layout.fragment_contact_groupassign, null);
                        final LinearLayout layout = (LinearLayout) requestView.findViewById(R.id.linearlayout_contact_groupassign);

                        for(int i=0; i < App.conLis.groups.size()-1; i++){// -1 because user shouldn't assign contact to 'all contacts'
                            String groupName = App.conLis.groups.get(i).name;
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
                                    for (int j=0; j<layout.getChildCount(); j++){
                                        CheckBox cb = (CheckBox)layout.getChildAt(j);
                                        if(cb.isChecked()){
                                            App.conLis.groups.get(j).addContacts(childList);
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
                actionMode.finish();
                for (int i = 0; i < lv.getChildCount(); i++) {
                    View listItem = lv.getChildAt(i);
                    listItem.setBackgroundColor(ContextCompat.getColor(App.mContext, R.color.colorPrimary));
                }
            }
        });

        lv.setAdapter(adapter);

        for (int i=0; i<adapter.getGroupCount(); i++){
            lv.expandGroup(i);
        }

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

        inflater.inflate(R.menu.contact_menu, menu);
        View menuContactRequests = menu.findItem(R.id.action_contact_requests).getActionView();
        request_number_text = (TextView) menuContactRequests.findViewById(R.id.textview_contact_requests_text);
        updateRequestCount(request_number);
        menuContactRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = MainActivity.fm.beginTransaction();
                //transaction.replace(R.id.frame_fragment_main, new ContactRequestFragment()).commit();
            }
        });

    }
    public void updateRequestCount(final int request_number) {
        if (request_number_text == null) return;

        if (request_number == 0)
            request_number_text.setVisibility(View.INVISIBLE);
        else {
            request_number_text.setVisibility(View.VISIBLE);
            request_number_text.setText(Integer.toString(request_number));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_send_request:
                final View requestView = View.inflate(App.mContext, R.layout.fragment_contact_dialog, null);
                TextView title = (TextView) requestView.findViewById(R.id.textview_contact_dialog_title);
                title.setText(R.string.contact_request);
                EditText mail = (EditText) requestView.findViewById(R.id.edittext_contact_email);
                mail.setHint(R.string.mail);
                AlertDialog.Builder requestBuilder = new AlertDialog.Builder(App.mContext);
                requestBuilder.setView(requestView)
                    .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String email = ((EditText) requestView.findViewById(R.id.edittext_contact_email)).getText().toString();
                            if (!email.isEmpty())
                                App.conLis.sendRequest(email);
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
                final View groupView = View.inflate(App.mContext, R.layout.fragment_contact_dialog, null);
                TextView titleGroup = (TextView) groupView.findViewById(R.id.textview_contact_dialog_title);
                titleGroup.setText(R.string.new_group);
                EditText group = (EditText) groupView.findViewById(R.id.edittext_contact_email);
                group.setHint(R.string.name_of_group);
                AlertDialog.Builder newGroupBuilder = new AlertDialog.Builder(App.mContext);
                newGroupBuilder.setView(groupView)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String groupName = ((EditText) groupView.findViewById(R.id.edittext_contact_email)).getText().toString();
                            App.conLis.createContactGroup(groupName);
                            adapter.notifyDataSetChanged();
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
