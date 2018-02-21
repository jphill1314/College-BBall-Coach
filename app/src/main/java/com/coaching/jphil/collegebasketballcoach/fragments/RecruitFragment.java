package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.RecruitAdapter;

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
        adapter = new RecruitAdapter(activity.teams[activity.playerTeamIndex].getRecruits());
        recyclerView.setAdapter(adapter);

        return view;
    }

}
