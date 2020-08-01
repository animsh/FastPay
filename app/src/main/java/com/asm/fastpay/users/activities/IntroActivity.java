package com.asm.fastpay.users.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.asm.fastpay.R;
import com.asm.fastpay.ScreenItem;
import com.google.android.material.tabs.TabLayout;
import com.asm.fastpay.adapters.IntroViewPageAdapter;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    IntroViewPageAdapter introViewPageAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;
    Button btnSkip;
    private ViewPager screenPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make the activity on full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // check app is opened before or not

        if (restorePrefData()) {

            Intent mainActivity = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(mainActivity);
            finish();
        }


        setContentView(R.layout.activity_intro);

        // hide the action bar

        getSupportActionBar().hide();


        // ini views

        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);

        //fill list screen

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Need Groceries Now?", "Select wide range of products from fresh fruits to delicious snacks", R.drawable.shopping));
        mList.add(new ScreenItem("Hassle Free Payment", "Use scanner, Pay as per your convenience, we accept Paytm only", R.drawable.payment));
        mList.add(new ScreenItem("Fast Doorstep Deliveries", "Our delivery executive will deliver your order in under 24 hours", R.drawable.delivery));


        //setup viewpager

        screenPager = findViewById(R.id.screen_viewpager);
        introViewPageAdapter = new IntroViewPageAdapter(this, mList);
        screenPager.setAdapter(introViewPageAdapter);

        // setup tabLayout with viewPager

        tabIndicator.setupWithViewPager(screenPager);

        // next button click listener

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {

                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == mList.size() - 1) {

                    //TODO: SHOW THE GETSTARTED BUTTON NAD HIDE THE INDICATOR AND NEXT BUTTON

                    loadLastScreen();


                }

            }
        });


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //TODO: SHOW THE GETSTARTED BUTTON NAD HIDE THE INDICATOR AND NEXT BUTTON
                screenPager.setCurrentItem(mList.size());
                loadLastScreen();


            }
        });


        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size() - 1) {

                    loadLastScreen();
                } else
                    loadFirstScreen();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // Get Started button click listener

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open main activity

                Intent mainActivity = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(mainActivity);

                // save boolean value so that next time user open app, app will not show on-boarding screen

                savePrefsData();
                finish();


            }


        });

    }

    private boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isActivityOpenedBefore = pref.getBoolean("isIntroOpened", false);
        return isActivityOpenedBefore;


    }

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.commit();


    }

    private void loadLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnSkip.setVisibility(View.INVISIBLE);

        //TODO: ADD BUTTON ANIMATION
        //setup animation

        btnGetStarted.setAnimation(btnAnim);
    }

    private void loadFirstScreen() {

        btnNext.setVisibility(View.VISIBLE);
        btnGetStarted.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.VISIBLE);
        btnSkip.setVisibility(View.VISIBLE);

    }
}
