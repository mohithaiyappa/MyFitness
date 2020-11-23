package com.example.myfitness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.example.myfitness.ui.main.SectionsPagerAdapter;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TabActivity extends AppCompatActivity {
    public static  int requestCode = 2456;
    private AlarmManager alarmManager;
    private List<PendingIntent> pendingIntentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        EventRepo.getInstance().loadInitialEvents();
    }

    @Override
    protected void onResume() {
       EventAlarmManager.getInstance().resetAlarm(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventAlarmManager.getInstance().removeAlarm(this);
        super.onDestroy();
    }
}