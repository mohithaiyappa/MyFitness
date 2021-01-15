package com.example.myfitness.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.example.myfitness.R;
import com.example.myfitness.model.VideoData;

public class VideoPopupDialog extends Dialog {

    private final Context mContext;
    private VideoData videoData;

    private TextView titleText, videoDetails, videoExplanation, addToEvent, downloadVideo, cancelText;
    private VideoView videoView;

    private MediaPlayer mediaPlayer;
    private MediaController mediaController;

    private View.OnClickListener videoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mediaController.show();

            if (videoView.isPlaying()) videoView.pause();
            else videoView.start();
        }
    };

    public VideoPopupDialog(@NonNull Context context, VideoData vData) {
        super(context);
        mContext = context;
        videoData = vData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_video_detailed_view);

        bindViews();
        bindData();
        setListeners();

    }

    private void bindViews() {
        titleText = findViewById(R.id.titleTextView);
        videoDetails = findViewById(R.id.videoDetailsText);
        videoExplanation = findViewById(R.id.videoExplanation);
        addToEvent = findViewById(R.id.addToEvent);
        downloadVideo = findViewById(R.id.downloadOrDelete);
        cancelText = findViewById(R.id.cancelButton);
        videoView = findViewById(R.id.videoView);
    }

    private void bindData() {
        titleText.setText(videoData.getVideoTitle());
        String detailsText = videoData.getIrName()
                + " . " + videoData.getCalorie()
                + " . " + videoData.getReleaseDate();
        videoDetails.setText(detailsText);
        videoExplanation.setText(videoData.getVideoExplanation());
        startVideo();
    }

    private void setListeners() {
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                VideoPopupDialog.this.dismiss();
            }
        });
    }

    private void startVideo() {
        mediaController = new MediaController(videoView.getContext());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.requestFocus();
        videoView.setVideoURI(Uri.parse(videoData.getVideoUrl()));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                //mediaController.show();
                mediaPlayer = mp;
                mediaController.show(1000);
                videoView.seekTo(3000);
                Toast.makeText(mContext, "video ready", Toast.LENGTH_SHORT).show();
            }
        });
        //videoView.start();
        videoView.setOnClickListener(videoClickListener);


    }

    public void setData(VideoData videoData) {
        this.videoData = videoData;
    }


}
