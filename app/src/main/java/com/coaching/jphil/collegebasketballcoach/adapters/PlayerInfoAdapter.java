package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

    public PlayerInfoAdapter(Player player, int type, Context context){
        this.player = player;
        this.type = type;
        getItems(context);
    }

    public PlayerInfoAdapter(ArrayList<String> items){
        this.items = items;
        type = -1;
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
                holder.tv.setText(items.get(position));
        }
    }


    @Override
    public int getItemCount() {
        if(items != null){
            return items.size();
        }
        return 0;
    }




}
