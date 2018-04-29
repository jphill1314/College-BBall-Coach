package com.coaching.jphil.collegebasketballcoach.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.RosterAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.google.firebase.analytics.FirebaseAnalytics;


public class RosterFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    private FloatingActionButton confirmButton;
    private MainActivity mainActivity;

    private int confIndex;
    private int teamId;

    public RosterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_roster, container, false);

        mainActivity = (MainActivity)getActivity();

        Bundle args = getArguments();
        if(args != null){
            confIndex = args.getInt("conf");
            teamId = args.getInt("team");
        }

        confirmButton = view.findViewById(R.id.confirm_roster_button);

        recyclerView = view.findViewById(R.id.roster_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        setupAdapter();

        return view;
    }

    public void setupAdapter(){
        if(mainActivity.conferences != null) {
            mainActivity.currentConference = mainActivity.conferences.get(confIndex);
            for(Team t: mainActivity.currentConference.getTeams()){
                if(t.getId() == teamId){
                    mainActivity.currentTeam = t;
                }
            }
            mainActivity.actionBar.setTitle(mainActivity.currentTeam.getFullName());
            mainActivity.updateColors();

            adapter = new RosterAdapter(mainActivity.currentTeam.getPlayers(), mainActivity.currentTeam.isPlayerControlled(), this);
            recyclerView.setAdapter(adapter);

            if (mainActivity.currentTeam.isPlayerControlled()) {
                confirmButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(mainActivity.currentTeam.getColorLight())));
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mainActivity.currentTeam.makeSubs(((RosterAdapter) adapter).getSubs());
                        adapter.notifyDataSetChanged();
                        confirmButton.setVisibility(View.GONE);
                    }
                });

            }
        }
        else{
            mainActivity.loadData("load for roster");
        }
    }

    public void makeFABVisible(){
        confirmButton.setVisibility(View.VISIBLE);
    }
}
