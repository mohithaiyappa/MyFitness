package com.example.myfitness.tab_screen.videos_tab.detailed;

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
import com.example.myfitness.model.Subcategory;
import com.example.myfitness.model.VideoData;
import com.example.myfitness.tab_screen.TabScreenSharedViewModel;
import com.example.myfitness.utils.Selection;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VideoDetailedViewAdapter extends RecyclerView.Adapter<VideoDetailedViewAdapter.ViewHolder> {

    private final Picasso picasso;
    List<VideoData> list = new ArrayList<>();
    private TabScreenSharedViewModel viewModel;
    private Selection viewType;
    private Context context;
    private int pos;

    public VideoDetailedViewAdapter(Context ctx, TabScreenSharedViewModel viewModel, Selection viewType) {
        this.viewModel = viewModel;
        context = ctx;
        picasso = Picasso.with(context);
        this.viewType = viewType;
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

    public void updatePosition(int categoryIndex, int subCategoryIndex) {
        list.clear();
        if (viewType == Selection.SUBCATEGORY_DETAILED) {
            list.addAll(viewModel.categoryList.get(categoryIndex).getSubcategories().get(subCategoryIndex).getVideoDataList());

        } else {
            for (Subcategory subcategory : viewModel.categoryList.get(categoryIndex).getSubcategories()) {
                list.addAll(subcategory.getVideoDataList());
            }
        }

        Log.d("RecyclerViewTest", "updatePosition: size" + list.size());
        Log.d("RecyclerViewTest", "updatePosition: pos" + categoryIndex);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView selectedText, videoLengthText, videoTitleText, releaseDateText, calBurntText, irNameText;
        ImageView thumbnailImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selectedText = itemView.findViewById(R.id.selectedText);
            videoLengthText = itemView.findViewById(R.id.videoLength);
            videoTitleText = itemView.findViewById(R.id.videoTitle);
            releaseDateText = itemView.findViewById(R.id.releaseDate);
            calBurntText = itemView.findViewById(R.id.calBurnt);
            irNameText = itemView.findViewById(R.id.irName);
            thumbnailImage = itemView.findViewById(R.id.videoThumbNail);
        }

        public void bindData(int position) {
            VideoData vData = list.get(position);
            videoTitleText.setText(vData.getVideoTitle());
            videoLengthText.setText(vData.getVideoTime());
            releaseDateText.setText(vData.getReleaseDate());
            calBurntText.setText(vData.getCalorie() + "kCal");
            irNameText.setText(vData.getIrName());
            picasso.load(vData.getThumbnailUrl()).into(thumbnailImage);
        }
    }
}
