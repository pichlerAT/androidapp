package fry.oldschool;

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

/**
 * Created by Edwin Pichler on 29.04.2016.
 */
public class TaskCreateFragment extends Fragment {

    Context ctx = App.getContext();

    ToDoList tdl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_create, container, false);
        final TableLayout tablelayout_task_entries = (TableLayout) rootView.findViewById(R.id.tablelayout_task_entries);

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
                tdl = ToDoList.create("name",length);
                for (int i = 0; i < length; i++) {
                    View table_view = tablelayout_task_entries.getChildAt(i);
                    if (table_view instanceof TableRow) {
                        TableRow row = (TableRow) table_view;

                        View view_checkbox = row.getChildAt(0);
                        View view_edittext = row.getChildAt(1);

                        CheckBox checkbox = (CheckBox) view_checkbox;
                        EditText edittext = (EditText) view_edittext;
                        String entry = edittext.getText().toString();
                        byte entry_done = 0;
                        if (checkbox.isChecked()) {
                            entry_done = 1;
                        }
                        //Save 'entry' and 'entry_done' to MySQL
                        tdl.setAtPosition(i,entry,entry_done);
                    }

                }

            }
        });

        return rootView;
    }

}
