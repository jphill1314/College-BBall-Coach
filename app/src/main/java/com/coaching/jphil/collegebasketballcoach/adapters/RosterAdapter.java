package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.fragments.PlayerInfoFragment;

/**
 * Created by jphil on 2/14/2018.
 */

public class RosterAdapter extends RecyclerView.Adapter<RosterAdapter.ViewHolder> {

    private Player[] players;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvPos, tvName, tvRating, tvPT;
        public ViewHolder(View view, final MainActivity activity){
            super(view);
            tvPos = view.findViewById(R.id.roster_position);
            tvName = view.findViewById(R.id.roster_name);
            tvRating =  view.findViewById(R.id.roster_rating);
            tvPT = view.findViewById(R.id.roster_pt);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putInt("player", getLayoutPosition());
                    PlayerInfoFragment frag = new PlayerInfoFragment();
                    frag.setArguments(args);

                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, frag)
                            .addToBackStack("playerFrag")
                            .commit();
                }
            });
        }
    }

    public RosterAdapter(Player[] players){
        this.players = players;
    }

    @Override
    public RosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roster_list_item, parent, false);

        ViewHolder vh = new ViewHolder(view, (MainActivity)parent.getContext());
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.tvPos.setText(players[position].getPositionAbr());
        holder.tvName.setText(players[position].getfName() + " " + players[position].getlName());
        holder.tvRating.setText(Integer.toString(players[position].getOverallRating()));
        holder.tvPT.setText(Integer.toString(players[position].getMinutes()));
    }

    @Override
    public int getItemCount(){
        return players.length;
    }
}
