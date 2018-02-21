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
import android.widget.Toast;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.ScheduleAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Recruit;
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

    private Button simGame, newSeason;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        TextView tvSeason = view.findViewById(R.id.schedule_season);
        tvSeason.setText(getResources().getString(R.string.season_name, 2017, 18));

        mainActivity = (MainActivity) getActivity();

        recyclerView = view.findViewById(R.id.schedule_list);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new ScheduleAdapter(getTeamSchedule(mainActivity.teams[mainActivity.playerTeamIndex])
                , mainActivity.teams[mainActivity.playerTeamIndex]);
        recyclerView.setAdapter(adapter);

        simGame = view.findViewById(R.id.sim_game);
        newSeason = view.findViewById(R.id.start_new_season);

        simGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simulateGames(mainActivity.teams[mainActivity.playerTeamIndex]);
                adapter.notifyDataSetChanged();
            }
        });

        newSeason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.startNewSeason();
                newSeason.setVisibility(View.GONE);
                simGame.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void simulateGames(Team team){
        for(Game game: mainActivity.masterSchedule) {
            if(!game.isPlayed()) {
                if(game.getHomeTeam().equals(team) || game.getAwayTeam().equals(team)) {
                    if(game.simulateGame()) {
                        game.getHomeTeam().playGame(game.homeTeamWin());
                        game.getAwayTeam().playGame(!game.homeTeamWin());
                        for(Recruit recruit: mainActivity.teams[mainActivity.playerTeamIndex].getRecruits()){
                            recruit.setIsRecentlyRecruited(false);
                        }
                        return;
                    }
                    else{
                        Toast toast = Toast.makeText(getContext(), getString(R.string.toast_minutes), Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                }
                else{
                    game.simulateGame();
                    game.getHomeTeam().playGame(game.homeTeamWin());
                    game.getAwayTeam().playGame(!game.homeTeamWin());
                }
            }
        }

        newSeason.setVisibility(View.VISIBLE);
        simGame.setVisibility(View.INVISIBLE);

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
