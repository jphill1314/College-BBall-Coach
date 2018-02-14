package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;

import java.util.ArrayList;

/**
 * Created by jphil on 2/13/2018.
 */

public class DrawerArrayAdapter extends ArrayAdapter{

    String[] itemNames;

    public DrawerArrayAdapter(Context context, String[] itemNames){
        super(context, 0);
        this.itemNames = itemNames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String name = itemNames[position];

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.drawer_text);
        tvName.setText(name);

        return convertView;
    }



}
