package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jphil on 3/20/2018.
 */

public class PlayerInfoAdapter extends RecyclerView.Adapter<PlayerInfoAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv;

        public ViewHolder(View view){
            super(view);
            tv = view.findViewById(R.id.player_info_tv);
        }
    }


    Player player;
    int type;
    ArrayList<String> items;
    ArrayList<Integer> gameIds;
    ArrayList<Game> games;
    MainActivity activity;

    public PlayerInfoAdapter(Player player, int type, Context context){
        this.player = player;
        this.type = type;
        getItems(context);
    }

    public PlayerInfoAdapter(ArrayList<String> items, ArrayList<Game> games, ArrayList<Integer> gameIds, MainActivity activity){
        this.items = items;
        type = -1;
        this.games = games;
        this.gameIds = gameIds;
        this.activity = activity;
    }

    private void getItems(Context context){
        switch (type){
            case 0:
                items = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.off_attributes)));
                break;
            case 1:
                items = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.def_attributes)));
                break;
            case 2:
                items = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.other_attributes)));
                break;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.player_info_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (type){
            case 0:
                holder.tv.setText(items.get(position) + " " + player.getOffensiveAttributes()[position]);
                break;
            case 1:
                holder.tv.setText(items.get(position) + " " + player.getDefensiveAttributes()[position]);
                break;
            case 2:
                holder.tv.setText(items.get(position) + " " + player.getOtherAttributes()[position]);
                break;
            case -1:
                if(isHomeGame(gameIds.get(position))) {
                    holder.tv.setText(activity.getString(R.string.game_name_home, getOpponentName(gameIds.get(position)),
                            items.get(position)));
                }
                else{
                    holder.tv.setText(activity.getString(R.string.game_name_away, getOpponentName(gameIds.get(position)),
                            items.get(position)));
                }
        }
    }

    private boolean isHomeGame(int id){
        if(games != null && activity != null){
            for(Game g: games){
                if(g.getId() == id){
                    if(g.getHomeTeam().equals(activity.currentTeam)){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private String getOpponentName(int id){
        if(games != null && activity != null){
            for(Game g: games){
                if(g.getId() == id){
                    if(g.getHomeTeam().equals(activity.currentTeam)){
                        return g.getAwayTeamName();
                    }
                    else{
                        return g.getHomeTeamName();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if(items != null){
            return items.size();
        }
        return 0;
    }




}
