package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.StandingAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class StandingsFragment extends Fragment {


    public StandingsFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_standings, container, false);
        mainActivity = (MainActivity) getActivity();

        recyclerView = view.findViewById(R.id.standings_list);
        recyclerView.setHasFixedSize(true);

        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        adapter = new StandingAdapter(mainActivity.currentConference.getTeams());
        recyclerView.setAdapter(adapter);

        return view;
    }

}
