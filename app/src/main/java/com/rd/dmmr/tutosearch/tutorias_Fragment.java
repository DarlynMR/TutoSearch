package com.rd.dmmr.tutosearch;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class tutorias_Fragment extends Fragment {

    private TextView textView;

    public tutorias_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutorias_, container, false);
        textView = view.findViewById(R.id.text_plo);
        textView.setText(getArguments().getString("message"));
        return view;
    }

}
