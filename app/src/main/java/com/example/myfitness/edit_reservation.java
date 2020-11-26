package com.example.myfitness;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class edit_reservation extends AppCompatActivity {

    private ListView save_video_list;
    private String user_id;
    private String strDate;
    private String e_id;
    private Event event;
    private ArrayList<Reservationvideolist_item> listItems = new ArrayList<>();
    int max_num = 1;

    HttpURLConnection conn;

    Spinner period_spinner;

    private PopupWindow mPopupWindow;
    private VideoView videoView;
    private boolean videosize = false;
    private ImageButton zoomButton;
    private TextView total_time_textview;
    private Button reset_button;
    private String repeat_order = "";

    private Calendar calendar = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    private DateFormat hhmmss = new SimpleDateFormat("HH:mm:ss");
    private String total_time = "00:00:00";

    private TextView end_date;
    private TimePicker end_time;

    private ImageButton edit_button;
    private ImageButton back_button;
    private ImageButton trash_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reservation);

        try {
            Date total_date = hhmmss.parse(total_time);
            calendar.setTime(total_date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent getIntent = getIntent();
        user_id = getIntent.getStringExtra("user_id");
        strDate = getIntent.getStringExtra("date");
        e_id = getIntent.getStringExtra("e_id");
        System.out.println((user_id + ":" + strDate + ":" + e_id));

        end_time = findViewById(R.id.end_time_picker);
        // 12時間表記に変更する
        end_time.setIs24HourView(true);

        end_date = findViewById(R.id.end_date);
        end_date.setText(strDate);

        total_time_textview = findViewById(R.id.total_time_textview);
        save_video_list = findViewById(R.id.save_video_list);
        save_video_list.setOnItemClickListener(onItemClickListener);
        GetSavedata getSavedata = new GetSavedata();
        getSavedata.execute(user_id);

        //戻るボタン
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TabActivity.class);
                intent.putExtra("user_id",user_id);
                startActivity(intent);
            }
        });
        //deletebutton
        trash_button = findViewById(R.id.trash_button);
        trash_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitEvent.getEventApi().deleteEvent(user_id,Integer.parseInt(e_id)).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        edit_reservation.this.finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("TestingDelete", "onFailure: could not delete item "+t);
                    }
                });
            }
        });
        //リセットボタン
        reset_button = findViewById(R.id.reset_button);
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reservation_itemAdapter adapter = (Reservation_itemAdapter) save_video_list.getAdapter();
                System.out.println(repeat_order);
                repeat_order = "";
                total_time = "00:00:00";
                try {
                    Date total_date = hhmmss.parse(total_time);
                    calendar.setTime(total_date);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < adapter.getCount(); i++) {
                    Reservationvideolist_item item1 = adapter.getItem(i);
                    item1.setMedley_num("");
                    max_num = 1;
                    adapter.notifyDataSetChanged();
                }
                total_time_textview.setText("総再生時間: 00:00:00");
            }
        });

        //新規追加、編集、決定ボタン
        edit_button = findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now_calendar = Calendar.getInstance();
                int now_hour = now_calendar.get(Calendar.HOUR_OF_DAY);
                int now_minutes = now_calendar.get(Calendar.MINUTE);
                int year = now_calendar.get(Calendar.YEAR);
                int month = now_calendar.get(Calendar.MONTH);
                int day = now_calendar.get(Calendar.DAY_OF_MONTH);
                String today = String.format(Locale.US, "%02d-%02d-%02d", year, month + 1, day);

                int set_hour = end_time.getHour();
                int set_minutes = end_time.getMinute();
                String e_time = String.format("%02d", set_hour) + ":" + String.format("%02d", set_minutes) + ":" + "00";

                if (repeat_order.equals("")) {
                    Toast ts = Toast.makeText(getApplicationContext(), "動画を1つ以上選択してください", Toast.LENGTH_LONG);
                    ts.setGravity(Gravity.CENTER, 0, -160);
                    ts.show();
                } else {
                    if (today.equals(end_date.getText())) {
                        if ((now_hour == set_hour && now_minutes >= set_minutes) || now_hour > set_hour) {
                            Toast ts = Toast.makeText(getApplicationContext() , "現在の時刻以降に設定してください", Toast.LENGTH_LONG);
                            ts.setGravity(Gravity.CENTER,0,-160);
                            ts.show();
                        } else {
                            //更新
                            try {
                                String[] date_split = total_time.split(":");
                                int hour = Integer.parseInt(date_split[0]);
                                int minute = Integer.parseInt(date_split[1]);
                                int second = Integer.parseInt(date_split[2]);
                                Date st_time = hhmmss.parse(e_time);
                                calendar.setTime(st_time);
                                calendar.add(Calendar.HOUR, hour);
                                calendar.add(Calendar.MINUTE, minute);
                                calendar.add(Calendar.SECOND, second);
                                Date d_en_time = calendar.getTime();
                                String endtime = hhmmss.format(d_en_time);
                                Schedule_update schedule_update = new Schedule_update();
                                System.out.println(end_date.getText().toString() + ":" + end_date.getText().toString() + ":" + e_time + ":" + endtime + repeat_order + e_id);
                                String send_repeat_order = repeat_order.substring(0, repeat_order.length() - 1);
                                schedule_update.execute(user_id, end_date.getText().toString(), end_date.getText().toString(), e_time, endtime, send_repeat_order, "月,火,水,木,金,土,日", "当日のみ", e_id);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //更新
                        try {
                            String[] date_split = total_time.split(":");
                            int hour = Integer.parseInt(date_split[0]);
                            int minute = Integer.parseInt(date_split[1]);
                            int second = Integer.parseInt(date_split[2]);
                            Date st_time = hhmmss.parse(e_time);
                            calendar.setTime(st_time);
                            calendar.add(Calendar.HOUR, hour);
                            calendar.add(Calendar.MINUTE, minute);
                            calendar.add(Calendar.SECOND, second);
                            Date d_en_time = calendar.getTime();
                            String endtime = hhmmss.format(d_en_time);
                            Schedule_update schedule_update = new Schedule_update();
                            System.out.println(end_date.getText().toString() + ":" + end_date.getText().toString() + ":" + e_time + ":" + endtime + repeat_order + e_id);
                            String send_repeat_order = repeat_order.substring(0, repeat_order.length() - 1);
                            schedule_update.execute(user_id, end_date.getText().toString(), end_date.getText().toString(), e_time, endtime, send_repeat_order, "月,火,水,木,金,土,日", "当日のみ", e_id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private class GetSavedata extends AsyncTask<String, String, String> {

        private ProgressDialog Dialog = new ProgressDialog(edit_reservation.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("読み込み中...");
            Dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL("http://www.cmanage.net/homefitness/save_video.php");
                // connection を定義
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setReadTimeout(2000);
                conn.setConnectTimeout(2000);
                conn.setRequestMethod("GET");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user_id", params[0]);
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
                    return (result.toString());
                } else {
                    Toast.makeText(edit_reservation.this, "returning unsuccessful", Toast.LENGTH_LONG).show();

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
            System.out.println("------------------------------------" + result);
            createList(result);
            Dialog.dismiss();
        }

        private void createList(String jsondata) {
            try {
                listItems.clear();
                JSONArray jsonArray = new JSONArray(jsondata);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Reservationvideolist_item item = new Reservationvideolist_item();
                    item.setVideo_id(jsonObject.getString("video_id"));
                    item.setVideo_title(jsonObject.getString("video_title"));
                    item.setThumbnail(jsonObject.getString("thumbnail_url"));
                    item.setLocal_path(jsonObject.getString("local_path"));
                    item.setCalorie("消費カロリー　約" + jsonObject.getString("calorie") + "kcal");
                    item.setSave_date("保存日　" + jsonObject.getString("save_date"));
                    item.setIr_name(jsonObject.getString("ir_name"));
                    item.setVideo_time("動画の長さ　" + jsonObject.getString("video_time"));
                    item.setThumbnail_time(jsonObject.getString("video_time"));
                    item.setVideo_explanation(jsonObject.getString("video_explanation"));
                    listItems.add(item);


                }
                // 出力結果をリストビューに表示
                Reservation_itemAdapter adapter = new Reservation_itemAdapter(edit_reservation.this, R.layout.reservationvideolist_item, listItems);
                save_video_list.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // タップしたアイテムの取得
            ListView listView = (ListView) parent;
            final Reservationvideolist_item item = (Reservationvideolist_item) listView.getItemAtPosition(position);
            System.out.println(view.getId());
            switch (view.getId()) {
                case R.id.thumbnail:
                    //サムネイルのクリック処理
                    mPopupWindow = new PopupWindow();
                    videoView = new VideoView(edit_reservation.this);
                    zoomButton = new ImageButton(edit_reservation.this);
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
                    videoView.setVideoPath(item.getLocal_path());
                    videoView.start();
                    videoView.setMediaController(new MediaController(edit_reservation.this));
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
                    new AlertDialog.Builder(edit_reservation.this, R.style.MyAlertDialogStyle)
                            .setTitle("動画の詳細")
                            .setMessage(item.getVideo_explanation())
                            .setPositiveButton("閉じる", null)
                            .show();
                    break;
                case R.id.medley_img:
                    //メドレーボタン
                    Reservation_itemAdapter adapter = (Reservation_itemAdapter) listView.getAdapter();
                    System.out.println(adapter.getCount());

                    for (int i = 0; i < adapter.getCount(); i++) {
                        Reservationvideolist_item item1 = adapter.getItem(i);
                        if (item1.getMedley_num() == null || item1.getMedley_num().equals("")) {
                            //なにもしない
                        } else {
                            int num = Integer.parseInt(item1.getMedley_num());
                            if (num > max_num) {
                                max_num += 1;
                            }
                        }
                    }
                    Reservationvideolist_item item2 = adapter.getItem(position);
                    if (item2.getMedley_num() == null || item2.getMedley_num().equals("")) {
                        item2.setMedley_num(Integer.toString(max_num));
                        String[] date_split = item2.getThumbnail_time().split(":");
                        int hour = Integer.parseInt(date_split[0]);
                        int minute = Integer.parseInt(date_split[1]);
                        int second = Integer.parseInt(date_split[2]);
                        calendar.add(Calendar.HOUR, hour);
                        calendar.add(Calendar.MINUTE, minute);
                        calendar.add(Calendar.SECOND, second);
                        Date sum_time = calendar.getTime();
                        total_time_textview.setText("総再生時間：" + hhmmss.format(sum_time));
                        total_time = hhmmss.format(sum_time);
                        repeat_order += item.getVideo_id() + ",";
                        max_num += 1;
                    } else {

                    }

                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private class Schedule_update extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL("http://www.cmanage.net/homefitness/schedule_update.php");
                // connection を定義
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setReadTimeout(2000);
                conn.setConnectTimeout(2000);
                conn.setRequestMethod("GET");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user_id", params[0])
                        .appendQueryParameter("start_date", params[1])
                        .appendQueryParameter("end_date", params[2])
                        .appendQueryParameter("start_time", params[3])
                        .appendQueryParameter("end_time", params[4])
                        .appendQueryParameter("video_id", params[5])
                        .appendQueryParameter("days_only", params[6])
                        .appendQueryParameter("mode", params[7])
                        .appendQueryParameter("e_id", params[8]);
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
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    conn.disconnect();
                    // Pass data to onPostExecute method
                    return(result.toString());
                } else {
                    Toast.makeText(edit_reservation.this, "returning unsuccessful", Toast.LENGTH_LONG).show();

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
            if (result != null) {
                if (result.equals("すでにスケジュールが登録されています")) {
                    Toast ts = Toast.makeText(getApplicationContext() , "すでにスケジュールが登録されています", Toast.LENGTH_LONG);
                    ts.setGravity(Gravity.CENTER,0,-160);
                    ts.show();
                } else {
                    Toast ts = Toast.makeText(getApplicationContext() , result, Toast.LENGTH_LONG);
                    ts.setGravity(Gravity.CENTER,0,-160);
                    ts.show();
                }
            } else {
                System.out.println("No");
            }

        }
    }
}