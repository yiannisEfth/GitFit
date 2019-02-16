package com2027.killaz.kalorie.gitfit;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //extra security for  rotation
        if (savedInstanceState == null ){
            //opens fragment1 on startup
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Fragment1()).commit();
            navigationView.setCheckedItem(R.id.dash);
        }



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            // lets you go to the fragment
            // what it is called in the drawmenu.xml
            case R.id.dash:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment1()).commit();
                break;

            case R.id.event:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment2()).commit();
                break;

            case R.id.search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment3()).commit();
                break;


            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment4()).commit();
                break;


            case R.id.activities:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment5()).commit();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

