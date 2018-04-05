package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;

//TODO: make this actually work

public class ColorSpinnerAdapter extends ArrayAdapter {

    private String[] colorNames;
    private LayoutInflater inflater;

    public ColorSpinnerAdapter(@NonNull Context context, int resource, String[] strings) {
        super(context, resource);
        inflater = LayoutInflater.from(context);
        colorNames = strings;
    }

    @Override
    public int getCount() {
        return 15;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.player_info_list_item, null);
        TextView tv = view.findViewById(R.id.player_info_tv);
        tv.setText("Team Color");
        tv.setBackgroundResource(getTeamColors(position));
        return view;
    }


    private int getTeamColors(int color){

        switch (color){
            case 0:
                return R.color.redPrimary;
            case 1:
                return R.color.pinkPrimary;
            case 2:
                return R.color.purplePrimary;
            case 3:
                return R.color.deepPurplePrimary;
            case 4:
                return R.color.indigoPrimary;
            case 5:
                return R.color.bluePrimary;
            case 6:
                return R.color.lightBluePrimary;
            case 7:
                return R.color.cyanPrimary;
            case 8:
                return R.color.tealPrimary;
            case 9:
                return R.color.greenPrimary;
            case 10:
                return R.color.lightGreenPrimary;
            case 11:
                return R.color.yellowPrimary;
            case 12:
                return R.color.orangePrimary;
            case 13:
                return R.color.deepOrangePrimary;
            case 14:
                return R.color.blueGreyPrimary;
        }

        return 1;
    }

}
