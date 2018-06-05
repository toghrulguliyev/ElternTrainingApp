package at.ac.univie.entertain.elterntrainingapp;

import android.app.FragmentManager;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        username = findViewById(R.id.textView);
//        username.setText(getUsername());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Eltern Training App");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.textView);
        username.setText(getUsername());
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //FragmentManager fm = getFragmentManager();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.exercises) {
            Fragment exercisesFragment = new ExercisesListFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_home, exercisesFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.messages) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_home, new FamilyRuleFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.settings) {
            //Fragment asf = new AccountSettingsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_home, new AccountSettingsFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.calendar) {
            Intent intent;
            intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout(){
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Const.TOKEN_KEY);
        editor.commit();
        editor.remove(Const.USERNAME_KEY);
        editor.commit();
        if (sharedPreferences.getString(Const.FCM_TOKEN, "") != null || !sharedPreferences.getString(Const.FCM_TOKEN, "").isEmpty()) {
            editor.remove(Const.FCM_TOKEN);
            editor.commit();
        }
        if (sharedPreferences.getString(Const.FAMILY_ID, "") != null || !sharedPreferences.getString(Const.FAMILY_ID, "").isEmpty()) {
            editor.remove(Const.FAMILY_ID);
            editor.commit();
        }
        editor.clear();
        editor.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public String getUsername() {
        sharedPreferences = this.getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.USERNAME_KEY,"");
    }

    public String getFamilyId() {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE, MODE_PRIVATE);
        return sharedPreferences.getString(Const.FAMILY_ID,"");
    }

}
