package com.coaching.jphil.collegebasketballcoach;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.fragments.RosterFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.ScheduleFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StrategyFragment;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String[] mDrawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    public Team[] teams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerItems = getResources().getStringArray(R.array.drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.drawer_list_item, mDrawerItems);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateFragment(i);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
            .replace(R.id.content_frame, new RosterFragment())
            .commit();
            generateTeams();
        }
    }

    public void updateFragment(int position){
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();

        switch(position){
            case 0:
                t.replace(R.id.content_frame, new RosterFragment());
                break;
            case 1:
                t.replace(R.id.content_frame, new ScheduleFragment());
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                t.replace(R.id.content_frame, new StrategyFragment());
                break;
            case 5:
                break;
            case 6:
                break;
        }
        t.commit();
    }

    private void generateTeams(){
        String[] names = {"UNC", "Duke", "Wofford", "Furman", "Citadel", "Mercer"};
        String[] mascots = {"Tar Heels", "Blue Devils", "Terriers", "Paladins", "Bulldogs", "Bears"};
        teams = new Team[names.length];

        for(int i = 0; i < teams.length; i++){
            teams[i] = new Team(names[i], mascots[i], getPlayers(10));
        }

        for(int x = 0; x < teams.length; x++){
            for(int y = 0; y < teams.length; y++){
                if(x != y) {
                    Game game = new Game(teams[x], teams[y]);
                    teams[x].addGame(game);
                    teams[y].addGame(game);
                }
            }
        }
    }

    private Player[] getPlayers(int numPlayers){
        Player[] players = new Player[numPlayers];
        String[] lastNames = getResources().getStringArray(R.array.last_names);
        String[] firstNames = getResources().getStringArray(R.array.first_names);
        Random r = new Random();

        for(int i = 0; i < numPlayers; i++){
            players[i] = new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],(i%5) + 1, 70);
        }
        return players;
    }

}



