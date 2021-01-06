package com.example.myfitness;

import android.app.Dialog;
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
        View view;

        public DayEventsViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTimeText = (TextView) itemView.findViewById(R.id.eventTime);
            eventModelImage = (ImageView) itemView.findViewById(R.id.eventModeIv);
            recyclerView = itemView.findViewById(R.id.eventDetailsRv);
            view = itemView;

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

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.event_click_dialog_layout);
                    dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((CustomViewPager) ((TabActivity) mContext).findViewById(R.id.view_pager)).moveTo(3);

                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //todo add delete functionality here
                        }
                    });
                    dialog.show();
                }
            };

            recyclerView.setAdapter(new DayEventsVideoDetailsAdapter(mContext, event.getVideoArray(), clickListener));
            recyclerView.setRecycledViewPool(viewPool);

            view.setOnClickListener(clickListener);

        }
    }
}
