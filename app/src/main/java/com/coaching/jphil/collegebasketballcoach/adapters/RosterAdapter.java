package com.coaching.jphil.collegebasketballcoach.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;

/**
 * Created by jphil on 2/14/2018.
 */

public class RosterAdapter extends RecyclerView.Adapter<RosterAdapter.ViewHolder> {

    private Player[] players;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvPos, tvName, tvRating, tvPT;
        public ViewHolder(View view){
            super(view);
            tvPos = (TextView) view.findViewById(R.id.roster_position);
            tvName = (TextView) view.findViewById(R.id.roster_name);
            tvRating = (TextView) view.findViewById(R.id.roster_rating);
            tvPT = (TextView) view.findViewById(R.id.roster_pt);
        }
    }

    public RosterAdapter(Player[] players){
        this.players = players;
    }

    @Override
    public RosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roster_list_item, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.tvPos.setText(Integer.toString(players[position].getPosition()));
        holder.tvName.setText(players[position].getfName() + " " + players[position].getlName());
        holder.tvRating.setText(Integer.toString(players[position].getOverallRating()));
        holder.tvPT.setText("25");
    }

    @Override
    public int getItemCount(){
        return players.length;
    }
}
