package com2027.killaz.kalorie.gitfit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;

import java.util.Calendar;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth mAuth;
    private Fragment homeFragment;
    private SensorManager sManager;
    private Sensor stepSensor;
    private int stepsToday;
    private DatabaseHelper db;
    private String username;
    private int challengeRemaining;
    private SharedPreferences sharedPref;

    /**
     * Main menu of the application.
     */
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
        mAuth = FirebaseAuth.getInstance();
        username = mAuth.getCurrentUser().getDisplayName();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = DatabaseHelper.getInstance(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //extra security for  rotation
        if (savedInstanceState == null) {
            //opens home_fragment on startup
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment(), "HOME")
                    .commit();
            navigationView.setCheckedItem(R.id.menu_home);
        } else {
            homeFragment = getSupportFragmentManager().getFragment(savedInstanceState, "HOME");
        }

        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
        stepsToday = db.getSteps(username, Calendar.getInstance().getTime());

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        int total = sharedPref.getInt(username + "total", 0);
        challengeRemaining = sharedPref.getInt(username + "remaining", total);
    }

    /**
     * Navigate to the appropriate fragment depending on user selection or to log out.
     * @param menuItem chosen to be displayed.
     * @return the chosen menuItem fragment or exit the app.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Save the steps in the db so that they are available for the next fragment.
        db.updateRecordSteps(username, Calendar.getInstance().getTime(), stepsToday);

        // Save the updated challenge remaining so it's also available.
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(username + "remaining", challengeRemaining);
        editor.apply();

        switch (menuItem.getItemId()) {
            // lets you go to the fragment
            // what it is called in the drawmenu.xml
            case R.id.menu_home:
                if (homeFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, homeFragment, "HOME")
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

            case R.id.menu_logout:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainMenu.this);
                builder1.setTitle("Log Out");
                builder1.setMessage("Are you sure you wish to log out and go back to the login screen?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                Intent backToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(backToLogin);
                                finish();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog logoutAlert = builder1.create();
                logoutAlert.show();
                break;

            case R.id.menu_logout_exit:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(MainMenu.this);
                builder2.setTitle("Log Out");
                builder2.setMessage("Are you sure you wish to log out and exit the app?");
                builder2.setCancelable(true);

                builder2.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                finish();
                            }
                        });

                builder2.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog logoutExitAlert = builder2.create();
                logoutExitAlert.show();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Fragment homeFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("HOME");
        if (homeFragment != null && homeFragment.isVisible()) {
            getSupportFragmentManager().putFragment(outState, "HOME", homeFragment);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    /**
     * Track today's steps.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        stepsToday++;
        challengeRemaining--;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.signOut();
    }
}

