package com.example.myfitness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.myfitness.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity {
    public static int requestCode = 2456;
    private final List<PendingIntent> pendingIntentList = new ArrayList<>();
    private AlarmManager alarmManager;
    private CustomViewPager viewPager;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        //EventRepo.getInstance().loadInitialEvents();
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
        super.onResume();
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