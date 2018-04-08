package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;

public class ColorSpinnerAdapter extends ArrayAdapter {

    private String[] colorNames;
    private Context context;

    public ColorSpinnerAdapter(@NonNull Context context, int resource, String[] strings) {
        super(context, resource);
        colorNames = strings;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 15;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.player_info_list_item, parent, false);
        }
        TextView tv = convertView.findViewById(R.id.player_info_tv);
        tv.setText(context.getResources().getString(R.string.color_spinner_label));
        tv.setBackgroundResource(getTeamColors(position));
        return convertView;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.player_info_list_item, parent, false);
        }
        TextView tv = convertView.findViewById(R.id.player_info_tv);
        tv.setText(colorNames[position]);
        tv.setBackgroundResource(getTeamColors(position));
        return convertView;
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
