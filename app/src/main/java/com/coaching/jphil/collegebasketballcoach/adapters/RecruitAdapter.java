package com.coaching.jphil.collegebasketballcoach.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Coach;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Recruit;

import java.util.ArrayList;

/**
 * Created by Jake on 2/20/2018.
 */

public class RecruitAdapter extends RecyclerView.Adapter<RecruitAdapter.ViewHolder> {

    private ArrayList<Recruit> recruits;
    private MainActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvPosition, tvName, tvRating, tvInterest, tvInform;
        View view;

        public ViewHolder(View view) {
            super(view);

            tvPosition = view.findViewById(R.id.recruit_position);
            tvName = view.findViewById(R.id.recruit_name);
            tvRating = view.findViewById(R.id.recruit_rating);
            tvInterest = view.findViewById(R.id.recruit_interest);
            tvInform = view.findViewById(R.id.is_recruited);
            this.view = view;
        }
    }

    public RecruitAdapter(ArrayList<Recruit> recruits, MainActivity activity){
        this.recruits = recruits;
        this.activity = activity;
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

         final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

         if(recruits.get(position).isRecruited() && !recruits.get(position).getIsCommitted()){
             builder.setTitle(R.string.warn_unrecruit);
             builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                     for(Coach c: activity.getPlayerTeam().getCoaches()){
                         if(c.getRecruits() != null) {
                             if (c.getRecruits().contains(recruits.get(position))) {
                                 c.removeRecruit(recruits.get(position));
                                 notifyDataSetChanged();
                                 break;
                             }
                         }
                     }
                 }
             });

             builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {

                 }
             });

             holder.view.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     builder.show();
                 }
             });

             for(Coach c : activity.getPlayerTeam().getCoaches()) {
                 if (c.getRecruits() != null) {
                     for (Recruit r : c.getRecruits()){
                         if(r.equals(recruits.get(position))){
                             holder.tvInform.setText(activity.getResources().getString(R.string.stop_recruit, c.getFullName()));
                         }
                     }
                 }
             }
         }
         else if(!recruits.get(position).getIsCommitted()){
             builder.setTitle(R.string.alert_title)
                     .setItems(activity.getPlayerTeam().getCoachesNamesAndAbility(),
                             new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                     if(!activity.getPlayerTeam().getCoaches().get(i).addRecruit(recruits.get(position))){
                                         Toast.makeText(activity.getApplicationContext(), "This coach is recruiting too many players." +
                                         "\nPlease unassign a recruit from this coach before assigning a new one.", Toast.LENGTH_LONG).show();
                                     }
                                     else {
                                         notifyDataSetChanged();
                                     }
                                 }
                             });

             holder.tvInform.setText(activity.getResources().getString(R.string.recruit));
             holder.view.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     builder.show();
                 }
             });
         }
         else{
             holder.tvInform.setText(activity.getResources().getString(R.string.committed));
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
