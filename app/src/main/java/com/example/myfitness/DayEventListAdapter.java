package com.example.myfitness;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DayEventListAdapter extends BaseAdapter {
    private final Context context;
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
            holder.lvStartTime      = convertView.findViewById(R.id.lv_startTime);
            holder.lvTitle          = convertView.findViewById(R.id.lv_title);
            holder.lvIrName         = convertView.findViewById(R.id.lv_irName);
            holder.lvVideoLength    = convertView.findViewById(R.id.lv_videoLength);
            holder.lvEditButton     = convertView.findViewById(R.id.lvEditButton);
            holder.lvDeleteButton   = convertView.findViewById(R.id.lvDeleteButton);
            convertView.setTag(holder);
        }else {
            holder  = (ViewHolder)convertView.getTag();
        }
        holder.event = (Event) getItem(position);
        holder.lvStartTime.setText(holder.event.getStartTime());
        holder.lvTitle.setText(holder.event.getVideoTitle());
        holder.lvIrName.setText(holder.event.getIrName());
        holder.lvVideoLength.setText(holder.event.getVideoTime());
        holder.lvEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = (Event) getItem(position);
                Intent intent = new Intent(context,CreateEventActivity.class);
                // todo add data to send to create or edit activity

                context.startActivity(intent);
            }
        });
        holder.lvDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Delete Clicked", Toast.LENGTH_SHORT).show();
                Event  event = (Event) getItem(position);
                EventRepo.getInstance().deleteEvent(event.getE_id(),event);
                dayEvents.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public void updateList(List<Event> events){
        dayEvents = events;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView lvStartTime;
        public TextView lvTitle;
        public TextView lvIrName;
        public TextView lvVideoLength;
        public ImageButton lvEditButton,lvDeleteButton;
        public Event event;
    }
}
