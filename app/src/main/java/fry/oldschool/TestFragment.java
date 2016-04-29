package fry.oldschool;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Edwin Pichler on 29.04.2016.
 */
public class TestFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_test, container, false);

        Button button1 = (Button) rootView.findViewById(R.id.button_test_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.errorDialog("Warning", "This is a test message");
            }
        });

        Button button2 = (Button) rootView.findViewById(R.id.button_test_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new MySQL.Register()).execute("bigblack.cock@gmx.net","cocksforlive");
            }
        });

        Button button3 = (Button) rootView.findViewById(R.id.button_test_3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new MySQL.Todolist()).execute("9","task3");
            }
        });

        return rootView;
    }
}
