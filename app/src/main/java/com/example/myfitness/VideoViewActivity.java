package com.example.myfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class VideoViewActivity extends AppCompatActivity {
    private Event event;
    private VideoView videoView;
    private int counter =0;

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

        List<String> urlArray = new ArrayList<>();
        String URL_1 = "https://ovp-vod.smartstream.ne.jp/hoankikaku/output/vod/ca5862191b9c4e12b16e36395c6248b8/78e3a5c478d84d24b27eabd6c4d96eb9/manifest.mp4";
        String URL_2 ="https://ovp-vod.smartstream.ne.jp/hoankikaku/output/vod/c6d28fa266bf458a85250432637c2ae8/aaa28206f8f84d12b494df39a7aaced9/9xNzN3ZzE6QTqra_YBMSWoxOEDdhm4EE.mp4";
        String URL_3 = "https://ovp-vod.smartstream.ne.jp/hoankikaku/output/vod/ca5862191b9c4e12b16e36395c6248b8/78e3a5c478d84d24b27eabd6c4d96eb9/manifest.mp4";
        String URL_4 =  "https://ovp-vod.smartstream.ne.jp/hoankikaku/output/vod/6879b15b4e3a43e28518d75fe85820db/c8b306b9bf5647f9bb41298b5fdeeeca/manifest.mp4";
        urlArray.add(URL_1);
        urlArray.add(URL_2);
        urlArray.add(URL_3);
        urlArray.add(URL_4);


        videoView  = (VideoView)findViewById(R.id.videoView);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.setVideoURI(Uri.parse(urlArray.get(counter++)));
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playVideoInLoop(urlArray.get(counter++));
                if(counter > urlArray.size()-1) counter=0;
            }
        });

    }

    private void playVideoInLoop(String url){
        Uri uri = Uri.parse(url); //Declare your url here.
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }
}