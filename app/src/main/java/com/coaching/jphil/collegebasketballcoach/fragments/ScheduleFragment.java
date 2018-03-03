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
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.Conference;

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

    private Button simGame, newSeason, startTournament;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        TextView tvSeason = view.findViewById(R.id.schedule_season);
        tvSeason.setText(getResources().getString(R.string.season_name, 2017, 18));

        mainActivity = (MainActivity) getActivity();

        recyclerView = view.findViewById(R.id.schedule_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new ScheduleAdapter(getTeamSchedule(mainActivity.currentTeam)
                , mainActivity.currentTeam);
        recyclerView.setAdapter(adapter);

        simGame = view.findViewById(R.id.sim_game);
        newSeason = view.findViewById(R.id.start_new_season);
        startTournament = view.findViewById(R.id.start_tournament);

        if(!mainActivity.currentTeam.isPlayerControlled()){
            simGame.setVisibility(View.INVISIBLE);
            newSeason.setVisibility(View.INVISIBLE);
        }

        simGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simulateGames(mainActivity.currentTeam);

                ScheduleAdapter adapt = (ScheduleAdapter)adapter;
                adapt.changeGames(getTeamSchedule(mainActivity.currentTeam));
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

        startTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.currentConference.generateTournament();
                startTournament.setVisibility(View.GONE);
                simGame.setVisibility(View.VISIBLE);

                ScheduleAdapter adapt = (ScheduleAdapter)adapter;
                adapt.changeGames(getTeamSchedule(mainActivity.currentTeam));
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void simulateGames(Team team){
        for(Conference c: mainActivity.conferences) {
            if(c.isInPostSeason()) {
                if(c.isSeasonFinished()){
                    newSeason.setVisibility(View.VISIBLE);
                    simGame.setVisibility(View.INVISIBLE);
                }
                c.generateTournament();
                return;
                // TODO: add a check to see if the player controlled team is still playing
                // TODO: if no, then simulate the rest of the season on one click
            }
            else {
                for (Game game : c.getMasterSchedule()) {
                    if (!game.isPlayed()) {
                        if (game.getHomeTeam().equals(team) || game.getAwayTeam().equals(team)) {
                            if (game.simulateGame()) {
                                game.getHomeTeam().playGame(game.homeTeamWin());
                                game.getAwayTeam().playGame(!game.homeTeamWin());
                                for (Recruit recruit : mainActivity.currentTeam.getRecruits()) {
                                    recruit.setIsRecentlyRecruited(false);
                                }
                                return;
                            } else {
                                Toast toast = Toast.makeText(getContext(), getString(R.string.toast_minutes), Toast.LENGTH_LONG);
                                toast.show();
                                return;
                            }
                        } else {
                            game.simulateGame();
                            game.getHomeTeam().playGame(game.homeTeamWin());
                            game.getAwayTeam().playGame(!game.homeTeamWin());
                        }
                    }
                }
            }
        }
        simGame.setVisibility(View.INVISIBLE);
        startTournament.setVisibility(View.VISIBLE);

    }

    private ArrayList<Game> getTeamSchedule(Team team){
        ArrayList<Game> teamSchedule = new ArrayList<>();

        for(Conference c: mainActivity.conferences) {
            for (Game game : c.getMasterSchedule()) {
                if (game.getHomeTeam().getFullName().equals(team.getFullName()) || game.getAwayTeam().getFullName().equals(team.getFullName())) {
                    teamSchedule.add(game);
                }
            }
        }

        return teamSchedule;
    }
}
