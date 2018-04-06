package com.coaching.jphil.collegebasketballcoach.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.RecruitAdapter;

import java.util.MissingFormatArgumentException;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecruitFragment extends Fragment {


    public RecruitFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recruit, container, false);
        MainActivity activity = (MainActivity) getActivity();

        recyclerView = view.findViewById(R.id.recruit_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);


        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        adapter = new RecruitAdapter(activity.currentTeam.getRecruits(), activity, prefs.getInt(getString(R.string.shared_pref_recruit_sort), 0));
        recyclerView.setAdapter(adapter);

        int[] needs = getNeeds();
        ((TextView)view.findViewById(R.id.position_needs)).setText(getString(R.string.position_needs,
                needs[0], needs[1], needs[2], needs[3], needs[4]));

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.sort_menu, menu);
    }

    @Override
    public void onStop(){
        super.onStop();

        SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.shared_pref_recruit_sort), ((RecruitAdapter)adapter).getSortType());
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.sort_position:
                ((RecruitAdapter)adapter).changeSortType(0);
                recyclerView.setAdapter(adapter);
                return true;
            case R.id.sort_rating:
                ((RecruitAdapter)adapter).changeSortType(1);
                recyclerView.setAdapter(adapter);
                return true;
            case R.id.sort_status:
                ((RecruitAdapter)adapter).changeSortType(2);
                recyclerView.setAdapter(adapter);
                return true;
            case R.id.sort_interest:
                ((RecruitAdapter)adapter).changeSortType(3);
                return true;
            case android.R.id.home:
                ((MainActivity)getActivity()).drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int[] getNeeds(){
        int[] needs = new int[5];
        MainActivity activity = (MainActivity)getActivity();
        for(int x = 0; x < 5; x++){
            needs[x] = activity.currentTeam.getNumberOfPlayersAtPosition(x+1, false);
        }
        return needs;
    }

}
