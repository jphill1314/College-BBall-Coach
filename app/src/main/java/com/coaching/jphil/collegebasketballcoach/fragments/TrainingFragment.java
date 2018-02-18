package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {


    public TrainingFragment() {
        // Required empty public constructor
    }

    TextView tvClose, tvMid, tvLong, tvHandle, tvPass, tvScreen;
    TextView tvPost, tvPerim, tvOnBall, tvOffball, tvSteal, tvRebound;
    TextView tvStamina;

    SeekBar sClose, sMid, sLong, sHandle, sPass, sScreen;
    SeekBar sPost, sPerim, sOnBall, sOffBall, sSteal, sRebound;
    SeekBar sStamina;

    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        return view;
    }

}
