package com.asat.amesoft.asat.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asat.amesoft.asat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPassFragment extends Fragment {


    public NewPassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_pass, container, false);
    }

}