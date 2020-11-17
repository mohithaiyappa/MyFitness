package com.example.myfitness;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

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
import java.util.concurrent.ExecutionException;

public class Reservation extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ListView save_video_list;
    private String user_id;
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
    private Button all_button;
    private String repeat_order = "";

    private Calendar calendar = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    private DateFormat hhmmss = new SimpleDateFormat("HH:mm:ss");
    private String total_time = "00:00:00";

    private String day_of_week = "";
    private CheckBox mon;
    private CheckBox tue;
    private CheckBox wed;
    private CheckBox thu;
    private CheckBox fri;
    private CheckBox sat;
    private CheckBox sun;

    private TextView start_date;
    private TextView end_date;
    private TextView tilde1_textview;
    private TextView tilde2_textview;
    private TextView tilde3_textview;

    private String check_date;

    ConstraintLayout check_const;

    private TimePicker start_time;
    private TimePicker end_time;

    private ImageButton edit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        try {
            Date total_date = hhmmss.parse(total_time);
            calendar.setTime(total_date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tilde1_textview = findViewById(R.id.tilde1_textview);
        tilde2_textview = findViewById(R.id.tilde2_textview);
        tilde3_textview = findViewById(R.id.tilde3_textview);

        check_const = findViewById(R.id.check_const);


        start_time = findViewById(R.id.start_time_picker);
        end_time = findViewById(R.id.end_time_picker);
        // 12時間表記に変更する
        start_time.setIs24HourView(true);
        end_time.setIs24HourView(true);

        // 現在の日付を取得
        int year = calendar2.get(Calendar.YEAR);
        int month = calendar2.get(Calendar.MONTH);
        final int day = calendar2.get(Calendar.DAY_OF_MONTH);

        start_date = findViewById(R.id.start_date);
        final String today = String.format(Locale.US, "%02d-%02d-%02d", year, month + 1, day);
        start_date.setText(today);
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_date = "start_date";
                showDatePickerDialog(view);
            }
        });

        end_date = findViewById(R.id.end_date);
        final String tomorrow = String.format(Locale.US, "%02d-%02d-%02d", year, month + 1, day + 1);
        end_date.setText(tomorrow);
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_date = "end_date";
                showDatePickerDialog(view);
            }
        });
        period_spinner = findViewById(R.id.period_spinner);
        period_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //何も選択されなかった時の動作
            @Override
            public void onNothingSelected(AdapterView adapterView) {
            }

            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                String mode = parent.getSelectedItem().toString();
                if (mode.equals("当日のみ")) {
                    check_const.setVisibility(View.INVISIBLE);
                    start_date.setVisibility(View.INVISIBLE);
                    tilde1_textview.setVisibility(View.INVISIBLE);
                    tilde2_textview.setText("開始時間");
                    tilde3_textview.setVisibility(View.INVISIBLE);
                    start_time.setVisibility(View.INVISIBLE);
                    end_date.setText(today);
                } else if (mode.equals("期間指定")) {
                    check_const.setVisibility(View.INVISIBLE);
                    tilde1_textview.setVisibility(View.VISIBLE);
                    tilde2_textview.setText("開始時間");
                    tilde3_textview.setVisibility(View.INVISIBLE);
                    start_date.setVisibility(View.VISIBLE);
                    start_time.setVisibility(View.INVISIBLE);
                } else if (mode.equals("曜日指定")) {
                    check_const.setVisibility(View.VISIBLE);
                    tilde1_textview.setVisibility(View.VISIBLE);
                    tilde2_textview.setText("開始時間");
                    tilde3_textview.setVisibility(View.INVISIBLE);
                    start_time.setVisibility(View.INVISIBLE);
                    start_date.setVisibility(View.VISIBLE);
                } else if (mode.equals("リピート")) {
                    check_const.setVisibility(View.VISIBLE);
                    start_date.setVisibility(View.VISIBLE);
                    start_time.setVisibility(View.VISIBLE);
                    tilde1_textview.setVisibility(View.VISIBLE);
                    tilde2_textview.setText("~");
                    tilde2_textview.setGravity(Gravity.CENTER_HORIZONTAL);
                    tilde3_textview.setVisibility(View.VISIBLE);
                }
            }
        });
        Intent getIntent = getIntent();
        user_id = getIntent.getStringExtra("user_id");
        total_time_textview = findViewById(R.id.total_time_textview);
        save_video_list = findViewById(R.id.save_video_list);
        save_video_list.setOnItemClickListener(onItemClickListener);
        GetSavedata getSavedata = new GetSavedata();
        getSavedata.execute(user_id);
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
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.fri);
        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);
        //全曜日選択ボタン
        all_button = findViewById(R.id.all_button);
        all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mon.setChecked(true);
                tue.setChecked(true);
                wed.setChecked(true);
                thu.setChecked(true);
                fri.setChecked(true);
                sat.setChecked(true);
                sun.setChecked(true);
            }
        });
        //新規追加、編集、決定ボタン
        edit_button = findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                day_of_week = "";
                if (mon.isChecked()) {
                    day_of_week += "月,";
                }
                if (tue.isChecked()){
                    day_of_week += "火,";
                }
                if (wed.isChecked()) {
                    day_of_week += "水,";
                }
                if (thu.isChecked()) {
                    day_of_week += "木,";
                }
                if (fri.isChecked()) {
                    day_of_week += "金,";
                }
                if (sat.isChecked()) {
                    day_of_week += "土,";
                }
                if (sun.isChecked()) {
                    day_of_week += "日";
                }
                Calendar now_calendar = Calendar.getInstance();
                String mode = period_spinner.getSelectedItem().toString();
                int now_hour = now_calendar.get(Calendar.HOUR_OF_DAY);
                int now_minutes = now_calendar.get(Calendar.MINUTE);
                int year = now_calendar.get(Calendar.YEAR);
                int month = now_calendar.get(Calendar.MONTH);
                int day = now_calendar.get(Calendar.DAY_OF_MONTH);
                String today = String.format(Locale.US, "%02d-%02d-%02d", year, month + 1, day);
                // 現在の日付を取得
                if (mode.equals("当日のみ")) {
                    int set_hour = end_time.getHour();
                    int set_minutes = end_time.getMinute();
                    String e_time = String.format("%02d", set_hour) + ":" + String.format("%02d", set_minutes) + ":" + "00";
                    if (today.equals(end_date.getText())) {
                        if ((now_hour == set_hour && now_minutes >= set_minutes) || now_hour > set_hour) {
                            Toast ts = Toast.makeText(getApplicationContext() , "現在の時刻以降に設定してください", Toast.LENGTH_LONG);
                            ts.setGravity(Gravity.CENTER,0,-160);
                            ts.show();
                        } else {
                            if (repeat_order.equals("")) {
                                Toast ts = Toast.makeText(getApplicationContext(), "動画を1つ以上選択してください", Toast.LENGTH_LONG);
                                ts.setGravity(Gravity.CENTER, 0, -160);
                                ts.show();
                            } else {
                                //登録
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
                                    Schedule_register schedule_register = new Schedule_register();
                                    System.out.println(end_date.getText().toString() + ":" + end_date.getText().toString() + ":" + e_time + ":" + endtime + repeat_order);
                                    schedule_register.execute(user_id, end_date.getText().toString(), end_date.getText().toString(), e_time, endtime, repeat_order, "", "当日のみ");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        if (repeat_order.equals("")) {
                            Toast ts = Toast.makeText(getApplicationContext(), "動画を1つ以上選択してください", Toast.LENGTH_LONG);
                            ts.setGravity(Gravity.CENTER, 0, -160);
                            ts.show();
                        } else {
                            //登録
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
                                Schedule_register schedule_register = new Schedule_register();
                                schedule_register.execute(user_id, end_date.getText().toString(), end_date.getText().toString(), e_time, endtime, repeat_order, "", "当日のみ");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } else if (mode.equals("期間指定")) {
                    int set_hour = end_time.getHour();
                    int set_minutes = end_time.getMinute();
                    String e_time = String.format("%02d", set_hour) + ":" + String.format("%02d", set_minutes) + ":" + "00";
                    if (repeat_order.equals("")) {
                        Toast ts = Toast.makeText(getApplicationContext(), "動画を1つ以上選択してください", Toast.LENGTH_LONG);
                        ts.setGravity(Gravity.CENTER, 0, -160);
                        ts.show();
                    } else {
                        String s_date = start_date.getText().toString();
                        String e_date = end_date.getText().toString();
                        //開始の日付が終了の日付より前ならOK
                        if (e_date.compareTo(s_date) > 0) {
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
                                Schedule_register schedule_register = new Schedule_register();
                                System.out.println(end_date.getText().toString() + ":" + end_date.getText().toString() + ":" + e_time + ":" + endtime);
                                schedule_register.execute(user_id, start_date.getText().toString(), end_date.getText().toString(), e_time, endtime, repeat_order, "", "期間指定");
                            } catch (Exception e) {

                            }
                        } else {
                            Toast ts = Toast.makeText(getApplicationContext() , "終了の日付を開始の日付より後に設定してください", Toast.LENGTH_LONG);
                            ts.setGravity(Gravity.CENTER,0,-160);
                            ts.show();
                        }
                    }


                } else if (mode.equals("曜日指定")) {
                    String s_date = start_date.getText().toString();
                    String e_date = end_date.getText().toString();
                    int set_hour = end_time.getHour();
                    int set_minutes = end_time.getMinute();
                    String e_time = String.format("%02d", set_hour) + ":" + String.format("%02d", set_minutes) + ":" + "00";
                    if (repeat_order.equals("")) {
                        Toast ts = Toast.makeText(getApplicationContext(), "動画を1つ以上選択してください", Toast.LENGTH_LONG);
                        ts.setGravity(Gravity.CENTER, 0, -160);
                        ts.show();
                    } else {
                        //開始の日付が終了の日付より前ならOK
                        if (e_date.compareTo(s_date) > 0) {
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
                                Schedule_register schedule_register = new Schedule_register();
                                System.out.println(end_date.getText().toString() + ":" + end_date.getText().toString() + ":" + e_time + ":" + endtime);
                                schedule_register.execute(user_id, start_date.getText().toString(), end_date.getText().toString(), e_time, endtime, repeat_order, day_of_week, "曜日指定");
                            } catch (Exception e) {

                            }
                        } else {
                            Toast ts = Toast.makeText(getApplicationContext() , "終了の日付を開始の日付より後に設定してください", Toast.LENGTH_LONG);
                            ts.setGravity(Gravity.CENTER,0,-160);
                            ts.show();
                        }
                    }

                } else if (mode.equals("リピート")) {
                    String s_date = start_date.getText().toString();
                    String e_date = end_date.getText().toString();
                    int s_time_hour = start_time.getHour();
                    int s_time_minutes = start_time.getMinute();
                    int e_time_hour = end_time.getHour();
                    int e_time_minutes = end_time.getMinute();
                    String s_time = String.format("%02d", s_time_hour) + ":" + String.format("%02d", s_time_minutes) + ":" + "00";
                    String e_time = String.format("%02d", e_time_hour) + ":" + String.format("%02d", e_time_minutes) + ":" + "00";

                    if (repeat_order.equals("")) {
                        Toast ts = Toast.makeText(getApplicationContext(), "動画を1つ以上選択してください", Toast.LENGTH_LONG);
                        ts.setGravity(Gravity.CENTER, 0, -160);
                        ts.show();
                    } else {
                        //開始の日付が終了の日付より前ならOK
                        if (e_date.compareTo(s_date) > 0) {
                            if ((s_time_hour == e_time_hour && s_time_minutes >= e_time_minutes) || s_time_hour > e_time_hour) {
                                Toast ts = Toast.makeText(getApplicationContext() , "終了時間を開始時間より後に設定してください", Toast.LENGTH_LONG);
                                ts.setGravity(Gravity.CENTER,0,-160);
                                ts.show();
                            } else {
                                Schedule_register schedule_register = new Schedule_register();
                                schedule_register.execute(user_id, s_date, e_date, s_time, e_time, repeat_order, day_of_week, "リピート");
                                System.out.println("OOOOk");
                            }
                        } else {
                            Toast ts = Toast.makeText(getApplicationContext() , "終了の日付を開始の日付より後に設定してください", Toast.LENGTH_LONG);
                            ts.setGravity(Gravity.CENTER,0,-160);
                            ts.show();
                        }
                    }


                }
            }
        });
    }

    private class GetSavedata extends AsyncTask<String, String, String> {

        private ProgressDialog Dialog = new ProgressDialog(Reservation.this);

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
                    Toast.makeText(Reservation.this, "returning unsuccessful", Toast.LENGTH_LONG).show();

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
                Reservation_itemAdapter adapter = new Reservation_itemAdapter(Reservation.this, R.layout.reservationvideolist_item, listItems);
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
                    videoView = new VideoView(Reservation.this);
                    zoomButton = new ImageButton(Reservation.this);
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
                    videoView.setMediaController(new MediaController(Reservation.this));
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
                    new AlertDialog.Builder(Reservation.this, R.style.MyAlertDialogStyle)
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

    private class Schedule_register extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL("http://www.cmanage.net/homefitness/schedule_insert.php");
                // connection を定義
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setReadTimeout(2000);
                conn.setConnectTimeout(2000);
                conn.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user_id", params[0])
                        .appendQueryParameter("start_date", params[1])
                        .appendQueryParameter("end_date", params[2])
                        .appendQueryParameter("start_time", params[3])
                        .appendQueryParameter("end_time", params[4])
                        .appendQueryParameter("video_ids", params[5])
                        .appendQueryParameter("day_of_week", params[6])
                        .appendQueryParameter("mode", params[7]);
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
                    return ("OK");
                } else {
                    Toast.makeText(Reservation.this, "returning unsuccessful", Toast.LENGTH_LONG).show();

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
                System.out.println("OK");
            } else {
                System.out.println("No");
            }

        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        String str = String.format(Locale.US, "%02d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
        if (check_date.equals("start_date")) {
            start_date.setText(str);
        } else {
            end_date.setText(str);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePick();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

}