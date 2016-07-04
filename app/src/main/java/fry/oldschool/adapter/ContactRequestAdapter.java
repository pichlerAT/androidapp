package fry.oldschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fry.oldschool.R;
import fry.oldschool.data.Contact;

/**
 * Created by Edwin Pichler on 21.05.2016.
 */
public class ContactRequestAdapter extends ArrayAdapter<Contact>{
    ArrayList<Contact> contactRequests;

    public ContactRequestAdapter(Context context, int resource, ArrayList<Contact> contactRequests) {
        super(context, resource, contactRequests);
        this.contactRequests = contactRequests;
    }

    @Override
    public Contact getItem(int position){
        return contactRequests.get(position);
    }

    @Override
    public int getCount(){
        return contactRequests.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View res;
        if (convertView == null){
            res = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_request_item, parent, false);
        }
        else{
            res = convertView;
        }

        final Contact sender = getItem(position);

        LinearLayout senderData = (LinearLayout) res.findViewById(R.id.linearlayout_contact_request_sender);
        TextView senderName = (TextView) senderData.getChildAt(0);
        TextView senderEmail = (TextView) senderData.getChildAt(1);

        senderName.setText(sender.getName());
        senderEmail.setText(sender.getEmail());

        Button accept = (Button) res.findViewById(R.id.imagebutton_contact_request_accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sender.accept();
            }
        });

        Button decline = (Button) res.findViewById(R.id.imagebutton_contact_request_decline);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sender.decline();
            }
        });

        return res;
    }
}
