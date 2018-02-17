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
import android.widget.Button;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.ScheduleAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;

import java.lang.reflect.Array;
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
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        TextView tvSeason = view.findViewById(R.id.schedule_season);
        tvSeason.setText("2017-18 Season");

        mainActivity = (MainActivity) getActivity();

        recyclerView = view.findViewById(R.id.schedule_list);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new ScheduleAdapter(getTeamSchedule(mainActivity.teams[mainActivity.playerTeamIndex])
                , mainActivity.teams[mainActivity.playerTeamIndex]);
        recyclerView.setAdapter(adapter);

        Button simGame = view.findViewById(R.id.sim_game);
        simGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simulateGames(mainActivity.teams[mainActivity.playerTeamIndex]);

                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void simulateGames(Team team){
        for(Game game: mainActivity.masterSchedule) {
            if(!game.isPlayed()) {
                if(game.getHomeTeam().equals(team) || game.getAwayTeam().equals(team)) {
                    game.simulateGame();
                    game.getHomeTeam().playGame(game.homeTeamWin());
                    game.getAwayTeam().playGame(!game.homeTeamWin());
                    return;
                }
                else{
                    game.simulateGame();
                    game.getHomeTeam().playGame(game.homeTeamWin());
                    game.getAwayTeam().playGame(!game.homeTeamWin());
                }
            }
        }

    }

    private ArrayList<Game> getTeamSchedule(Team team){
        ArrayList<Game> teamSchedule = new ArrayList<Game>();


        for(Game game : mainActivity.masterSchedule){
            if(game.getHomeTeam().getFullName().equals(team.getFullName()) || game.getAwayTeam().getFullName().equals(team.getFullName())){
                teamSchedule.add(game);
            }
        }

        return teamSchedule;
    }
}
