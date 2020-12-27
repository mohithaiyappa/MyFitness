package com.example.myfitness;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DayEventsAdapter extends RecyclerView.Adapter<DayEventsAdapter.DayEventsViewHolder> {

    private final Context mContext;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<Event> dayEvents = new ArrayList<>();

    public DayEventsAdapter(Context ctx) {
        mContext = ctx;
    }

    @NonNull
    @Override
    public DayEventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_event_details, parent, false);

        return new DayEventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayEventsViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return dayEvents.size();
    }

    public void submitList(List<Event> events) {
        dayEvents = events;
        notifyDataSetChanged();
    }

    class DayEventsViewHolder extends RecyclerView.ViewHolder {

        private TextView eventTimeText;
        private ImageView eventModelImage;
        private RecyclerView recyclerView;

        public DayEventsViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTimeText = (TextView) itemView.findViewById(R.id.eventTime);
            eventModelImage = (ImageView) itemView.findViewById(R.id.eventModeIv);
            recyclerView = itemView.findViewById(R.id.eventDetailsRv);

        }

        public void bindData(int position) {
            Event event = dayEvents.get(position);
            String timeText = event.getStartTime().trim().substring(0, 5)
                    + "~"
                    + event.getEndTime().trim().substring(0, 5);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            //layoutManager.setInitialPrefetchItemCount(event.getVideoArray().size());
            eventTimeText.setText(timeText);
            eventModelImage.setImageResource(R.drawable.ic_repeat);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new DayEventsVideoDetailsAdapter(mContext, event.getVideoArray()));
            recyclerView.setRecycledViewPool(viewPool);
        }
    }
}
