package com.example.myfitness;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DayEventsVideoDetailsAdapter extends RecyclerView.Adapter<DayEventsVideoDetailsAdapter.VideoDetailsViewHolder> {


    private List<EventVideoDetails> videoDetailsList = new ArrayList<>();
    private Context mContext;
    private View.OnClickListener mClickListener;

    public DayEventsVideoDetailsAdapter(Context ctx, List<EventVideoDetails> list, View.OnClickListener clickListener) {
        mContext = ctx;
        videoDetailsList = list;
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public VideoDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_events_video_details, parent, false);
        return new VideoDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoDetailsViewHolder holder, int position) {
        holder.bindData(videoDetailsList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoDetailsList.size();
    }

    class VideoDetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVideoName, tvVideoTime, tvIrName;
        View view;

        public VideoDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVideoName = itemView.findViewById(R.id.tvVideoTitle);
            tvVideoTime = itemView.findViewById(R.id.tvVideoTime);
            tvIrName = itemView.findViewById(R.id.tvIrName);
            view = itemView;
        }

        public void bindData(EventVideoDetails eventVideoDetails) {

            tvVideoName.setText(eventVideoDetails.getVideoTitle().trim());
            tvVideoTime.setText(eventVideoDetails.getVideoTime().trim());
            tvIrName.setText(eventVideoDetails.getIrName().trim());
            view.setOnClickListener(mClickListener);
        }
    }
}
