package fry.oldschool.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by Edwin Pichler on 04.05.2016.
 */
public class TaskCreateAdapter extends PagerAdapter {

    private ArrayList<View> view_list = new ArrayList<View>();

    @Override
    public int getItemPosition(Object object) {
        int index = view_list.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = view_list.get(position);
        container.addView(v);
        return v;
    }

    @Override
    public int getCount() {
        return view_list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(view_list.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return setPageTitle(position);
    }

    public int addView(View v)
    {
        int position = view_list.size();
        view_list.add(position, v);
        return position;
    }

    public int removeView(ViewPager pager, View v){
        int position = view_list.indexOf(v);
        pager.setAdapter(null);
        view_list.remove(position);
        pager.setAdapter(this);
        return position;
    }

    public View getView(int position){
        return view_list.get(position);
    }

    public CharSequence setPageTitle(int position){
        RelativeLayout currentView = (RelativeLayout) getView(position);
        EditText header = (EditText) currentView.getChildAt(0);
        String headerText = header.getText().toString();
        if (headerText.matches(""))
            return "New task";

        return (CharSequence) headerText;
    }

}
