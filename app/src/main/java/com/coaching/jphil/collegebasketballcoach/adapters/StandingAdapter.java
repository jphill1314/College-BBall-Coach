package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.fragments.RosterFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by jphil on 2/17/2018.
 */

public class StandingAdapter extends RecyclerView.Adapter<StandingAdapter.ViewHolder> {

    private ArrayList<Team> standing;
    private Context context;
    private int type;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvPos, tvName, tvWins, tvLoses;
        View view;

        public ViewHolder(View view, final int type){
            super(view);

            tvPos = view.findViewById(R.id.standing_position);
            tvName = view.findViewById(R.id.standing_team);
            tvWins = view.findViewById(R.id.standing_conf);
            tvLoses = view.findViewById(R.id.standing_overall);
            this.view = view;
        }
    }

    public StandingAdapter(ArrayList<Team> teams, int type, Context context){
        standing = teams;
        this.context = context;
        this.type = type;
        if(type != -1) {
            generateStandings();
        }
        else{
            RPIRanking();
        }
    }

    @Override
    public StandingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.standings_list_item, parent, false);
        return new ViewHolder(view, type);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.tvPos.setText(String.format(Locale.US, "%d", position+1));
        holder.tvName.setText(standing.get(position).getFullName());
        if(type != -1) {
            int totalGames = (standing.size() * 2) - 2;
            holder.tvWins.setText(context.getResources().getString(R.string.record_string, standing.get(position).getConferenceWins(totalGames),
                    standing.get(position).getConferenceLoses(totalGames)));
        }
        else{
            holder.tvWins.setText(String.format(Locale.US,"%.4f", standing.get(position).getRPI()));
        }
        holder.tvLoses.setText((context.getResources().getString(R.string.record_string, standing.get(position).getWins(),
                standing.get(position).getLoses())));

        if(standing.get(position).isPlayerControlled()){
            holder.view.setBackgroundResource(standing.get(position).getColorLight());
        }
        else{
            holder.view.setBackgroundColor(Color.rgb(250,250,250));
        }

        if(type != -1) {
            final int pos = position;
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putInt("team", standing.get(pos).getId());
                    args.putInt("conf", type);
                    RosterFragment frag = new RosterFragment();
                    frag.setArguments(args);

                    ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, frag)
                            .commit();

                    ((MainActivity) context).homeButton.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return standing.size();
    }

    private void generateStandings(){
        int changes;
        int totalGames = (standing.size() * 2) - 2;

        do{
            changes = 0;
            for(int x = 0; x < standing.size() - 1; x++){
                for(int y = x + 1; y < standing.size(); y++) {
                    if(standing.get(x).getConferenceWinPercent(totalGames) < standing.get(y).getConferenceWinPercent(totalGames)){
                        Collections.swap(standing, x, y);
                    }
                    else if(standing.get(x).getConferenceWinPercent(totalGames) == standing.get(y).getConferenceWinPercent(totalGames)){
                        if(standing.get(x).getConferenceWins(totalGames) < standing.get(y).getConferenceWins(totalGames)){
                            Collections.swap(standing, x, y);
                        }
                        else if (standing.get(x).getConferenceWins(totalGames) == standing.get(y).getConferenceWins(totalGames)){
                            if(standing.get(x).getWinPercent() < standing.get(y).getWinPercent()) {
                                Collections.swap(standing, x, y);
                                changes++;
                            }
                            else if(standing.get(x).getWinPercent() == standing.get(y).getWinPercent()) {
                                if (standing.get(x).getWins() < standing.get(y).getWins()) {
                                    Collections.swap(standing, x, y);
                                    changes++;
                                }
                            }
                        }
                    }
                }
            }
        }while(changes != 0);
    }

    private void RPIRanking(){
        int changes;

        do{
            changes = 0;
            for(int x = 0; x < standing.size() - 1; x++){
                for(int y = x + 1; y < standing.size(); y++){
                    if(standing.get(x).getRPI() < standing.get(y).getRPI()){
                        Collections.swap(standing, x, y);
                        changes++;
                    }
                }
            }
        }while(changes != 0);
    }
}
