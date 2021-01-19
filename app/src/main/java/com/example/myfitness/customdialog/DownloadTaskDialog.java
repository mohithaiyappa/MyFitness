package com.example.myfitness.customdialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myfitness.R;
import com.example.myfitness.model.VideoData;
import com.example.myfitness.repository.EventRepo;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
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

public class DownloadTaskDialog extends ProgressDialog {

    static final int TIMEOUT_SECOND = 60000;
    private final String user_id = EventRepo.userName;
    Context mContext;
    ImageView mImageView;
    private VideoData videoData;

    //private ProgressDialog progressDialog;


    public DownloadTaskDialog(@NonNull Context context, VideoData vData, ImageView imageView) {
        super(context);
        mContext = context;
        mImageView = imageView;
        videoData = vData;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupProgressDialog();
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.dialog_download_task);


        startDownload();

    }

    private void setupProgressDialog() {
        //progressDialog = new ProgressDialog(mContext);
        DownloadTaskDialog.this.setMessage("ダウンロード中");

        //DownloadTaskDialog.this.getWindow().setLayout(50, 50);
        setIndeterminate(false);
        setMax(100);
        setCanceledOnTouchOutside(true);
        setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new Message());
    }

    private void startDownload() {
        String DOWNLOAD_URL = videoData.getVideoUrl();
        DownLoadTask task = new DownLoadTask();
        String file_name = DOWNLOAD_URL.replace("/", "a").replace(":", "a");
        task.execute(DOWNLOAD_URL, file_name, videoData.getVideoId());
    }

    private class DownLoadTask extends AsyncTask<String, Integer, Boolean> {
        private final String TAG = DownLoadTask.class.getSimpleName();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private final String download_date = dateFormat.format(date);
        private String filePath;
        private String video_id;

        @Override
        protected void onPreExecute() {
            DownloadTaskDialog.this.setButton(
                    DialogInterface.BUTTON_NEGATIVE,
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DownLoadTask.this.cancel(false);
                        }
                    }
            );
            //DownloadTaskDialog.this.show();
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
                String sdPath = Environment.getExternalStorageDirectory().getPath() + "/.fitness";
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
            DownloadTaskDialog.this.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean success) {

            String toastMessage = (success) ? "ダウンロード完了" : "ダウンロード失敗";
            if (success) {
                System.out.println("ダウンロード完了");


                if (mImageView != null)
                    mImageView.setImageResource(R.drawable.ic_download_completed);
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
            //DownloadTaskDialog.this.dismiss();
            DownloadTaskDialog.this.dismiss();
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
