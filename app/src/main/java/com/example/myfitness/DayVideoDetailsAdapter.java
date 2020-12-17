package com.example.myfitness;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DayVideoDetailsAdapter extends BaseAdapter {

    private final Context context;

    private List<EventVideoDetails> videoDetailsList = new ArrayList<>();

    public DayVideoDetailsAdapter(Context ctx) {
        this.context = ctx;
    }

    @Override
    public int getCount() {
        return videoDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DayVideoDetailsAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.day_fragment_video_details, parent, false);
            holder = new DayVideoDetailsAdapter.ViewHolder();
            holder.lvTitle = convertView.findViewById(R.id.lv_title);
            holder.lvIrName = convertView.findViewById(R.id.lv_irName);
            holder.lvVideoLength = convertView.findViewById(R.id.lv_videoLength);
            convertView.setTag(holder);
        } else {
            holder = (DayVideoDetailsAdapter.ViewHolder) convertView.getTag();
        }
        Log.d("MyFitness229789", "getView: " + videoDetailsList.size());
        holder.videoDetails = (EventVideoDetails) getItem(position);
        String videoTitle;
        if ((position + 1) == videoDetailsList.size()) {
            videoTitle = holder.videoDetails.getVideoTitle();
        } else {
            videoTitle = holder.videoDetails.getVideoTitle();
        }
        holder.lvTitle.setText(videoTitle);
        holder.lvIrName.setText(holder.videoDetails.getIrName());
        holder.lvVideoLength.setText(holder.videoDetails.getVideoTime());
        return convertView;
    }

    public void updateList(List<EventVideoDetails> videoDetails) {
        videoDetailsList = videoDetails;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView lvTitle;
        public TextView lvIrName;
        public TextView lvVideoLength;
        public EventVideoDetails videoDetails;
    }
}
