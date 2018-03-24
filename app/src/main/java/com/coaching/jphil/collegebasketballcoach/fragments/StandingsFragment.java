package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.StandingAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.Conference;

import java.util.ArrayList;

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

    private Spinner confNames;
    private ArrayList<String> names;

    private TextView confWL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_standings, container, false);
        mainActivity = (MainActivity) getActivity();

        confWL = view.findViewById(R.id.conf_wl);

        recyclerView = view.findViewById(R.id.standings_list);
        recyclerView.setHasFixedSize(true);

        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        adapter = new StandingAdapter(mainActivity.currentConference.getTeams(), mainActivity.conferences.indexOf(mainActivity.currentConference), getContext());
        recyclerView.setAdapter(adapter);

        confNames = view.findViewById(R.id.conference_name);
        names = new ArrayList<>();
        int selection = 0;
        for(int x = 0; x < mainActivity.conferences.size(); x++){
            names.add(mainActivity.conferences.get(x).getName());
            if(mainActivity.conferences.get(x).equals(mainActivity.currentConference)){
                selection = x;
            }
        }
        names.add("RPI Ranking");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, names);
        confNames.setAdapter(spinnerAdapter);
        confNames.setSelection(selection, false);
        confNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeView(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void changeView(int type){
        if(type < names.size() - 1) {
            adapter = new StandingAdapter(mainActivity.conferences.get(type).getTeams(), type, getContext());
            recyclerView.setAdapter(adapter);
            confWL.setText(getString(R.string.conf_record));
        }
        else{
            ArrayList<Team> allTeams = new ArrayList<>();
            for(Conference c: mainActivity.conferences){
                allTeams.addAll(c.getTeams());
            }

            adapter = new StandingAdapter(allTeams, -1, getContext());
            recyclerView.setAdapter(adapter);

            confWL.setText(getString(R.string.rpi));
        }
    }
}
