package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.ScheduleAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {


    public ScheduleFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        TextView tvSeason = (TextView) view.findViewById(R.id.schedule_season);
        tvSeason.setText("2017-18 Season");

        MainActivity mainActivity = (MainActivity) getActivity();
        Log.v("tag", "size: " + mainActivity.teams[2].getGames().size());

        recyclerView = (RecyclerView) view.findViewById(R.id.schedule_list);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new ScheduleAdapter(mainActivity.teams[2].getGames());
        recyclerView.setAdapter(adapter);


        return view;
    }

}
