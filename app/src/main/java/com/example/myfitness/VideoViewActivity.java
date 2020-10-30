package com.example.myfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

public class VideoViewActivity extends AppCompatActivity {
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        String eventJsonString = getIntent().getStringExtra("event");
        if(!(eventJsonString == null || eventJsonString.isEmpty())) {
            event = new Gson().fromJson(eventJsonString, Event.class);
        }else {
            Log.d("MyFitness229229", "event json data is empty ");
        }
        if(event !=null){
            playVideo();
        }
    }

    private void playVideo() {
        Log.d("MyFitness229229", "playing video "+event);

    }
}