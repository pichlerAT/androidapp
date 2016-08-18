package com.frysoft.notifry.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frysoft.notifry.R;
import com.frysoft.notifry.activity.MainActivity;
import com.frysoft.notifry.adapter.ContactAdapter;
import com.frysoft.notifry.data.Contact;
import com.frysoft.notifry.data.ContactList;
import com.frysoft.notifry.data.MySQLListener;
import com.frysoft.notifry.utils.App;

import java.util.ArrayList;

/**
 * Created by Edwin Pichler on 28.04.2016.
 */
public class ContactFragment extends Fragment {

    protected AppCompatEditText mSearch;
    protected ContactAdapter adapter;
    protected ArrayList<Contact> childList;
    protected int request_number = 0;
    private TextView request_number_text = null;

    protected ExpandableListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        setHasOptionsMenu(true);

        childList = new ArrayList<>();
        request_number = ContactList.getNoRequests();

        //ArrayList<ContactGroup> contactGroupList = new ArrayList<>(App.conLis.groups);
        MainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.fab.setVisibility(View.INVISIBLE);
                MainActivity.fab.setClickable(false);
                ContactFABMenu();
            }
        });

        lv = (ExpandableListView) rootView.findViewById(R.id.listview_contact_id);
        adapter = new ContactAdapter();

        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));

                if (expandableListView.isItemChecked(index)) {
                    expandableListView.setItemChecked(index, false);
                } else {
                    expandableListView.setItemChecked(index, true);

                }

                return false;
            }
        });

        lv.setMultiChoiceModeListener(new ExpandableListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int index, long l, boolean b) {
                View v = lv.getChildAt(index);
                String[] positions = ((String) v.getTag()).split(";"); // TODO NullPointerException: siehe Discord (bei onLongTouch auf Gruppe)
                int groupPosition = Integer.parseInt(positions[0]);
                int childPosition = Integer.parseInt(positions[1]);
                //Vice versa because item is already checked before the if statement
                if (!lv.isItemChecked(index)) {
                    childList.remove(adapter.getChild(groupPosition, childPosition));
                } else {
                    childList.add(adapter.getChild(groupPosition, childPosition));
                }

                final int checkedCount = lv.getCheckedItemCount();
                switch (checkedCount) {
                    case 0:
                        actionMode.setTitle(null);
                        break;
                    case 1:
                        actionMode.setTitle("1 contact");
                        break;
                    default:
                        actionMode.setTitle("" + checkedCount + " contacts");
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

                switch (menuItem.getItemId()) {
                    case R.id.action_contact_delete:
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(App.getContext());
                        deleteBuilder.setTitle(R.string.warning)
                                .setMessage(R.string.delete_message)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        adapter.removeChilds(childList);
                                        onDestroyActionMode(actionMode);
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        onDestroyActionMode(actionMode);
                                    }
                                })
                                .show();

                        return true;
                    case R.id.action_assign_to_group:
                        View requestView = View.inflate(App.getContext(), R.layout.fragment_contact_groupassign, null);
                        final LinearLayout layout = (LinearLayout) requestView.findViewById(R.id.linearlayout_contact_groupassign);

                        for (int i = 0; i < ContactList.getNoGroups() - 1; i++) {// -1 because user shouldn't assign contact to 'all contacts'
                            String groupName = ContactList.getGroup(i).getName();
                            AppCompatCheckBox cb = new AppCompatCheckBox(App.getContext());
                            cb.setText(groupName);
                            layout.addView(cb);
                        }

                        AlertDialog.Builder requestBuilder = new AlertDialog.Builder(App.getContext());
                        requestBuilder.setTitle(R.string.assign_to_group)
                                .setView(requestView)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        for (int j = 0; j < layout.getChildCount(); j++) {
                                            CheckBox cb = (CheckBox) layout.getChildAt(j);
                                            if (cb.isChecked()) {
                                                ContactList.getGroup(j).addContacts(childList);
                                            }
                                            else {
                                                ContactList.getGroup(j).removeContacts(childList);
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                        onDestroyActionMode(actionMode);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        onDestroyActionMode(actionMode);
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
            }
        });
        lv.setAdapter(adapter);

        App.setMySQLListener(new MySQLListener() {
            @Override
            public void mysql_finished() {
                if (ContactList.isEmpty()) {
                    // Set 'No contacts found'
                } else {
                    adapter.notifyDataSetChanged();
                }

            }
        });

        expandList();

        //Search listener for the listview
        mSearch = (AppCompatEditText) rootView.findViewById(R.id.searchview_contact_id);
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filterContacts(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!MainActivity.fab.isShown()) {
            MainActivity.fab.show();
        }
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
                transaction.replace(R.id.frame_fragment_main, new ContactRequestFragment()).commit();
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

    private void expandList(){
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            lv.expandGroup(i);
        }
    }
    public void ContactFABMenu(){
        final FABDialog dialog = new FABDialog(App.getContext());
        // it remove the dialog title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set the laytout in the dialog
        dialog.setContentView(R.layout.fab_menu_contact);
        // set the background partial transparent
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        //window.setDimAmount(0f);
        WindowManager.LayoutParams param = window.getAttributes();
        // set the layout at right bottom
        param.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        // it dismiss the dialog when click outside the dialog frame
        dialog.setCanceledOnTouchOutside(true);

        FloatingActionButton contact_request = (FloatingActionButton) dialog.findViewById(R.id.fab_contact_request);
        contact_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View requestView = View.inflate(App.getContext(), R.layout.fragment_contact_dialog, null);
                TextView title = (TextView) requestView.findViewById(R.id.textview_contact_dialog_title);
                title.setText(R.string.contact_request);
                AppCompatEditText mail = (AppCompatEditText) requestView.findViewById(R.id.edittext_contact_email);
                mail.setHint(R.string.mail);
                AlertDialog.Builder requestBuilder = new AlertDialog.Builder(App.getContext());
                requestBuilder.setView(requestView)
                        .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String email = ((EditText) requestView.findViewById(R.id.edittext_contact_email)).getText().toString();
                                if (!email.isEmpty())
                                    ContactList.sendRequest(email);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

                dialog.dismiss();
            }
        });

        FloatingActionButton add_group = (FloatingActionButton) dialog.findViewById(R.id.fab_contact_add_group);
        add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.fab.setVisibility(View.VISIBLE);
                final View groupView = View.inflate(App.getContext(), R.layout.fragment_contact_dialog, null);
                TextView titleGroup = (TextView) groupView.findViewById(R.id.textview_contact_dialog_title);
                titleGroup.setText(R.string.new_group);
                AppCompatEditText group = (AppCompatEditText) groupView.findViewById(R.id.edittext_contact_email);
                group.setHint(R.string.name_of_group);
                AlertDialog.Builder newGroupBuilder = new AlertDialog.Builder(App.getContext());
                newGroupBuilder.setView(groupView)
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String groupName = ((EditText) groupView.findViewById(R.id.edittext_contact_email)).getText().toString();
                                adapter.addGroup(groupName);
                                expandList();

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

                dialog.dismiss();
            }
        });

        FloatingActionButton fab_cancel = (FloatingActionButton) dialog.findViewById(R.id.fab_contact_cancel);
        fab_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private class FABDialog extends Dialog {

        public FABDialog(Context context) {
            super(context);
        }

        @Override
        public void dismiss(){
            super.dismiss();
            MainActivity.fab.setVisibility(View.VISIBLE);
            MainActivity.fab.setClickable(true);
        }
    }



}