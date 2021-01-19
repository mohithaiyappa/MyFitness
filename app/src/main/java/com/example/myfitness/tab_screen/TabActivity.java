package com.example.myfitness.tab_screen;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfitness.R;
import com.example.myfitness.login_screen.MainActivity;
import com.example.myfitness.repository.EventRepo;
import com.example.myfitness.ui.main.SectionsPagerAdapter;
import com.example.myfitness.utils.CustomViewPager;
import com.example.myfitness.utils.EventAlarmManager;
import com.example.myfitness.utils.StringUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity {
    public static int requestCode = 2456;
    private final List<PendingIntent> pendingIntentList = new ArrayList<>();
    private AlarmManager alarmManager;
    private CustomViewPager viewPager;
    private TabLayout tabs;
    TabScreenSharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewModel = new ViewModelProvider(this).get(TabScreenSharedViewModel.class);


    }

    @Override
    protected void onResume() {
        //EventRepo.getInstance().loadInitialEvents();
        super.onResume();
        //read username from shared pref
        SharedPreferences sharedPref = TabActivity.this.getSharedPreferences(
                StringUtils.SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
        EventRepo.userName = sharedPref.getString(StringUtils.SHARED_PREFERENCE_USERNAME, "");

        viewModel.loadDataFromNetwork();
        EventAlarmManager.getInstance().resetAlarm(this);
        EventRepo.getInstance().loadUserDetails();
        EventRepo.getInstance().getMembershipStatusLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    //launch login activity and remove all other activity
                    EventRepo.getInstance().resetMembershipStatus();
                    Intent intent = new Intent(TabActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        EventAlarmManager.getInstance().removeAlarm(this);
        super.onDestroy();
    }

    public void disableTabScrolling() {
        viewPager.disablePaging();
    }

    public void enableTabScrolling() {
        viewPager.enablePaging();
    }

    public void updateEditOrCreateTabHeading(String headingText) {
        tabs.getTabAt(3).setText(headingText);
    }
}