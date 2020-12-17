package com.example.myfitness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.BootstrapButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class download extends Fragment {

    TableLayout tableLayout;
    private String user_id;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_download, container, false);
    }

    // Viewが生成し終わった時に呼ばれるメソッド
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tableLayout = view.findViewById(R.id.category_list);
        Intent getIntent = getActivity().getIntent();
        user_id = getIntent.getStringExtra("user_id");
        GetVideoData getjson = new GetVideoData();
        getjson.execute();
    }

    private void initTableLayout(List<String> data) {
        try {
            String c_data = data.get(0);
            String ir_data = data.get(1);
            JSONArray c_json = new JSONArray(c_data);
            JSONArray ir_json = new JSONArray(ir_data);
            for (int i = 0; i < c_json.length(); i++) {
                JSONObject jsonObject = c_json.getJSONObject(i);
                //サブカテゴリーを取得してコンマ区切りで配列に入れる
                String sub_category = jsonObject.getString("sub_category");
                String[] sub_category_list = sub_category.split(",");
                tableLayout.addView(createParentRow(jsonObject.getString("main_category")));
                for (int j = 0; j < sub_category_list.length; j += 2) {
                    try {
                        tableLayout.addView(createChildRow(sub_category_list[j], sub_category_list[j + 1]));
                    } catch (Exception e) {
                        tableLayout.addView(createChildRow2(sub_category_list[j]));
                    }
                }
            }
            List<String> manid_list = new ArrayList<String>();
            List<String> man_list = new ArrayList<String>();
            List<String> womanid_list = new ArrayList<String>();
            List<String> woman_list = new ArrayList<String>();

            for (int i = 0; i < ir_json.length(); i++) {
                JSONObject jsonObject = ir_json.getJSONObject(i);
                if (jsonObject.getString("gender").equals("男")) {
                    manid_list.add(jsonObject.getString("ir_id"));
                    man_list.add(jsonObject.getString("ir_name"));
                } else {
                    womanid_list.add(jsonObject.getString("ir_id"));
                    woman_list.add(jsonObject.getString("ir_name"));
                }
            }

            tableLayout.addView(createParentRow("男性インストラクター"));
            for (int i = 0; i < man_list.size(); i += 2) {
                try {
                    tableLayout.addView(createIrRow(man_list.get(i), man_list.get(i + 1), manid_list.get(i), manid_list.get(i + 1)));
                } catch (Exception e) {
                    tableLayout.addView(createIrRow2(man_list.get(i), manid_list.get(i)));
                }
            }

            tableLayout.addView(createParentRow("女性インストラクター"));
            for (int i = 0; i < woman_list.size(); i += 2) {
                try {
                    tableLayout.addView(createIrRow(woman_list.get(i), woman_list.get(i + 1), womanid_list.get(i), womanid_list.get(i + 1)));
                } catch (Exception e) {
                    tableLayout.addView(createIrRow2(woman_list.get(i), womanid_list.get(i)));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private BootstrapButton categoryButton(final String text) {
        BootstrapButton button = new BootstrapButton(this.getActivity());
        button.setText(text);
        button.setShowOutline(true);
        button.setRounded(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), search_result.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("category", text);
                startActivity(intent);
            }
        });
        return button;
    }

    private BootstrapButton IrButton(final String text, final String id) {
        BootstrapButton button = new BootstrapButton(this.getActivity());
        button.setText(text);
        button.setShowOutline(true);
        button.setRounded(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), search_result.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("category", id);
                intent.putExtra("ir_name", text);
                startActivity(intent);
            }
        });
        return button;
    }

    private TableRow createParentRow(String title) {
        TableRow parentRow = new TableRow(this.getActivity());
        parentRow.addView(createText(title));
        return parentRow;
    }

    private TableRow createChildRow(String child1, String child2) {
        TableRow childrow = new TableRow(this.getActivity());
        TableRow.LayoutParams row_style = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 5f);
        row_style.height = 200;
        childrow.addView(categoryButton(child1), row_style);
        childrow.addView(categoryButton(child2), row_style);
        return childrow;
    }

    private TableRow createChildRow2(String child) {
        TableRow childrow = new TableRow(this.getActivity());
        TableRow.LayoutParams row_style = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 5f);
        row_style.height = 200;
        childrow.addView(categoryButton(child), row_style);
        return childrow;
    }

    private TableRow createIrRow(String ir1, String ir2, String ir1_id, String ir2_id) {
        TableRow childrow = new TableRow(this.getActivity());
        TableRow.LayoutParams row_style = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 5f);
        row_style.height = 200;
        childrow.addView(IrButton(ir1, ir1_id), row_style);
        childrow.addView(IrButton(ir2, ir2_id), row_style);
        return childrow;
    }

    private TableRow createIrRow2(String ir1, String ir1_id) {
        TableRow childrow = new TableRow(this.getActivity());
        TableRow.LayoutParams row_style = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 5f);
        row_style.height = 200;
        childrow.addView(IrButton(ir1, ir1_id), row_style);
        return childrow;
    }

    private TextView createText(String text) {
        TextView Text = new TextView(this.getActivity());
        Text.setTextSize(25f);
        Text.setText(text);
        return Text;
    }

    private class GetVideoData extends AsyncTask<Void, Void, List<String>> {

        private final ProgressDialog Dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("読み込み中...");
            Dialog.show();
        }

        @Override
        protected List<String> doInBackground(Void... v) {

            String line = "";
            StringBuilder sb = new StringBuilder();
            String ir_line = "";
            StringBuilder ir_sb = new StringBuilder();
            List<String> json_list = new ArrayList<String>();

            try {
                URL url = new URL("http://www.cmanage.net/homefitness/category.php");
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.connect();

                InputStreamReader in = new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(in);

                while ((line = br.readLine()) != null) {
                    if (sb.length() > 0) sb.append('\n');
                    sb.append(line);
                }

                br.close();
                in.close();
                http.disconnect();

                URL ir_url = new URL("http://www.cmanage.net/homefitness/ir.php");
                HttpURLConnection ir_http = (HttpURLConnection) ir_url.openConnection();
                ir_http.setRequestMethod("GET");
                ir_http.connect();

                InputStreamReader ir_in = new InputStreamReader(ir_http.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader ir_br = new BufferedReader(ir_in);

                while ((ir_line = ir_br.readLine()) != null) {
                    if (ir_sb.length() > 0) ir_sb.append('\n');
                    ir_sb.append(ir_line);
                }

                ir_br.close();
                ir_in.close();
                ir_http.disconnect();

            } catch (Exception e) {
                System.out.println(e);
            }
            json_list.add(sb.toString());
            json_list.add(ir_sb.toString());
            return json_list;
        }

        protected void onPostExecute(List<String> data) {
            super.onPostExecute(data);
            if (data != null) {
                initTableLayout(data);
                Dialog.dismiss();
            } else {
                System.out.println("No");
            }
        }
    }

}