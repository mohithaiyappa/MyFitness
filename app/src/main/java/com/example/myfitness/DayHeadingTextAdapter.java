package com.example.myfitness;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DayHeadingTextAdapter extends BaseAdapter {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    String[] days = {"日", "月", "火", "水", "木", "金", "土"};

    public DayHeadingTextAdapter(Context ctx) {
        mLayoutInflater = LayoutInflater.from(ctx);
        mContext = ctx;
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public String getItem(int position) {
        return days[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.calendar_heading_cell, null);
            holder = new DayHeadingTextAdapter.ViewHolder();
            holder.dayText = convertView.findViewById(R.id.dayHeadingText);
            convertView.setTag(holder);
        } else {
            holder = (DayHeadingTextAdapter.ViewHolder) convertView.getTag();
        }
        float dp = mContext.getResources().getDisplayMetrics().density;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(parent.getWidth() / 7,
                (parent.getHeight() - (int) dp * 1) / 1);
        convertView.setLayoutParams(params);
        holder.dayText.setText(getItem(position));
        if (position == 0) {
            holder.dayText.setTextColor(Color.RED);
        } else if (position == 6) {
            holder.dayText.setTextColor(Color.BLUE);
        } else {
            holder.dayText.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView dayText;
    }
}
