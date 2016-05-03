package fry.oldschool.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import fry.oldschool.utils.App;
import fry.oldschool.R;
import fry.oldschool.utils.ToDoList;

/**
 * Created by Edwin Pichler on 29.04.2016.
 */
public class TaskCreateFragment extends Fragment {

    Context ctx = App.getContext();

    ToDoList tdl;

    public static TaskCreateFragment newInstance(String header, byte[] checked, String[] entries){
        TaskCreateFragment fragment = new TaskCreateFragment();

        Bundle args = new Bundle();
        args.putString("header", header);
        args.putByteArray("checked", checked);
        args.putStringArray("entries", entries);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_create, container, false);
        final TableLayout tablelayout_task_entries = (TableLayout) rootView.findViewById(R.id.tablelayout_task_entries);
        final EditText edittext_task_name = (EditText) rootView.findViewById(R.id.edittext_task_name);

        Bundle args = getArguments();
        if (args != null){
            byte[] checked = args.getByteArray("checked");
            String[] entries = args.getStringArray("entries");

            edittext_task_name.setText(args.getString("header"));
            for(int i=0; i<checked.length; i++){
                CheckBox checkbox = new CheckBox(ctx);
                if (checked[i] == 1)
                    checkbox.setChecked(true);

                EditText edittext = new EditText(ctx);
                edittext.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                edittext.setText(entries[i]);

                TableRow row = new TableRow(ctx);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                row.addView(checkbox);
                row.addView(edittext);
            }
        }


        Button button_task_addentry = (Button) rootView.findViewById(R.id.button_task_addentry);
        button_task_addentry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = new TableRow(ctx);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                CheckBox checkbox = new CheckBox(ctx);
                EditText edittext = new EditText(ctx);
                edittext.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                edittext.setFocusableInTouchMode(true);
                edittext.requestFocus();

                row.addView(checkbox);
                row.addView(edittext);
                tablelayout_task_entries.addView(row, 0);
            }
        });

        Button button_task_save = (Button) rootView.findViewById(R.id.button_task_save);
        button_task_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int length = tablelayout_task_entries.getChildCount();
                if(tdl == null) {
                    tdl = ToDoList.create(edittext_task_name.getText().toString(), length);
                }else if(length != tdl.length()) {
                    tdl.setLength(length);
                }
                for (int i = 0; i < length; i++) {
                    View table_view = tablelayout_task_entries.getChildAt(i);
                    if (table_view instanceof TableRow) {
                        TableRow row = (TableRow) table_view;

                        View view_checkbox = row.getChildAt(0);
                        View view_edittext = row.getChildAt(1);

                        CheckBox checkbox = (CheckBox) view_checkbox;
                        EditText edittext = (EditText) view_edittext;
                        String entry = edittext.getText().toString();
                        // pssst, i changed something ;)
                        //Save 'entry' and 'entry_done' to MySQL
                        tdl.setAtPosition(i,entry,checkbox.isChecked());
                    }

                }
                tdl.update();

            }
        });

        return rootView;
    }

}
