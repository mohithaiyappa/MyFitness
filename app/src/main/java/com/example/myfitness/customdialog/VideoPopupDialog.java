package com.example.myfitness.customdialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.example.myfitness.R;
import com.example.myfitness.model.Event;
import com.example.myfitness.model.EventVideoDetails;
import com.example.myfitness.model.VideoData;
import com.example.myfitness.repository.EventRepo;
import com.example.myfitness.tab_screen.TabActivity;
import com.example.myfitness.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoPopupDialog extends Dialog {

    private static final String TAB_NAME = "スケジュール作成";
    private final Context mContext;
    private VideoData videoData;

    static final int TIMEOUT_SECOND = 60000;
    static final String DOWNLOAD = "ダウンロード";
    static final String DELETE = "削除";

    private TextView titleText, videoDetails, videoExplanation, addToEvent, downloadVideo, cancelText;
    private VideoView videoView;
    private ImageButton fullScreenButton;

    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private final String user_id = EventRepo.userName;
    private ProgressDialog progressDialog;
    private boolean downloaded = false;
    private ImageView view;
    private TextView selectedText;

    private final Handler mHandler = new Handler();

    private Runnable hideMediaControllerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaController != null) mediaController.setVisibility(View.INVISIBLE);
        }
    };

    private View.OnClickListener videoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mHandler.removeCallbacks(hideMediaControllerRunnable);
            if (mediaController != null) {
                mediaController.setVisibility(View.VISIBLE);
                mHandler.postDelayed(hideMediaControllerRunnable, 4000);
            }


            /*mediaController.show();

            if (videoView.isPlaying()) videoView.pause();
            else videoView.start();*/
        }
    };

    public VideoPopupDialog(@NonNull Context context, VideoData vData, ImageView v, TextView selectedText) {
        super(context);
        mContext = context;
        videoData = vData;
        view = v;
        this.selectedText = selectedText;
        this.downloaded = EventRepo.downloadedVideosIds.contains(videoData.getVideoId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_video_detailed_view);

        setupProgressDialog();
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
        fullScreenButton = findViewById(R.id.fullscreenButton);
    }

    private void bindData() {
        titleText.setText(videoData.getVideoTitle());
        String detailsText = videoData.getIrName()
                + "  ·  " + videoData.getCalorie() + "kcal "
                + "  ·  " + "公開日" + videoData.getReleaseDate().replace("-", "/");
        videoDetails.setText(detailsText);
        String explanationText = videoData.getVideoExplanation();

        explanationText = explanationText.replace("\\n", " \n");
        videoExplanation.setText(explanationText);
        startVideo();
        if (downloaded) {
            downloadVideo.setText(DELETE);
            addToEvent.setEnabled(true);
        } else {
            downloadVideo.setText(DOWNLOAD);
            addToEvent.setEnabled(false);
        }

        downloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File[] storageDir = mContext.getExternalFilesDirs(null);
                if (storageDir.length == 1) {

                    //return for now show dialog in download task
                    AcknowledgementDialog noUsbAcknowledgementDialog = new AcknowledgementDialog(mContext,
                            StringUtils.CONNECT_USB_STORAGE);
                    noUsbAcknowledgementDialog.show();
                    VideoPopupDialog.this.dismiss();
                    return;
                }

                if (!downloaded)
                    startDownload();
                else deleteFile();
            }
        });

        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPopupWindowClick();
            }
        });
    }

    private void onPopupWindowClick() {
        PopupWindow mPopupWindow;
        VideoView popupVideoView;
        View popupView;
        ImageButton popupDismissButton;

        popupView = getLayoutInflater().inflate(R.layout.popup_window_video_view, null);
        mPopupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        popupVideoView = popupView.findViewById(R.id.popupVideoView);
        popupDismissButton = popupView.findViewById(R.id.popupDismissButton);

        popupVideoView.setVideoPath(videoData.getVideoUrl());
        //popupVideoView.start();
        popupVideoView.setMediaController(new MediaController(mContext));
        mPopupWindow.setContentView(popupView);


        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setFocusable(true);

        mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);


        popupDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow.isShowing()) {
                    videoView.seekTo(popupVideoView.getCurrentPosition());
                    mPopupWindow.dismiss();
                }
            }
        });
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                VideoPopupDialog.this.show();
            }
        });
        popupVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                popupVideoView.seekTo(0);
            }
        });
        videoView.pause();
        popupVideoView.seekTo(videoView.getCurrentPosition());

        mHandler.removeCallbacks(hideMediaControllerRunnable);

        if (mediaController != null)
            mediaController.setVisibility(View.GONE);

        VideoPopupDialog.this.hide();
        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("ダウンロード中");

        progressDialog.getWindow().setLayout(50, 50);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    private void setListeners() {
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                VideoPopupDialog.this.dismiss();
            }
        });
        addToEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = EventRepo.getInstance().getCreateOrEditEventLiveData().getValue();
                if (event == null) event = new Event();
                EventVideoDetails videoDetails = new EventVideoDetails();
                videoDetails.setThumbnailUrl(videoData.getThumbnailUrl());
                videoDetails.setCalorie(videoData.getCalorie());
                videoDetails.setIrName(videoData.getIrName());
                videoDetails.setReleaseDate(videoData.getReleaseDate());
                videoDetails.setVideoTitle(videoData.getVideoTitle());
                videoDetails.setVideoTime(videoData.getVideoTime());
                String id = videoData.getVideoId();
                if (event.getVideoId() == null || event.getVideoId().isEmpty()) {
                    event.setVideoId(id);
                } else {
                    event.setVideoId(event.getVideoId() + "," + id);
                }

                event.addToVideoArray(videoDetails);
                EventRepo.getInstance().setCreateOrEditEvent(event);
                EventRepo.getInstance().loadSelectedVideoIds();
                if (selectedText != null)
                    selectedText.setVisibility(View.VISIBLE);

                setTabName(event.getVideoArray().size());
                EventAddedResponseDialog responseDialog = new EventAddedResponseDialog(mContext);
                responseDialog.show();
                dismiss();
            }
        });
    }

    private void startVideo() {
        //mediaController = new MediaController(mContext);
        //mediaController.setMediaPlayer(videoView);
        //videoView.setMediaController(mediaController);
        //mediaController.setAnchorView(videoView);
        //videoView.requestFocus();
        videoView.setVideoURI(Uri.parse(videoData.getVideoUrl()));
        //mediaController.show();
        /*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                //mediaController.show();
                mediaPlayer = mp;
                mediaController.show();
                videoView.seekTo(1);
            }
        });*/
        //videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp,
                                                   int width, int height) {
                        /*
                         * add media controller
                         */
                        mediaController = new MediaController(mContext);
                        videoView.setMediaController(mediaController);
                        /*
                         * and set its position on screen
                         */
                        mediaController.setAnchorView(videoView);

                        ((ViewGroup) mediaController.getParent()).removeView(mediaController);

                        ((FrameLayout) findViewById(R.id.videoViewWrapper))
                                .addView(mediaController);
                        videoView.start();
                        mediaController.setBackgroundColor(Color.parseColor("#10000000"));
                        mediaController.setVisibility(View.INVISIBLE);
                        //mHandler.postDelayed(hideMediaControllerRunnable, 4000);

                        //mediaController.show(500);
                    }
                });
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.seekTo(0);
            }
        });
        videoView.setOnClickListener(videoClickListener);


    }

    private void startDownload() {
        videoView.pause();
        String DOWNLOAD_URL = videoData.getVideoUrl();
        DownLoadTask task = new DownLoadTask();
        String file_name = DOWNLOAD_URL.replace("/", "a").replace(":", "a");
        task.execute(DOWNLOAD_URL, file_name, videoData.getVideoId());
    }

    private void deleteFile() {
        String filePath;
        EventRepo.getInstance().deleteFile(videoData.getVideoId());

        String DOWNLOAD_URL = videoData.getVideoUrl();
        String file_name = DOWNLOAD_URL.replace("/", "a").replace(":", "a");
        //String sdPath = Environment.getExternalStorageDirectory().getPath() + "/.fitness";
        File[] storageDir = mContext.getExternalFilesDirs(null);
        String sdPath = storageDir[1].getPath() + "/.fitness";
        filePath = sdPath + "/" + file_name;
        File file = new File(filePath);
        file.delete();
        downloaded = false;
        downloadVideo.setText(DOWNLOAD);
        addToEvent.setEnabled(false);
        if (view != null)
            view.setImageResource(R.drawable.ic_download);
        EventRepo.getInstance().reCalculateSpace.setValue(true);

    }

    public void setData(VideoData videoData) {
        this.videoData = videoData;
    }

    private void setTabName(int size) {
        if (size <= 0) {
            ((TabActivity) mContext).updateEditOrCreateTabHeading(TAB_NAME);
        } else {
            ((TabActivity) mContext).updateEditOrCreateTabHeading(TAB_NAME + " [" + size + "]");
        }
    }


    private class DownLoadTask extends AsyncTask<String, Integer, Boolean> {
        private final String TAG = VideoPopupDialog.DownLoadTask.class.getSimpleName();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private final String download_date = dateFormat.format(date);
        private String filePath;
        private String video_id;

        @Override
        protected void onPreExecute() {
            progressDialog.setButton(
                    DialogInterface.BUTTON_NEGATIVE,
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            VideoPopupDialog.DownLoadTask.this.cancel(false);
                        }
                    }
            );
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            URL url;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                Log.e(getClass().getSimpleName(), "誤ったURL書式 : " + params[0], e);
                return null;
            }

            HttpURLConnection conn = null;

            long total = 0;

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // Keep-Aliveを無効にしないと、2回目の通信時にReadTimeOutを喰らう
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2 &&
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    conn.setRequestProperty("Connection", "close");
                }
                conn.setReadTimeout(TIMEOUT_SECOND);
                conn.setConnectTimeout(TIMEOUT_SECOND);
                conn.connect();
                // ファイルサイズの取得（％表示するため）
                int fileLength = conn.getContentLength();
                // SDカードのパスは環境によって異なるので、動的に取得する
                //String sdPath = Environment.getExternalStorageDirectory().getPath() + "/.fitness";
                File[] storageDir = mContext.getExternalFilesDirs(null);
                String sdPath = storageDir[1].getPath() + "/.fitness";
                File directory = new File(sdPath);
                if (!directory.exists()) {
                    directory.mkdir();
                    // If you require it to make the entire directory path including parents,
                    // use directory.mkdirs(); here instead.
                }
                filePath = sdPath + "/" + params[1];
                video_id = params[2];

                // ファイルダウンロード
                try (InputStream input = new BufferedInputStream(conn.getInputStream());
                     OutputStream output = new FileOutputStream(filePath)) {
                    byte[] data = new byte[1024];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // ProgressBarに進捗を表示してもらう
                        publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                    output.flush();
                }
            } catch (ProtocolException e) {
                Log.e(TAG, "total received = " + total, e);
                return false;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                return false;
            } finally {
                if (conn != null) {
                    try {
                        conn.disconnect();
                    } catch (Exception ignore) {
                        Log.e(TAG, ignore.getMessage(), ignore);
                    }
                }
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressDialog.dismiss();
            String toastMessage = (success) ? "ダウンロード完了" : "ダウンロード失敗";
            if (success) {
                System.out.println("ダウンロード完了");
                downloaded = true;
                downloadVideo.setText(DELETE);
                addToEvent.setEnabled(true);
                if (view != null)
                    view.setImageResource(R.drawable.ic_download_completed);
                //ダウンロード完了でDBに書き込み
                InsertVideodata insertvideodata = new InsertVideodata();
                insertvideodata.execute(user_id, video_id, download_date, filePath);
            } else {
                System.out.println("ダウンロード失敗");
            }
            Toast.makeText(mContext, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private class InsertVideodata extends AsyncTask<String, String, String> {
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL("http://www.cmanage.net/homefitness/download.php");
                // connection を定義
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setReadTimeout(2000);
                conn.setConnectTimeout(2000);
                conn.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user_id", params[0])
                        .appendQueryParameter("video_id", params[1])
                        .appendQueryParameter("save_date", params[2])
                        .appendQueryParameter("local_path", params[3]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                final int status = conn.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    return ("OK");
                } else {
                    Toast.makeText(mContext, "returning unsuccessful", Toast.LENGTH_LONG).show();

                    return ("unsuccessful");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "exception";
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            EventRepo.getInstance().loadDownloadedVideoIds();
            EventRepo.getInstance().reCalculateSpace.setValue(true);
            if (result != null) {
                System.out.println(result);
//                finish();
//                Intent intent = new Intent(getApplicationContext(), search_result.class);
//                intent.putExtra("user_id", user_id);
//                intent.putExtra("category", search_word);
//                startActivity(intent);
            } else {
                System.out.println("No");
            }

        }
    }
}
