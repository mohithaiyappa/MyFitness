package com.example.myfitness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VideoList extends Fragment {

    private ListView save_video_list;
    private String user_id;
    private ArrayList<savevideolist_item> listItems = new ArrayList<>();

    HttpURLConnection conn;

    private PopupWindow mPopupWindow;
    private VideoView videoView;
    private boolean videosize = false;
    private ImageButton zoomButton;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_video_list,container,false);
    }

    // Viewが生成し終わった時に呼ばれるメソッド
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent getIntent = getActivity().getIntent();
        user_id = getIntent.getStringExtra("user_id");
        System.out.println(user_id);
        save_video_list = view.findViewById(R.id.save_video_list);
        save_video_list.setOnItemClickListener(onItemClickListener);
        GetSavedata getSavedata = new GetSavedata();
        getSavedata.execute(user_id);

    }

    private class GetSavedata extends AsyncTask<String, String, String> {

        private ProgressDialog Dialog = new ProgressDialog(getActivity());

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
                conn = (HttpURLConnection)url.openConnection();
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
                    return(result.toString());
                }
                else{
                    Toast.makeText(getActivity(), "returning unsuccessful", Toast.LENGTH_LONG).show();

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
                    savevideolist_item item = new savevideolist_item();
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
                savevideolist_itemAdapter adapter = new savevideolist_itemAdapter(getActivity().getApplication(), R.layout.savevideolist_item, listItems);
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
            final savevideolist_item item = (savevideolist_item) listView.getItemAtPosition(position);
            System.out.println(view.getId());
            switch (view.getId()) {
                case R.id.thumbnail:
                    //サムネイルのクリック処理
                    mPopupWindow = new PopupWindow(getActivity());
                    videoView = new VideoView(getActivity());
                    zoomButton = new ImageButton(getActivity());
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
                    System.out.println(item.getLocal_path());
                    videoView = popupView.findViewById(R.id.videoView);
                    videoView.setVideoPath(item.getLocal_path());
                    videoView.start();
                    videoView.setMediaController(new MediaController(getActivity()));
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
                case R.id.calender_img:
                    //カレンダーボタン
                    Intent intent = new Intent(getActivity(),Reservation.class);
                    intent.putExtra("user_id",user_id);
                    startActivity(intent);
                    break;
                case R.id.content_img:
                    //詳細文表示ボタン
                    new AlertDialog.Builder(getActivity(),R.style.MyAlertDialogStyle)
                            .setTitle("動画の詳細")
                            .setMessage(item.getVideo_explanation())
                            .setPositiveButton("閉じる", null)
                            .show();
                    break;
                case R.id.trash_img:
                    //ゴミ箱ボタン
                    new AlertDialog.Builder(getActivity())
                            .setTitle("")
                            .setMessage("動画を削除してもよろしいですか？(関連予約も削除されます)")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // OK button pressed
                                    DeleteSql deleteSql = new DeleteSql();
                                    deleteSql.execute(user_id,item.getVideo_id());
                                    File file = new File(item.getLocal_path());
                                    file.delete();
                                    Toast t = Toast.makeText(getActivity(), "ファイルを削除しました", Toast.LENGTH_LONG);
                                    t.show();
                                    new GetSavedata().execute(user_id);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    break;
            }
        }
    };


    private class DeleteSql extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL("http://www.cmanage.net/homefitness/file_delete.php");
                // connection を定義
                conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setReadTimeout(2000);
                conn.setConnectTimeout(2000);
                conn.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user_id", params[0])
                        .appendQueryParameter("video_id", params[1]);
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
                    Toast.makeText(getActivity(), "returning unsuccessful", Toast.LENGTH_LONG).show();

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
                System.out.println("OK");
            }else{
                System.out.println("No");
            }

        }
    }
}