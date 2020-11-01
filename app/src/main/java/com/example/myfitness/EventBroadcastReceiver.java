package com.example.myfitness;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class EventBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(isAppInForeground(context) ){
            String eventJsonString = intent.getStringExtra("event");

            Intent videoViewIntent = new Intent(context, VideoViewActivity.class);
            videoViewIntent.putExtra("event", eventJsonString);

            context.startActivity(videoViewIntent);
        }
    }

    public boolean isAppInForeground(Context mContext) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            return topActivity.getPackageName().equals(mContext.getPackageName());
        }

        return true;
    }
}
