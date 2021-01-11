package com.example.myfitness.tab_screen.create_event_tab;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfitness.R;
import com.example.myfitness.model.EventVideoDetails;

import java.util.List;

public class CreateEventsVideoDetailsAdapter extends RecyclerView.Adapter<CreateEventsVideoDetailsAdapter.VideoDetailsViewHolder> {

    private Context mContext;
    private List<EventVideoDetails> videoDetailsList;

    public CreateEventsVideoDetailsAdapter(Context ctx, List<EventVideoDetails> list) {
        mContext = ctx;
        videoDetailsList = list;
    }

    @NonNull
    @Override
    public VideoDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.create_event_video_details_item,
                parent, false);
        return new VideoDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoDetailsViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return videoDetailsList.size();
    }

    public void submitList(List<EventVideoDetails> list) {
        videoDetailsList = list;
        notifyDataSetChanged();
    }

    class VideoDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView itemNumberText, videoTitleText, irNameText, videoLengthText, calBurntText, releaseDateText;
        private ImageView thumbnailImageView;
        private View view;

        public VideoDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNumberText = itemView.findViewById(R.id.itemNumber);
            videoTitleText = itemView.findViewById(R.id.videoName);
            irNameText = itemView.findViewById(R.id.rvIrName);
            calBurntText = itemView.findViewById(R.id.rvCalBurnt);
            releaseDateText = itemView.findViewById(R.id.rvReleaseDate);
            videoLengthText = itemView.findViewById(R.id.rvVideoLength);
            thumbnailImageView = itemView.findViewById(R.id.videoThumbNail);
            view = itemView;
        }

        public void bindData(int position) {
            EventVideoDetails videoDetails = videoDetailsList.get(position);
            itemNumberText.setText(Integer.toString(position + 1));
            videoTitleText.setText(videoDetails.getVideoTitle());
            irNameText.setText(videoDetails.getIrName());
            videoLengthText.setText(videoDetails.getVideoTime());
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.move_event_dialog_layout);

                    //cancel dialog
                    dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    //if video size is 1 or less don't highlight any options
                    if (videoDetailsList.size() <= 1) {
                        //don't set listeners for other views
                        //Todo: ask if view has to be grayed out
                    } else {

                        //make item move up
                        dialog.findViewById(R.id.moveUpButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        //make item move down
                        dialog.findViewById(R.id.moveDownButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        //delete item
                        dialog.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });


                    }

                    dialog.show();
                }
            };

            view.setOnClickListener(clickListener);
        }

        public void onClick(View v) {
        }
    }
}
