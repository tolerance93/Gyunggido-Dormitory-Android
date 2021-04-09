package org.androidtown.dormitory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.androidtown.dormitory.fragments.BoardFragment;
import org.androidtown.dormitory.fragments.BusFragment;
import org.androidtown.dormitory.fragments.MenuFragment;
import org.androidtown.dormitory.R;
import org.androidtown.dormitory.fragments.RepairFragment;
import org.androidtown.dormitory.fragments.SleepFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private MenuFragment menuFragment;
    private BusFragment busFragment;
    private SleepFragment sleepFragment;
    private RepairFragment repairFragment;
    private BoardFragment boardFragment;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        menuFragment = new MenuFragment();
        busFragment = new BusFragment();
        sleepFragment = new SleepFragment();
        repairFragment = new RepairFragment();
        boardFragment = new BoardFragment();

        setFragment(menuFragment);

        mMainNav.setItemBackgroundResource(R.drawable.transparent);



        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_menu:
//                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(menuFragment);
                        return true;
                    case R.id.nav_bus:
//                        mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(busFragment);
                        return true;
                    case R.id.nav_sleep:
//                        mMainNav.setItemBackgroundResource(R.color.colorWhite);
                        setFragment(sleepFragment);
                        return true;
                    case R.id.nav_repair:
//                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(repairFragment);
                        return true;
                    case R.id.nav_board:
//                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(boardFragment);
                        return true;
                    default:
                        return false;

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            sendToStart();

        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(startIntent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
