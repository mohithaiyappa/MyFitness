package com.example.myfitness;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DayEventListAdapter extends BaseAdapter {
    private Context context;
    public final static String TAG = "MyFitness229";
    //private static List<Event> eventList = new ArrayList<>();
    /*public static SelectedDate currentDate = new SelectedDate() {
        @Override
        public void updateDate(Date date) {
            List<Event> dayEventList  = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String dateString = dateFormat.format(date);
            for ( Event event : MonthSchedule.events){
                if (event.getEventDate().equals(dateString)){
                    dayEventList.add(event);
                }
            }
            eventList = dayEventList;
            updateList();

        }
    };*/

    private List<Event> dayEvents = new ArrayList<>();

    public DayEventListAdapter(Context ctx){
        this.context = ctx;
    }
    @Override
    public int getCount() {
        return dayEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return dayEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.day_fragment_list_view, parent, false);
            holder = new ViewHolder();
            holder.lvStartTime = convertView.findViewById(R.id.lv_startTime);
            holder.lvTitle = convertView.findViewById(R.id.lv_title);
            holder.lvIrName = convertView.findViewById(R.id.lv_irName);
            holder.lvVideoLength = convertView.findViewById(R.id.lv_videoLength);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.event = (Event) getItem(position);
        holder.lvStartTime.setText(holder.event.getStartTime());
        holder.lvTitle.setText(holder.event.getVideoTitle());
        holder.lvIrName.setText(holder.event.getIrName());
        holder.lvVideoLength.setText(holder.event.getVideoTime());
        return convertView;
    }

    public void updateList(List<Event> events){
        //eventList = list;
        dayEvents = events;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView lvStartTime;
        public TextView lvTitle;
        public TextView lvIrName;
        public TextView lvVideoLength;
        public Event event;
    }
}
