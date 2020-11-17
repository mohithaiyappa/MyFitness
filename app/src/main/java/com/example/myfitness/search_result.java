package com.example.myfitness;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class search_result extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE_CODE = 0x01;
    private static String[] mPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    static final int TIMEOUT_SECOND = 60000;

    ProgressDialog progressDialog;

    HttpURLConnection conn;
    private String user_id;
    private String search_word;
    ListView video_list;
    ArrayList<videolist_item> listItems = new ArrayList<>();

    private PopupWindow mPopupWindow;
    private VideoView videoView;
    private boolean videosize = false;
    private ImageButton zoomButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        String sdPath = Environment.getExternalStorageDirectory().getPath() + "/.fitness";
        //ダウンロードした動画を保存するディレクトリを最初に作成する
        mkdir(sdPath);

        verifyStoragePermissions(this);

        progressDialog = new ProgressDialog(search_result.this);
        progressDialog.setMessage("ダウンロード中");
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        Intent get = getIntent();
        search_word = get.getStringExtra("category");
        user_id = get.getStringExtra("user_id");
        video_list = findViewById(R.id.result_list);
        video_list.setOnItemClickListener(onItemClickListener);
        new SearchResult().execute(search_word,user_id);
    }

    private class SearchResult extends AsyncTask<String, String, String> {

        private ProgressDialog Dialog = new ProgressDialog(search_result.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("読み込み中...");
            Dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL("http://www.cmanage.net/homefitness/search_result.php");
                // connection を定義
                conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setReadTimeout(2000);
                conn.setConnectTimeout(2000);
                conn.setRequestMethod("GET");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("word", params[0])
                        .appendQueryParameter("user_id",params[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                final int status = conn.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    // レスポンスを受け取る処理等
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    // Pass data to onPostExecute method
                    return(result.toString());
                }
                else{
                    Toast.makeText(search_result.this, "returning unsuccessful", Toast.LENGTH_LONG).show();

                    return("unsuccessful");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "exception";
            }
            finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result != null){
                createList(result);
                Dialog.dismiss();
            }else{
                System.out.println("No");
            }

        }

        private void createList(String jsondata) {
            try {
                listItems.clear();
                JSONArray jsonArray = new JSONArray(jsondata);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String exclusive_user = jsonObject.getString("user_only");
                    String[] ex_user_list = exclusive_user.split(",");
                    if (exclusive_user.equals("") || Arrays.asList(ex_user_list).contains(user_id)) {
                        videolist_item item = new videolist_item();
                        item.setVideo_id(jsonObject.getString("video_id"));
                        item.setVideo_title(jsonObject.getString("video_title"));
                        item.setThumbnail(jsonObject.getString("thumbnail_url"));
                        item.setVideo_url(jsonObject.getString("video_url"));
                        item.setCalorie("消費カロリー　約" + jsonObject.getString("calorie") + "kcal");
                        item.setRelease_date("公開日　" + jsonObject.getString("release_date"));
                        item.setIr_name(jsonObject.getString("ir_name"));
                        item.setVideo_time("動画の長さ　" + jsonObject.getString("video_time"));
                        item.setThumbnail_time(jsonObject.getString("video_time"));
                        item.setVideo_explanation(jsonObject.getString("video_explanation"));
                        listItems.add(item);
                    }

                }
                // 出力結果をリストビューに表示
                videolist_itemAdapter adapter = new videolist_itemAdapter(getApplication(), R.layout.videolist_item, listItems);
                video_list.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // タップしたアイテムの取得
            ListView listView = (ListView)parent;
            videolist_item item = (videolist_item) listView.getItemAtPosition(position);
            System.out.println(view.getId());
            switch (view.getId()) {
                case R.id.thumbnail:
                    //サムネイルのクリック処理
                    mPopupWindow = new PopupWindow(search_result.this);
                    videoView = new VideoView(search_result.this);
                    zoomButton = new ImageButton(search_result.this);
                    // レイアウト設定
                    View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);
                    popupView.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mPopupWindow.isShowing()) {
                                mPopupWindow.dismiss();
                            }
                        }
                    });
                    videoView = popupView.findViewById(R.id.videoView);
                    videoView.setVideoURI(Uri.parse(item.getVideo_url()));
                    videoView.start();
                    videoView.setMediaController(new MediaController(search_result.this));
                    mPopupWindow.setContentView(popupView);

                    // タップ時に他のViewでキャッチされないための設定
                    mPopupWindow.setOutsideTouchable(true);
                    mPopupWindow.setFocusable(true);

                    // 表示サイズの設定 今回は幅500dp
                    float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 700, getResources().getDisplayMetrics());
                    mPopupWindow.setWindowLayoutMode((int) width, WindowManager.LayoutParams.WRAP_CONTENT);
                    mPopupWindow.setWidth((int) width);
                    mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    // 画面中央に表示
                    mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                    zoomButton = popupView.findViewById(R.id.zoomButton);
                    zoomButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Resources r = getResources();
                            Bitmap shrink = BitmapFactory.decodeResource(r, R.drawable.shrink_icon);
                            Bitmap zoom = BitmapFactory.decodeResource(r, R.drawable.window_zoom);
                            if (videosize) {
                                //全画面のとき小さくする
                                mPopupWindow.dismiss();
                                videoView.start();
                                zoomButton.setImageBitmap(zoom);
                                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1000, getResources().getDisplayMetrics());
                                mPopupWindow.setWindowLayoutMode((int) width, WindowManager.LayoutParams.WRAP_CONTENT);
                                mPopupWindow.setWidth((int) width);
                                mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                                // 画面中央に表示
                                mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                                videosize = false;
                            } else {
                                //小さい画面のとき全画面表示にする
                                mPopupWindow.dismiss();
                                videoView.start();
                                zoomButton.setImageBitmap(shrink);
                                mPopupWindow.setWindowLayoutMode(
                                        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                                mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                                mPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                                // 画面中央に表示
                                mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                                videosize = true;
                            }
                        }
                    });

                    break;
                case R.id.content_img:
                    //詳細文表示ボタン
                    new AlertDialog.Builder(search_result.this,R.style.MyAlertDialogStyle)
                            .setTitle("動画の詳細")
                            .setMessage(item.getVideo_explanation())
                            .setPositiveButton("閉じる", null)
                            .show();
                    break;
                case R.id.download_img:
                    //ダウンロードボタン
                    String DOWNLOAD_URL = item.getVideo_url();
                    DownLoadTask task = new DownLoadTask();
                    String file_name = DOWNLOAD_URL.replace("/","a").replace(":","a");
                    task.execute(DOWNLOAD_URL,file_name,item.getVideo_id());
                    break;
            }
        }
    };

    private static void verifyStoragePermissions(Activity activity) {
        int readPermission = ContextCompat.checkSelfPermission(activity, mPermissions[0]);
        int writePermission = ContextCompat.checkSelfPermission(activity, mPermissions[1]);

        if (writePermission != PackageManager.PERMISSION_GRANTED ||
                readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    mPermissions,
                    REQUEST_EXTERNAL_STORAGE_CODE
            );
        }
    }

    public boolean mkdir(String path){

        File file = new File(path);
        return file.mkdir();
    }

    private class DownLoadTask extends AsyncTask<String, Integer, Boolean> {
        private final String TAG = DownLoadTask.class.getSimpleName();
        private String filePath;
        private String video_id;
        Date date=new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private String download_date = dateFormat.format(date);

        @Override
        protected void onPreExecute() {
            progressDialog.setButton (
                    DialogInterface . BUTTON_NEGATIVE,
                    "Cancel",
                    new DialogInterface.OnClickListener () {
                        public void onClick (DialogInterface dialog, int which) {
                            DownLoadTask.this.cancel (false);
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
                if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2 &&
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ){
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
                    byte data[] = new byte[1024];
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
                //ダウンロード完了でDBに書き込み
                InsertVideodata insertvideodata = new InsertVideodata();
                insertvideodata.execute(user_id,video_id,download_date,filePath);
            } else {
                System.out.println("ダウンロード失敗");
            }
            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private class InsertVideodata extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL("http://www.cmanage.net/homefitness/download.php");
                // connection を定義
                conn = (HttpURLConnection)url.openConnection();
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
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                final int status = conn.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    return("OK");
                }
                else{
                    Toast.makeText(search_result.this, "returning unsuccessful", Toast.LENGTH_LONG).show();

                    return("unsuccessful");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "exception";
            }
            finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result != null){
                System.out.println(result);
                finish();
                Intent intent = new Intent(getApplicationContext(),search_result.class);
                intent.putExtra("user_id",user_id);
                intent.putExtra("category",search_word);
                startActivity(intent);
            }else{
                System.out.println("No");
            }

        }
    }

}