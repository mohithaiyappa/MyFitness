package com.example.myfitness.tab_screen;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.myfitness.R;
import com.example.myfitness.login_screen.MainActivity;
import com.example.myfitness.repository.EventRepo;
import com.example.myfitness.ui.main.SectionsPagerAdapter;
import com.example.myfitness.utils.CustomViewPager;
import com.example.myfitness.utils.EventAlarmManager;
import com.example.myfitness.utils.ResetEventAlarmWorker;
import com.example.myfitness.utils.StringUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

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
        checkIfPermissionIsNeeded();
        setupAutoAlarmWorker();


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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {

        //cancel all work when app closes
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.cancelAllWorkByTag(ResetEventAlarmWorker.TAG_RESET_EVENT_ALARM);

        EventAlarmManager.getInstance().removeAlarm(this);
        super.onDestroy();
    }

    private void checkIfPermissionIsNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission(); // Code for permission
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(TabActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(TabActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(TabActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(TabActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void setupAutoAlarmWorker(){

        //build work request
        WorkRequest workRequest = ResetEventAlarmWorker.getNewResetEventAlarmWorkRequest();

        //enqueue work
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(workRequest);
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