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

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.RosterAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;

public class RosterFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    public RosterFragment() {
        // Required empty public constructor
    }

    public static RosterFragment newInstance() {
        RosterFragment fragment = new RosterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_roster, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.roster_list);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new RosterAdapter(getPlayers());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private Player[] getPlayers(){
        Player[] players = new Player[15];

        for(int i = 0; i < 15; i++){
            players[i] = new Player(""+i, "Player", (i+1)%5, i*6);
        }
        return players;
    }
}
