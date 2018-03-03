package com.coaching.jphil.collegebasketballcoach.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Recruit;

import java.util.ArrayList;

/**
 * Created by Jake on 2/20/2018.
 */

public class RecruitAdapter extends RecyclerView.Adapter<RecruitAdapter.ViewHolder> {

    ArrayList<Recruit> recruits;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvPosition, tvName, tvRating, tvInterest;
        public Button buttonRecruit, buttonCommit;

        public ViewHolder(View view) {
            super(view);

            tvPosition = view.findViewById(R.id.recruit_position);
            tvName = view.findViewById(R.id.recruit_name);
            tvRating = view.findViewById(R.id.recruit_rating);
            tvInterest = view.findViewById(R.id.recruit_interest);
            buttonRecruit = view.findViewById(R.id.button_recruit);
            buttonCommit = view.findViewById(R.id.button_commit);
        }
    }

    public RecruitAdapter(ArrayList<Recruit> recruits){
        this.recruits = recruits;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recruit_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvPosition.setText(recruits.get(position).getPositionAsString());
        holder.tvName.setText(recruits.get(position).getFullName());
        holder.tvRating.setText(getStarRating(recruits.get(position).getRating()));
        holder.tvInterest.setText(recruits.get(position).getInterest() + "");

        if(!recruits.get(position).getIsCommitted() && recruits.get(position).getInterest() >= 75){
            holder.buttonRecruit.setVisibility(View.GONE);
            holder.buttonCommit.setVisibility(View.VISIBLE);
            holder.buttonCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recruits.get(position).attemptToRecruit();
                    notifyDataSetChanged();
                }
            });
        }
        else if(recruits.get(position).getIsCommitted()){
            holder.buttonCommit.setVisibility(View.GONE);
            holder.buttonRecruit.setVisibility(View.INVISIBLE);
        }
        else {
            holder.buttonCommit.setVisibility(View.GONE);
            holder.buttonRecruit.setVisibility(View.VISIBLE);
            holder.buttonRecruit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recruits.get(position).attemptToRecruit();
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return recruits.size();
    }

    private String getStarRating(int rating){
        String stars;

        stars = Integer.toString(rating / 20);
        if(rating % 20 > 9){
            stars += ".5";
        }
        return stars;
    }
}
