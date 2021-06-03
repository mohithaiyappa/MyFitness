package com.example.myfitness.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ResetEventAlarmWorker extends Worker {

    public static final String TAG_RESET_EVENT_ALARM = "ResetEventAlarmTag";

    private final Context context;

    public ResetEventAlarmWorker(@NonNull Context ctx, @NonNull  WorkerParameters workerParams) {
        super(ctx, workerParams);
        context = ctx;
    }

    public static WorkRequest getNewResetEventAlarmWorkRequest(){

        //Take default Date and due Date
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        // Set Execution around 00:02:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 0);
        dueDate.set(Calendar.MINUTE, 2);
        dueDate.set(Calendar.SECOND, 0);

        //if reset time is already passed then add 24hrs
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }

        //calculate time diff for work request
        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();


        return new OneTimeWorkRequest
                .Builder(ResetEventAlarmWorker.class)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(TAG_RESET_EVENT_ALARM)
                .build();
    }

    @NonNull
    @Override
    public Result doWork() {

        //Reset Event Alarms
        EventAlarmManager.getInstance().resetAlarm(context.getApplicationContext());

        //new worker for tomorrow
        setNextWork();

        //return success
        return Result.success();
    }

    public void setNextWork(){
        WorkRequest workRequest = getNewResetEventAlarmWorkRequest();
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueue(workRequest);
    }
}
