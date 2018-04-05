package com.coaching.jphil.collegebasketballcoach.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.RosterAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class RosterFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    private FloatingActionButton confirmButton;

    public RosterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_roster, container, false);

        MainActivity mainActivity = (MainActivity)getActivity();

        Bundle args = getArguments();
        if(args != null){
            mainActivity.currentConference = mainActivity.conferences.get(args.getInt("conf"));
            for(Team t: mainActivity.currentConference.getTeams()){
                if(t.getId() == args.getInt("team")){
                    mainActivity.currentTeam = t;
                }
            }
            mainActivity.actionBar.setTitle(mainActivity.currentTeam.getFullName());
            mainActivity.updateColors();
        }

        recyclerView = view.findViewById(R.id.roster_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new RosterAdapter(mainActivity.currentTeam.getPlayers(), mainActivity.currentTeam.isPlayerControlled(), this);
        recyclerView.setAdapter(adapter);

        if(mainActivity.currentTeam.isPlayerControlled()){
            confirmButton = view.findViewById(R.id.confirm_roster_button);
            confirmButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(((MainActivity)getActivity()).currentTeam.getColorLight())));
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)getActivity()).currentTeam.makeSubs(((RosterAdapter)adapter).getSubs());
                    adapter.notifyDataSetChanged();
                    confirmButton.setVisibility(View.GONE);
                }
            });

        }

        return view;
    }

    public void makeFABVisible(){
        confirmButton.setVisibility(View.VISIBLE);
    }
}
