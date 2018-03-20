package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;

import java.util.ArrayList;

/**
 * Created by jphil on 3/20/2018.
 */

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        Spinner spinner;

        public ViewHolder(View view){
            super(view);

            name = view.findViewById(R.id.player_name);
            spinner = view.findViewById(R.id.player_training);
        }
    }

    private Context context;
    private ArrayList<Player> players;

    public TrainingAdapter(ArrayList<Player> players){
        this.players = players;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.training_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(context.getResources().getString(R.string.player_name_pos, players.get(position).getFullName(),
                players.get(position).getPositionAbr()));

        holder.spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
                context.getResources().getStringArray(R.array.training_types)));
        holder.spinner.setSelection(players.get(position).getTrainingAs(), false);
        final int pos = position;
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                players.get(pos).setTraining(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}

