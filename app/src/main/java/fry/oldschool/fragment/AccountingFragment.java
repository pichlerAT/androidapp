package fry.oldschool.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fry.oldschool.R;

/**
 * Created by Edwin Pichler on 28.04.2016.
 */
public class AccountingFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_accounting, container, false);

        return rootView;
    }
}
