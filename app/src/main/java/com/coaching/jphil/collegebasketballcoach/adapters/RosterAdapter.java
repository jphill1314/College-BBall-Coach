package com.coaching.jphil.collegebasketballcoach.adapters;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.fragments.PlayerInfoFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.RosterFragment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jphil on 2/14/2018.
 */

public class RosterAdapter extends RecyclerView.Adapter<RosterAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvPos, tvName, tvRating, tvPrefPos, tvYear;
        View view;
        public ViewHolder(View view){
            super(view);
            tvPos = view.findViewById(R.id.roster_position);
            tvName = view.findViewById(R.id.roster_name);
            tvRating =  view.findViewById(R.id.roster_rating);
            tvPrefPos = view.findViewById(R.id.roster_pref_pos);
            tvYear = view.findViewById(R.id.roster_year);
            this.view = view;
        }
    }

    private RosterFragment roster;
    private ArrayList<Player> players;
    private ArrayList<Player> subs;
    private boolean playerControlled;
    private MainActivity activity;
    private int player1;

    public RosterAdapter(ArrayList<Player> players, boolean playerControlled, RosterFragment roster){
        this.players = players;
        subs = new ArrayList<>(this.players);
        this.playerControlled = playerControlled;
        this.roster = roster;
        player1 = -1;
    }

    @Override
    public RosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roster_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        activity = (MainActivity)parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        if(position < players.size()) {
            if (subs.get(position).equals(players.get(position)) && player1 != position) {
                holder.tvName.setTextColor(Color.BLACK);
                holder.tvPos.setTextColor(Color.BLACK);
                holder.tvPrefPos.setTextColor(Color.BLACK);
                holder.tvYear.setTextColor(Color.BLACK);
                holder.tvRating.setTextColor(Color.BLACK);
            } else {
                holder.tvName.setTextColor(Color.GRAY);
                holder.tvPos.setTextColor(Color.GRAY);
                holder.tvPrefPos.setTextColor(Color.GRAY);
                holder.tvYear.setTextColor(Color.GRAY);
                holder.tvRating.setTextColor(Color.GRAY);
            }

            holder.tvName.setText(subs.get(position).getFullName());
            holder.tvPrefPos.setText(subs.get(position).getPositionAbr());
            holder.tvYear.setText(subs.get(position).getYearAsString());

            if (position < 5) {
                holder.tvRating.setText(subs.get(position).calculateRatingAtPosition(position + 1) + "");
            } else {
                holder.tvRating.setText(subs.get(position).getOverallRating() + "");
            }

            String pos;
            switch (position) {
                case 0:
                    pos = "PG";
                    break;
                case 1:
                    pos = "SG";
                    break;
                case 2:
                    pos = "SF";
                    break;
                case 3:
                    pos = "PF";
                    break;
                case 4:
                    pos = "C";
                    break;
                default:
                    pos = "-";
            }

            holder.tvPos.setText(pos);

            final int fpos = position;

            final GestureDetectorCompat detectorCompat = new GestureDetectorCompat(activity,
                    new GestureDetector.SimpleOnGestureListener(){
                        @Override
                        public boolean onDoubleTap(MotionEvent event){
                            Bundle args = new Bundle();
                            args.putInt("player", fpos);
                            PlayerInfoFragment frag = new PlayerInfoFragment();
                            frag.setArguments(args);

                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, frag)
                                    .addToBackStack("playerFrag")
                                    .commit();

                            return true;
                        }

                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent event){
                            if(playerControlled) {
                                if (player1 == -1) {
                                    player1 = fpos;
                                } else {
                                    if (player1 == fpos) {
                                        player1 = -1;
                                    } else {
                                        Collections.swap(subs, player1, fpos);
                                        roster.makeFABVisible();
                                        player1 = -1;
                                    }
                                }
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });


            holder.view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return detectorCompat.onTouchEvent(motionEvent);
                }
            });
        }
        else{
            holder.view.setOnTouchListener(null);
            holder.tvPos.setText("");
            holder.tvName.setText("");
            holder.tvRating.setText("");
            holder.tvPrefPos.setText("");
            holder.tvYear.setText("");
        }
    }

    public ArrayList<Player> getSubs(){
        players = new ArrayList<>(subs);
        return subs;
    }

    @Override
    public int getItemCount(){
        return players.size() + 1;
    }
}
