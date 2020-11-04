package com.example.myfitness;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarAdapter extends BaseAdapter {
    private List<Date> dateArray = new ArrayList();
    private Context mContext;
    private DateManager mDateManager;
    private LayoutInflater mLayoutInflater;
    private List<Event> monthEvents = new ArrayList<>();

    //カスタムセルを拡張したらここでWigetを定義
    private static class ViewHolder {
        public TextView dateText;
        public TextView eventText;
    }

    public CalendarAdapter(Context context){
        mContext    = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDateManager    = new DateManager();
        dateArray   = mDateManager.getDays();
    }

    @Override
    public int getCount() {
        return dateArray.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.calendar_cell, null);
            holder  = new ViewHolder();
            holder.dateText     = convertView.findViewById(R.id.dateText);
            holder.eventText    = convertView.findViewById(R.id.eventsText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //セルのサイズを指定
        float dp = mContext.getResources().getDisplayMetrics().density;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(parent.getWidth()/7 - (int)dp,
                (parent.getHeight() - (int)dp * mDateManager.getWeeks() ) / mDateManager.getWeeks());
        convertView.setLayoutParams(params);

        //日付のみ表示させる
        final SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
        holder.dateText.setText(dateFormat.format(dateArray.get(position)));
        //run only if monthEvents is not empty
        holder.eventText.setText(getThisDaysEvents(dateArray.get(position)));

        //当月以外のセルをグレーアウト
        if (mDateManager.isCurrentMonth(dateArray.get(position))){
            convertView.setBackgroundColor(Color.WHITE);
        }else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }

        //日曜日を赤、土曜日を青に color used for weekends
        int colorId;
        switch (mDateManager.getDayOfWeek(dateArray.get(position))){
            case 1:
                colorId = Color.RED;
                break;
            case 7:
                colorId = Color.BLUE;
                break;
            default:
                colorId = Color.BLACK;
                break;
        }
        holder.dateText.setTextColor(colorId);
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return dateArray.get(position);
    }

    //表示月を取得 display the current month
    public String getTitle(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月", Locale.US);
        return format.format(mDateManager.mCalendar.getTime());
    }

    //翌月表示 display the next month
    public void nextMonth(){
        mDateManager.nextMonth();
        EventRepo.getInstance().loadEvents(mDateManager.getYear(),mDateManager.getMonth());
        dateArray = mDateManager.getDays();
        monthEvents.clear();
        this.notifyDataSetChanged();
    }

    //前月表示 display the previous month
    public void prevMonth(){
        mDateManager.prevMonth();
        EventRepo.getInstance().loadEvents(mDateManager.getYear(),mDateManager.getMonth());
        dateArray = mDateManager.getDays();
        monthEvents.clear();
        this.notifyDataSetChanged();
    }

    public DateManager getDateManager() {
        return mDateManager;
    }

    //checks for day events to add to calendar grid view
    private String getThisDaysEvents(Date date) {
        String eventText = "";
        if (monthEvents.isEmpty()) return eventText;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateString = dateFormat.format(date);
        int counter = 0;
        for ( Event event : monthEvents){
            if (event.getEventDate().equals(dateString)){
                counter++;
                if (counter<3) {
                    int lastIndex = event.getStartTime().lastIndexOf(":");
                    eventText = eventText
                            + event.getStartTime().substring(0, lastIndex)
                            + " "
                            + event.getVideoTitle().substring(0, 4)
                            + "...\n";
                }
            }
        }
        if(counter>2) return eventText+(counter-2)+" more event";
        return eventText;
    }

    public void updateList(List<Event> events){
        if(monthEvents.isEmpty()&&events.isEmpty()) return;

        monthEvents = events;
        dateArray = mDateManager.getDays();
        notifyDataSetChanged();
    }
}