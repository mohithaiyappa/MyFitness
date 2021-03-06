package com.example.myfitness.tab_screen.month_tab;

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

import com.example.myfitness.R;
import com.example.myfitness.customdialog.AcknowledgementDialog;
import com.example.myfitness.model.Event;
import com.example.myfitness.repository.EventRepo;
import com.example.myfitness.tab_screen.TabActivity;
import com.example.myfitness.utils.CustomViewPager;
import com.example.myfitness.utils.StringUtils;

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
        DayEventsVideoDetailsAdapter adapter;
        View view;

        public DayEventsViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTimeText = (TextView) itemView.findViewById(R.id.eventTime);
            eventModelImage = (ImageView) itemView.findViewById(R.id.eventModeIv);
            recyclerView = itemView.findViewById(R.id.eventDetailsRv);
            view = itemView;
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new DayEventsVideoDetailsAdapter(mContext);
            recyclerView.setAdapter(adapter);
            //recyclerView.setRecycledViewPool(viewPool);

        }

        public void bindData(int position) {
            Event event = dayEvents.get(position);
            String timeText = event.getStartTime().trim().substring(0, 5)
                    + "~"
                    + event.getEndTime().trim().substring(0, 5);

            //layoutManager.setInitialPrefetchItemCount(event.getVideoArray().size());
            eventTimeText.setText(timeText);
            if (event.getMode().trim().equals(StringUtils.EVENT_MODE_REPEAT)) {
                eventModelImage.setImageResource(R.drawable.ic_repeat);
            } else {
                eventModelImage.setImageResource(R.drawable.ic_repeat_one);
            }


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
                            EventRepo.getInstance().loadEventWithDetails(event.getE_id());
                            ((CustomViewPager) ((TabActivity) mContext).findViewById(R.id.view_pager)).moveTo(3);

                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AcknowledgementDialog acknowledgementDialog = new AcknowledgementDialog(mContext,
                                    StringUtils.MESSAGE_EVENT_DELETED);
                            acknowledgementDialog.show();
                            //todo add delete functionality here
                            EventRepo.getInstance().deleteEvent(event.getE_id(), event, mContext);
                            dayEvents.remove(event);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            };

            //recyclerView.setAdapter(new DayEventsVideoDetailsAdapter(mContext, event.getVideoArray(), clickListener));
            //recyclerView.setRecycledViewPool(viewPool);
            adapter.updateData(event.getVideoArray(), clickListener);
            view.setOnClickListener(clickListener);

        }
    }
}
