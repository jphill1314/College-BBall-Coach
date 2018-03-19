package com.coaching.jphil.collegebasketballcoach.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;

/**
 * Created by Jake on 2/19/2018.
 */

public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    private String[] drawerItems;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvNav;

        public ViewHolder(View view, final MainActivity activity){
            super(view);

            tvNav = view.findViewById(R.id.drawer_text);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.updateFragment(getLayoutPosition());
                }
            });
        }
    }

    public NavDrawerAdapter(String[] items){
        drawerItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list_item, parent, false);
        return new ViewHolder(view, (MainActivity)parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvNav.setText(drawerItems[position]);
    }

    @Override
    public int getItemCount() {
        return drawerItems.length;
    }
}
