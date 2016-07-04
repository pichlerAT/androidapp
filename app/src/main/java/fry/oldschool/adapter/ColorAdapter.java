package fry.oldschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import fry.oldschool.R;
import fry.oldschool.utils.App;

/**
 * Created by Edwin Pichler on 02.07.2016.
 */
public class ColorAdapter extends ArrayAdapter<String> {

    protected String[] mColors;
    protected String mActiveColor;

    public ColorAdapter(Context context, int resource, String[] objects, String active_color) {
        super(context, resource, objects);
        this.mColors = objects;
        this.mActiveColor = active_color;
    }

    @Override
    public int getCount() {
        return mColors.length;
    }

    @Override
    public String getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View res;
        if (convertView == null){
            res = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_dialog_item, parent, false);
        }
        else{
            res = convertView;
        }

        ImageView color_item = (ImageView) res.findViewById(R.id.color_item);
        color_item.setBackgroundColor(Color.parseColor(mColors[position]));

        if (mActiveColor.toLowerCase().equals(mColors[position]))
            color_item.setImageResource(R.drawable.ic_done);

        return res;
    }
}
