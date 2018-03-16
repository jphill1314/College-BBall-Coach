package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.AsyncTask;
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

    private SimAsync async;

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

        async = null;

        if(!mainActivity.currentTeam.isPlayerControlled()){
            simGame.setVisibility(View.GONE);
            newSeason.setVisibility(View.GONE);
            startTournament.setVisibility(View.GONE);
        }

        simGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainActivity.championship == null) {
                    if(async == null) {
                        async = new SimAsync(mainActivity.conferences, false);
                        async.execute();
                    }
                }
                else{
                    if(async == null) {
                        ArrayList<Conference> conf = new ArrayList<>();
                        conf.add(mainActivity.conferences.get(0));
                        async = new SimAsync(conf, true);
                        async.execute();
                    }
                }

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
                if(!mainActivity.currentConference.isInPostSeason()) {
                    if(async == null){
                        async = new SimAsync(mainActivity.conferences, true);
                        async.execute();
                    }
                }
                else{
                    int count = 0;
                    for(Conference c: mainActivity.conferences){
                        if(c.isSeasonFinished()){
                            count++;
                        }
                    }
                    if(count == mainActivity.conferences.size()){
                        mainActivity.generateNationalChampionship();
                    }
                }
                startTournament.setVisibility(View.GONE);
                simGame.setVisibility(View.VISIBLE);

                ScheduleAdapter adapt = (ScheduleAdapter)adapter;
                adapt.changeGames(getTeamSchedule(mainActivity.currentTeam));
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        if(async != null){
            async.cancel(true);
        }
    }

    private class SimAsync extends AsyncTask<String, Integer, Integer>{

        private ArrayList<Conference> conferences;
        private boolean simAll;

        public SimAsync(ArrayList<Conference> conferences, boolean simAll){
            this.conferences = conferences;
            this.simAll = simAll;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int index = 0;
            for(Conference c: conferences){
                index = simulateGames(c, c.getTeams().get(0), simAll);
                if(index > -1){
                    return index;
                }
            }
            return index;
        }

        @Override
        protected void onPostExecute(Integer results){
            if(results > -1){
                GameFragment frag = new GameFragment();
                Bundle args = new Bundle();
                args.putInt("game", results);

                frag.setArguments(args);

                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, frag)
                        .addToBackStack("games")
                        .commit();
            }
            async = null;
        }

        private Integer simulateGames(Conference conference, Team team, boolean simAll){
            if(!conference.isInPostSeason()) {
                for (Game game : conference.getMasterSchedule()) {
                    if (!game.isPlayed()) {
                        if (game.getHomeTeam().equals(team) || game.getAwayTeam().equals(team)) {
                            if(team.isPlayerControlled()){
                                for(int x = 0; x < conference.getMasterSchedule().size(); x++){
                                    if(game.equals(conference.getMasterSchedule().get(x))){
                                        return x;
                                    }
                                }
                            }
                            else{
                                game.simulateGame();
                            }
                            if(!simAll){
                                return -1;
                            }
                        } else {
                            game.simulateGame();
                        }
                    }
                }
            }
            else if(!conference.isSeasonFinished()){
                conference.generateTournament();
                return -2;
            }

            if(mainActivity.championship != null){
                mainActivity.championship.playNextRound();
                if(mainActivity.championship.hasChampion()) {
                    newSeason.setVisibility(View.VISIBLE);
                    simGame.setVisibility(View.INVISIBLE);
                }
                return -3;
            }

            simGame.setVisibility(View.INVISIBLE);
            startTournament.setVisibility(View.VISIBLE);
            return -100;
        }
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

        if(mainActivity.championship != null){
            for(Game game : mainActivity.championship.getGames()){
                if (game.getHomeTeam().getFullName().equals(team.getFullName()) || game.getAwayTeam().getFullName().equals(team.getFullName())) {
                    teamSchedule.add(game);
                }
            }
        }

        return teamSchedule;
    }
}
