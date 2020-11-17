package com.example.myfitness;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class videolist_itemAdapter extends ArrayAdapter<videolist_item> {

    private int mResource;
    private List<videolist_item> mItems;
    private LayoutInflater mInflater;


    public videolist_itemAdapter(Context context, int resource, List<videolist_item> items) {
        super(context, resource, items);

        mResource = resource;
        mItems = items;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        }
        else {
            view = mInflater.inflate(mResource, null);
        }

        try {
            // リストビューに表示する要素を取得
            final videolist_item item = mItems.get(position);
            // サムネイル画像を設定
            ImageView thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
            Picasso.with(getContext())
                    .load(item.getThumbnail())
                    .resize(700,500)
                    .into(thumbnail);
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(item.getVideo_url());
                    ((ListView) parent).performItemClick(view, position, R.id.thumbnail);
                }
            });


            // タイトルを設定
            TextView title = (TextView)view.findViewById(R.id.video_title);
            title.setText(item.getVideo_title());

            TextView thumbnail_time = view.findViewById(R.id.thumbnail_time);
            thumbnail_time.setText(item.getThumbnail_time());

            TextView calorie = view.findViewById(R.id.calorie);
            calorie.setText(item.getCalorie());

            TextView release_date = view.findViewById(R.id.save_date);
            release_date.setText(item.getRelease_date());

            TextView video_time = view.findViewById(R.id.video_time);
            video_time.setText(item.getVideo_time());

            TextView ir_name = view.findViewById(R.id.ir_name);
            ir_name.setText(item.getIr_name());

            ImageView start_icon = view.findViewById(R.id.start_icon);
            Picasso.with(getContext())
                    .load(R.drawable.start_icon)
                    .resize(200,200)
                    .into(start_icon);

            ImageView content_img = view.findViewById(R.id.content_img);
            Picasso.with(getContext())
                    .load(R.drawable.content_icon)
                    .resize(150,200)
                    .into(content_img);
            content_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListView) parent).performItemClick(view, position, R.id.content_img);
                }
            });

            ImageView download_img = view.findViewById(R.id.download_img);
            Picasso.with(getContext())
                    .load(R.drawable.download_icon)
                    .resize(150,150)
                    .into(download_img);
            download_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListView) parent).performItemClick(view, position, R.id.download_img);
                }
            });

            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}
