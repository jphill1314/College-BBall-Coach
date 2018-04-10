package com.coaching.jphil.collegebasketballcoach.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.fragments.GameFragment;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by jphil on 3/15/2018.
 */

public class GameRosterAdapter extends RecyclerView.Adapter<GameRosterAdapter.ViewHolder> {

    private ArrayList<Player> players;
    private GameFragment gameFragment;
    private ArrayList<Player> pendingSub;
    private boolean isPlayerTeam;
    private int index1;
    private int type;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView pos, name, prefPos, condition, rating, fouls;
        TextView stat1, stat2, stat3, stat4;
        View view;

        public ViewHolder(View view, int type){
            super(view);

            if(type == 0) {
                pos = view.findViewById(R.id.player_position);
                prefPos = view.findViewById(R.id.player_pref);
                condition = view.findViewById(R.id.player_condition);
                rating = view.findViewById(R.id.player_rating);
                fouls = view.findViewById(R.id.player_fouls);
            }
            else{
                stat1 = view.findViewById(R.id.stat_1);
                stat2 = view.findViewById(R.id.stat_2);
                stat3 = view.findViewById(R.id.stat_3);
                stat4 = view.findViewById(R.id.stat_4);
            }

            name = view.findViewById(R.id.player_name);
            this.view = view;
        }
    }

    public GameRosterAdapter(ArrayList<Player> players, int type, GameFragment gameFragment){
        this.players = players;
        pendingSub = new ArrayList<>(this.players);
        this.type = type;
        this.gameFragment = gameFragment;
        isPlayerTeam = true;

        index1 = -1;
    }

    public GameRosterAdapter(ArrayList<Player> players, int type){
        this.players = players;
        this.type = type;
        isPlayerTeam = false;

        index1 = -1;
        pendingSub = new ArrayList<>(this.players);
    }


    @Override
    public GameRosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view;
        if(type == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_roster_list_item, parent, false);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_roster_stats_list_item, parent, false);
        }
        return new ViewHolder(view, type);
    }

    private void bindType0(GameRosterAdapter.ViewHolder holder, int position){
        String ps;
        switch (position+1){
            case 1:
                ps = "PG";
                break;
            case 2:
                ps = "SG";
                break;
            case 3:
                ps = "SF";
                break;
            case 4:
                ps = "PF";
                break;
            case 5:
                ps = "C";
                break;
            default:
                ps = "-";
        }
        holder.pos.setText(ps);
        holder.prefPos.setText(pendingSub.get(position).getPositionAbr());

        int condition = 100 - pendingSub.get(position).getFatigue();
        holder.condition.setText("" + condition);

        if(position < 5){
            holder.rating.setText(pendingSub.get(position).calculateRatingAtPosition(position+1) + "");
        }
        else{
            holder.rating.setText(pendingSub.get(position).getOverallRating() + "");
        }
        holder.fouls.setText(""+pendingSub.get(position).getFouls());

        holder.view.setBackgroundColor(Color.rgb(250, 250,250));

        if(players.get(position).getFatigue() > 60){
            holder.condition.setTextColor(Color.rgb(255, 0, 0));
        }
        else if(players.get(position).getFatigue() > 40){
            holder.condition.setTextColor(Color.rgb(255, 255, 0));
        }
        else {
            holder.condition.setTextColor(Color.rgb(0, 255, 0));
        }
    }

    private void bindType1(GameRosterAdapter.ViewHolder holder, int position){
        holder.stat1.setText(pendingSub.get(position).getTwoPointShotMade() + "/" + pendingSub.get(position).getTwoPointShotAttempts());
        holder.stat2.setText(pendingSub.get(position).getThreePointShotMade() + "/" + pendingSub.get(position).getThreePointShotAttempts());
        holder.stat3.setText(pendingSub.get(position).getFreeThrowMade() + "/" + pendingSub.get(position).getFreeThrowAttempts());
        holder.stat4.setText(pendingSub.get(position).getAssists()+"");
    }

    private void bindType2(GameRosterAdapter.ViewHolder holder, int position){
        holder.stat1.setText(pendingSub.get(position).getoRebounds()+"");
        holder.stat2.setText(pendingSub.get(position).getdRebounds()+"");
        holder.stat3.setText(pendingSub.get(position).getSteals()+"");
        holder.stat4.setText(pendingSub.get(position).getTurnovers()+"");
    }

    @Override
    public void onBindViewHolder(GameRosterAdapter.ViewHolder holder, int position){
        if(type == 0){
            bindType0(holder, position);
        }
        else if(type == 1){
            bindType1(holder, position);
        }
        else if(type == 2){
            bindType2(holder, position);
        }

        if(players.get(position).equals(pendingSub.get(position)) && index1 != position){
            if(type == 0){
                holder.pos.setTextColor(Color.BLACK);
                holder.prefPos.setTextColor(Color.BLACK);
                holder.rating.setTextColor(Color.BLACK);
                holder.fouls.setTextColor(Color.BLACK);
            }
            else{
                holder.stat1.setTextColor(Color.BLACK);
                holder.stat2.setTextColor(Color.BLACK);
                holder.stat3.setTextColor(Color.BLACK);
                holder.stat4.setTextColor(Color.BLACK);
            }
            holder.name.setTextColor(Color.BLACK);
        }
        else{
            if(type == 0){
                holder.pos.setTextColor(Color.GRAY);
                holder.prefPos.setTextColor(Color.GRAY);
                holder.rating.setTextColor(Color.GRAY);
                holder.fouls.setTextColor(Color.GRAY);
            }
            else{
                holder.stat1.setTextColor(Color.GRAY);
                holder.stat2.setTextColor(Color.GRAY);
                holder.stat3.setTextColor(Color.GRAY);
                holder.stat4.setTextColor(Color.GRAY);
            }
            holder.name.setTextColor(Color.GRAY);
        }

        holder.name.setText(pendingSub.get(position).getInitialAndName());

        if(isPlayerTeam) {
            final int pos = position;
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(index1 == -1){
                        index1 = pos;
                    }
                    else{
                        if(index1 == pos){
                            index1 = -1;
                        }
                        else{
                            Collections.swap(pendingSub, index1, pos);
                            index1 = -1;
                            gameFragment.fab.setVisibility(View.VISIBLE);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public ArrayList<Player> getSubs(){
        return pendingSub;
    }

    @Override
    public int getItemCount(){
        return players.size();
    }
}
