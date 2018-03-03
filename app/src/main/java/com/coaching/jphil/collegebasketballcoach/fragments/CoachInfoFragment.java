package com.coaching.jphil.collegebasketballcoach.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Coach;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.Conference;


public class CoachInfoFragment extends Fragment {


    public CoachInfoFragment() {
        // Required empty public constructor
    }

    private int coachIndex;
    private TextView tvShot, tvBallControl, tvScreen, tvPositioning, tvOnBall, tvOffBall;
    private TextView tvRebound, tvSteal, tvCondition, tvGuard, tvBig, tvName;

    private ImageView star1, star2, star3, star4, star5;

    private MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            coachIndex = getArguments().getInt("coach");
        }

        activity = (MainActivity) getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coach_info, container, false);

        tvShot = view.findViewById(R.id.staff_shoot);
        tvBallControl = view.findViewById(R.id.staff_ball_control);
        tvScreen = view.findViewById(R.id.staff_screen);
        tvPositioning = view.findViewById(R.id.staff_positioning);
        tvOnBall = view.findViewById(R.id.staff_on_ball);
        tvOffBall = view.findViewById(R.id.staff_off_ball);
        tvRebound = view.findViewById(R.id.staff_rebound);
        tvSteal = view.findViewById(R.id.staff_steal);
        tvCondition = view.findViewById(R.id.staff_condition);
        tvGuard = view.findViewById(R.id.staff_guards);
        tvBig = view.findViewById(R.id.staff_bigs);
        tvName = view.findViewById(R.id.staff_name);

        star1 = view.findViewById(R.id.staff_star_1);
        star2 = view.findViewById(R.id.staff_star_2);
        star3 = view.findViewById(R.id.staff_star_3);
        star4 = view.findViewById(R.id.staff_star_4);
        star5 = view.findViewById(R.id.staff_star_5);

        setAttributes();

        return view;
    }

    private void setAttributes(){
        Coach coach = activity.currentTeam.getCoaches().get(coachIndex);

        tvShot.setText(getResources().getString(R.string.shot_teach, coach.getShotTeaching()));
        tvBallControl.setText(getResources().getString(R.string.ball_handle, coach.getBallControlTeaching()));
        tvScreen.setText(getResources().getString(R.string.screen_teach, coach.getScreenTeaching()));
        tvPositioning.setText(getResources().getString(R.string.positioning, coach.getDefPositionTeaching()));
        tvOnBall.setText(getResources().getString(R.string.on_ball_teach, coach.getDefOnBallTeaching()));
        tvOffBall.setText(getResources().getString(R.string.off_ball_teach, coach.getDefOffBallTeaching()));
        tvRebound.setText(getResources().getString(R.string.rebound_teach, coach.getReboundTeaching()));
        tvSteal.setText(getResources().getString(R.string.steal_teach, coach.getStealTeaching()));
        tvCondition.setText(getResources().getString(R.string.condition_teaching, coach.getConditioningTeaching()));
        tvGuard.setText(getResources().getString(R.string.work_guards, coach.getWorkingWithGuards()));
        tvBig.setText(getResources().getString(R.string.work_bigs, coach.getWorkingWithBigs()));
        tvName.setText(getResources().getString(R.string.coach_name_pos, coach.getFullName(), coach.getPositionAsString()));

        setStarRating(coach.getOverallRating());
    }

    private void setStarRating(int rating){
        switch (getStarRating(rating)){
            case "0.5":
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                break;
            case "1":
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                break;
            case "1.5":
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                break;
            case "2":
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                break;
            case "2.5":
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                break;
            case "3":
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                break;
            case "3.5":
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                break;
            case "4":
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                break;
            case "4.5":
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star5.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                break;
            case "5":
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star5.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                break;
        }
    }

    private String getStarRating(int rating){
        String stars;

        stars = Integer.toString(rating / 20);
        if(rating % 20 > 9){
            stars += ".5";
        }

        return stars;
    }

}
