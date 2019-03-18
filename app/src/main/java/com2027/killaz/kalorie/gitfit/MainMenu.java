package com2027.killaz.kalorie.gitfit;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, FirestoreService.class);
        startService(intent);
        setContentView(R.layout.activity_home);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //extra security for  rotation
        if (savedInstanceState == null) {
            //opens home_fragment on startup
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment(), "HOME")
                    .commit();
            navigationView.setCheckedItem(R.id.menu_home);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            // lets you go to the fragment
            // what it is called in the drawmenu.xml
            case R.id.menu_home:
                android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag("HOME");
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment, "HOME")
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment(), "HOME")
                            .commit();
                }

                break;

            case R.id.menu_tracker:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TrackerFragment()).commit();
                break;

            case R.id.menu_friends:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FriendsFragment()).commit();
                break;


            case R.id.menu_challenges:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ChallengesFragment()).commit();
                break;


            case R.id.menu_bmicalc:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new BmiFragment()).commit();
                break;

            case R.id.menu_leaderboards:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LeaderboardsFragment()).commit();
                break;

            case R.id.menu_settings:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

