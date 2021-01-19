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
import com.example.myfitness.repository.EventRepo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateEventsVideoDetailsAdapter extends RecyclerView.Adapter<CreateEventsVideoDetailsAdapter.VideoDetailsViewHolder> {

    private Context mContext;
    private List<EventVideoDetails> videoDetailsList;
    private final Picasso picasso;
    private List<String> videoIdList = new ArrayList<>();
    private ReservationFragment.SetVideoTime setVideoTimeInterface;

    public CreateEventsVideoDetailsAdapter(Context ctx, List<EventVideoDetails> list, ReservationFragment.SetVideoTime videoTimeInterface) {
        mContext = ctx;
        videoDetailsList = list;
        picasso = Picasso.with(mContext);
        setVideoTimeInterface = videoTimeInterface;
        //String[] idArray = EventRepo.getInstance().getCreateOrEditEventLiveData().getValue().getVideoId().split(",");
        //videoIdList = Arrays.asList(idArray);
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
        String[] idArray = EventRepo.getInstance().getCreateOrEditEventLiveData().getValue().getVideoId().split(",");
        videoIdList = new ArrayList<>(Arrays.asList(idArray));
        notifyDataSetChanged();
    }

    public void moveUp(int position) {
        if (position < 1) return;

        Collections.swap(videoDetailsList, position, position - 1);
        Collections.swap(videoIdList, position, position - 1);
        updateEventItem();
    }

    public void moveDown(int position) {
        if (position >= videoDetailsList.size() - 1) return; //5 4 3

        Collections.swap(videoDetailsList, position, position + 1);
        Collections.swap(videoIdList, position, position + 1);
        updateEventItem();
    }

    public void removeFromList(int position) {
        videoDetailsList.remove(position);
        videoIdList.remove(position);
        setVideoTimeInterface.calculateAndSetTime(videoDetailsList);
        updateEventItem();
    }

    public void updateEventItem() {
        EventRepo.getInstance().getCreateOrEditEventLiveData().getValue().setVideoArray(videoDetailsList);
        EventRepo.getInstance().getCreateOrEditEventLiveData().getValue().setVideoId(getVideoIds());
        EventRepo.getInstance().loadSelectedVideoIds();
        notifyDataSetChanged();
    }

    //todo return video id arrayList as string with ,
    public String getVideoIds() {
        String videoId = "";
        for (String id : videoIdList) {
            videoId = videoId + id + " ";
        }
        videoId = videoId.trim();
        videoId = videoId.replace(" ", ",");
        return videoId;
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
            calBurntText.setText(videoDetails.getCalorie());
            releaseDateText.setText(videoDetails.getReleaseDate());
            if (!videoDetails.getThumbnailUrl().isEmpty()) {
                picasso.load(videoDetails.getThumbnailUrl()).into(thumbnailImageView);
            }

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


                        //make item move up
                        dialog.findViewById(R.id.moveUpButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                moveUp(position);
                                dialog.dismiss();
                            }
                        });

                        //make item move down
                        dialog.findViewById(R.id.moveDownButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                moveDown(position);
                                dialog.dismiss();
                            }
                        });

                        //delete item
                        dialog.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeFromList(position);
                                dialog.dismiss();
                            }
                        });

                    dialog.show();
                }
            };

            view.setOnClickListener(clickListener);
        }

        public void onClick(View v) {
        }


    }
}
