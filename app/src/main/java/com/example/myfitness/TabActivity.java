package com.example.myfitness;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.myfitness.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class TabActivity extends AppCompatActivity {

    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        viewPager.setCurrentItem(0, true);
        EventAlarmManager.getInstance().resetAlarm(this);
    }

    @Override
    protected void onDestroy() {
        EventAlarmManager.getInstance().removeAlarm(this);
        super.onDestroy();
    }
}