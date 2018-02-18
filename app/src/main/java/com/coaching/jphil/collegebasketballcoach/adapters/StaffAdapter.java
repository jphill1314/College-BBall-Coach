package com.coaching.jphil.collegebasketballcoach.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Coach;
import com.coaching.jphil.collegebasketballcoach.fragments.CoachInfoFragment;

/**
 * Created by jphil on 2/18/2018.
 */

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {

    private Coach[] coaches;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvPos, tvName, tvRating;

        public ViewHolder(View view, final MainActivity activity){
            super(view);
            tvPos = view.findViewById(R.id.staff_position);
            tvName = view.findViewById(R.id.staff_name);
            tvRating = view.findViewById(R.id.staff_rating);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CoachInfoFragment fragment = new CoachInfoFragment();
                    Bundle args = new Bundle();
                    args.putInt("coach", getLayoutPosition());
                    fragment.setArguments(args);

                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack("coaches")
                            .commit();
                }
            });
        }
    }

    public StaffAdapter(Coach[] coaches){
        this.coaches = coaches;
    }

    @Override
    public StaffAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_list_item, parent, false);
        return new ViewHolder(view, (MainActivity)parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.tvPos.setText(coaches[position].getPositionAsString());
        holder.tvName.setText(coaches[position].getFullName());
        holder.tvRating.setText(Integer.toString(coaches[position].getOverallRating()));
    }

    @Override
    public int getItemCount(){
        return coaches.length;
    }
}
