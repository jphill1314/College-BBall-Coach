package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.PlayerInfoAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerInfoFragment extends Fragment {


    public PlayerInfoFragment() {
        // Required empty public constructor
    }

    private int playerIndex;
    TextView name, rating, training;
    Spinner spinner;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    private MainActivity mainActivity;

    private Player player;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_info, container, false);
        mainActivity = (MainActivity) getActivity();

        Bundle args = getArguments();
        if(args != null){
            playerIndex = args.getInt("player");
        }
        player = mainActivity.currentTeam.getPlayers().get(playerIndex);

        name = view.findViewById(R.id.player_name);
        name.setText(getString(R.string.player_name_pos, player.getFullName(), player.getPositionAbr()));

        rating = view.findViewById(R.id.rating_at_pos);
        rating.setText(getString(R.string.rating_at_pos, player.calculateRatingAtPosition(1), player.calculateRatingAtPosition(2),
                player.calculateRatingAtPosition(3), player.calculateRatingAtPosition(4), player.calculateRatingAtPosition(5)));
        training = view.findViewById(R.id.training_as);
        training.setText(getString(R.string.training_desc, getResources().getStringArray(R.array.training_types)[player.getTrainingAs()]));

        spinner = view.findViewById(R.id.player_spinner);
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.player_info_spinner));
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeAdapter(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recyclerView = view.findViewById(R.id.player_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        adapter = new PlayerInfoAdapter(player, 0, getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }

    private void changeAdapter(int type){
        adapter = new PlayerInfoAdapter(player, type, getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
