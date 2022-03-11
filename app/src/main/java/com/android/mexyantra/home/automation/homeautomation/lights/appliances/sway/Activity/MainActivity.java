package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments.AccountFragment;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments.HomeFragment;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments.StarredFragment;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Fragments.VideoSurveillanceFragment;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static boolean doubleOnBackPressed_PressedOnce = false;
    private int count = 0;
    private Toast toast;

    private HomeFragment homeFragment;
    private StarredFragment starredFragment;
    private VideoSurveillanceFragment videoSurveillanceFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instantiation();

        navigationView();
    }

    @Override
    public void onBackPressed(){
        tellFragments();
    }

    public void tellFragments(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment f : fragments){
            if(f instanceof HomeFragment){
                ((HomeFragment)f).onBackPressed();
                if(doubleOnBackPressed_PressedOnce && count>=1){
                    count = 0;
                    super.onBackPressed();
                }
                else if(doubleOnBackPressed_PressedOnce){
                    toast = Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT);
                    toast.show();
                    count++;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleOnBackPressed_PressedOnce = false;
                        count = 0;
                    }
                }, 2000);
            }
        }
        Log.d(TAG, "tellFragments: reached");
    }

    public void instantiation(){
        homeFragment = new HomeFragment();
        starredFragment = new StarredFragment();
        videoSurveillanceFragment = new VideoSurveillanceFragment();
        accountFragment = new AccountFragment();
        setFragment(homeFragment);
        toast = new Toast(this);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.homeFragment:
                    setFragment(homeFragment);
                    return true;

                case R.id.starredFragment:
                    setFragment(starredFragment);
                    return true;

                case R.id.nav_cam:
                    setFragment(videoSurveillanceFragment);
                    return true;

                case R.id.nav_account:
                    setFragment(accountFragment);
                    return true;
            }
            return false;
        }
    };

    private void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }

    public void navigationView(){
        // BottomNavigation
        setupBottomNavigationView();
        BottomNavigationView mMainNav = findViewById(R.id.main_nav);
        mMainNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.main_nav);
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(true);

    }

}
