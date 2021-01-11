package com.example.myfitness.video_screen;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfitness.R;
import com.example.myfitness.model.Event;
import com.example.myfitness.model.EventVideoDetails;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class VideoViewActivity extends AppCompatActivity {
    private final List<String> urlArray = new ArrayList<>();
    private Event event;
    private VideoView videoView;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        videoView = findViewById(R.id.videoView);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        String eventJsonString = getIntent().getStringExtra("event");
        if (!(eventJsonString == null || eventJsonString.isEmpty())) {
            event = new Gson().fromJson(eventJsonString, Event.class);
        } else {
            Log.d("MyFitness", "event json data is empty ");
        }
        if (event != null) {
            playVideo();
        }
    }

    @Override
    protected void onResume() {
        if (event != null) {
            finishActivityAfterEventTime();
        }
        super.onResume();
    }

    private void playVideo() {
        //todo add local path to the list. basic is done
        for (EventVideoDetails eventVideo : event.getVideoArray()) {
            urlArray.add(eventVideo.getLocalPath());
        }

        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.setVideoURI(Uri.parse(urlArray.get(counter++)));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playVideoInLoop(urlArray.get(counter++));
                if (counter > urlArray.size() - 1) counter = 0;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

    }

    private void playVideoInLoop(String url) {
        Uri uri = Uri.parse(url); //Declare your url here.
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }

    private void finishActivityAfterEventTime() {
        //todo handle end time
        //todo check if you require to use end time or video length
        try {
            Handler handler = new Handler();
            // end time is in sent from backend
            //String endTimeStr = getEndTime(event.getStartTime(), event.getVideoArray().get(0).getVideoTime());//todo changed to video end time correct it later
            long endTime = getFinishTime(event.getEventDate(), event.getEndTime()).getTimeInMillis();//todo change to event start time
            long runTime = endTime - Calendar.getInstance().getTimeInMillis();
            handler.postDelayed(new Runnable() {
                public void run() {
                    videoView.pause();
                    Log.d("MyFitness", "run: " + runTime);
                    finish();
                }
            }, runTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Calendar getFinishTime(String stringDate, String stringTime) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(stringDate.trim() + " " + stringTime.trim());
        if (date == null) {
            throw new ParseException("parse error", 3);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    private String getEndTime(String startTime, String videoTime) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date1 = timeFormat.parse(startTime);
        Date date2 = timeFormat.parse(videoTime);

        long sum;
        sum = date1.getTime() + date2.getTime();

        return timeFormat.format(new Date(sum));
    }
}