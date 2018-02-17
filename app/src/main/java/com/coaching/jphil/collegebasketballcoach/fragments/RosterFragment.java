package com.coaching.jphil.collegebasketballcoach.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.RosterAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;

import java.util.Random;

public class RosterFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    public RosterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_roster, container, false);

        MainActivity mainActivity = (MainActivity)getActivity();

        recyclerView = (RecyclerView) view.findViewById(R.id.roster_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new RosterAdapter(mainActivity.teams[2].getPlayers());
        recyclerView.setAdapter(adapter);

        return view;
    }

}
