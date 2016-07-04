package fry.oldschool.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import fry.oldschool.R;
import fry.oldschool.activity.MainActivity;
import fry.oldschool.adapter.ContactRequestAdapter;
import fry.oldschool.data.ContactList;
import fry.oldschool.utils.App;
import fry.oldschool.data.MySQLListener;

/**
 * Created by Edwin Pichler on 19.05.2016.
 */
public class ContactRequestFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_contact_request, container, false);
        ListView lv = (ListView) rootView.findViewById(android.R.id.list);

        final ContactRequestAdapter adapter = new ContactRequestAdapter(App.mContext, R.layout.fragment_contact_request_item, ContactList.getRequests());
        lv.setAdapter(adapter);

        App.setMySQLListener(new MySQLListener() {
            @Override
            public void mysql_finished() {
                if (ContactList.getNoRequests() == 0){
                    TextView text = (TextView) rootView.findViewById(R.id.textview_contact_message);
                    text.setText("No contact requests available");

                }
                else{
                    adapter.notifyDataSetChanged();
                }

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.fab.isShown()){
            MainActivity.fab.hide();
        }
    }
}
