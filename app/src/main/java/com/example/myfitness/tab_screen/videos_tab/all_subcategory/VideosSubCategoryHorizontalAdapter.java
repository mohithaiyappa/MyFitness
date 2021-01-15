package com.example.myfitness.tab_screen.videos_tab.all_subcategory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfitness.R;
import com.example.myfitness.customdialog.VideoPopupDialog;
import com.example.myfitness.model.VideoData;
import com.example.myfitness.tab_screen.TabScreenSharedViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VideosSubCategoryHorizontalAdapter extends RecyclerView.Adapter<VideosSubCategoryHorizontalAdapter.ViewHolder> {

    private final Picasso picasso;
    List<VideoData> list = new ArrayList<>();
    private TabScreenSharedViewModel viewModel;
    private Context context;
    private int pos;

    public VideosSubCategoryHorizontalAdapter(Context ctx, TabScreenSharedViewModel viewModel) {
        this.viewModel = viewModel;
        context = ctx;
        picasso = Picasso.with(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updatePosition(int p, int categoryPosition) {
        list.clear();
        list.addAll(viewModel.categoryList.get(categoryPosition).getSubcategories().get(p).getVideoDataList());
        Log.d("RecyclerViewTest", "updatePosition: size" + list.size());
        Log.d("RecyclerViewTest", "updatePosition: pos" + p);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView selectedText, videoLengthText, videoTitleText, releaseDateText, calBurntText, irNameText;
        ImageView thumbnailImage;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selectedText = itemView.findViewById(R.id.selectedText);
            videoLengthText = itemView.findViewById(R.id.videoLength);
            videoTitleText = itemView.findViewById(R.id.videoTitle);
            releaseDateText = itemView.findViewById(R.id.releaseDate);
            calBurntText = itemView.findViewById(R.id.calBurnt);
            irNameText = itemView.findViewById(R.id.irName);
            thumbnailImage = itemView.findViewById(R.id.videoThumbNail);
            view = itemView;
        }

        public void bindData(int position) {
            VideoData vData = list.get(position);
            videoTitleText.setText(vData.getVideoTitle());
            videoLengthText.setText(vData.getVideoTime());
            releaseDateText.setText(vData.getReleaseDate());
            calBurntText.setText(vData.getCalorie() + "kCal");
            irNameText.setText(vData.getIrName());
            picasso.load(vData.getThumbnailUrl()).into(thumbnailImage);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoPopupDialog videoPopupDialog = new VideoPopupDialog(context, vData);
                    videoPopupDialog.show();
                }
            };

            view.setOnClickListener(clickListener);
        }
    }
}
