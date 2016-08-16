package com.frysoft.notifry.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.frysoft.notifry.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edwin Pichler on 08.08.2016.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TextViewHolder> {
    private List<String> labels;

    public TestAdapter(int count, int events) {
        labels = new ArrayList<String>(count);
        for (int j = 0; j<= 6; j++){
            labels.add(getWeekday(j));
        }
        for (int i = 1; i <= count; ++i) {
            labels.add(String.valueOf(i));
        }
    }

    private String getWeekday(int day){
        switch(day){
            case 0:
                return "Mo";
            case 1:
                return "Tu";
            case 2:
                return "We";
            case 3:
                return "Th";
            case 4:
                return "Fr";
            case 5:
                return "Sa";
            case 6:
                return "Su";
            default:
                return "";
        }
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TextViewHolder holder, final int position) {
        final String label = labels.get(position);
        holder.textView.setText(label);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        holder.textView.getContext(), label, Toast.LENGTH_SHORT).show();
            }
        });
        if (position % 6 == 0){
        }
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_test);
        }
    }
}