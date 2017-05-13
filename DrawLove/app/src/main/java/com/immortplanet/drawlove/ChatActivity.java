package com.immortplanet.drawlove;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;

import android.support.v4.widget.DrawerLayout;
import android.view.WindowManager;
import android.widget.Toast;

import com.immortplanet.drawlove.fragment.AboutFragment;
import com.immortplanet.drawlove.fragment.ChatGroupFragment;
import com.immortplanet.drawlove.fragment.FriendFragment;
import com.immortplanet.drawlove.fragment.SettingFragment;

public class ChatActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private NavigationDrawerFragment mNavigationDrawerFragment;
    DrawerLayout drawer;

    boolean backPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //-- set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_chat);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawer);
    }

    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            if (backPressedOnce) {
                super.onBackPressed();
            }
            else{
                backPressedOnce = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backPressedOnce = false;
                    }
                }, 2000);
                Toast.makeText(ChatActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        if (position == 0){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new ChatGroupFragment())
                    .commit();
        }
        else if (position == 1){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new FriendFragment())
                    .commit();
        }
        else if (position == 2){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new SettingFragment())
                    .commit();
        }
        else if (position == 3) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new AboutFragment())
                    .commit();
        }
    }

}
