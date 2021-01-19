package com.example.myfitness.tab_screen.videos_tab.all_category;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfitness.R;
import com.example.myfitness.customdialog.VideoPopupDialog;
import com.example.myfitness.model.Subcategory;
import com.example.myfitness.model.VideoData;
import com.example.myfitness.repository.EventRepo;
import com.example.myfitness.tab_screen.TabScreenSharedViewModel;
import com.example.myfitness.utils.Selection;
import com.example.myfitness.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class VideosCategoryHorizontalAdapter extends RecyclerView.Adapter<VideosCategoryHorizontalAdapter.ViewHolder> {

    private final Picasso picasso;
    List<VideoData> list = new ArrayList<>();
    private TabScreenSharedViewModel viewModel;
    private Context context;
    private final int VIEW_TYPE_NORMAL_CARD = 1;
    private final int VIEW_TYPE_MORE_CARD = 2;
    private int selectedCategoryIndex;
    private int itemCount;

    private List<String> downloadedVideoIds = EventRepo.downloadedVideosIds;

    public VideosCategoryHorizontalAdapter(Context ctx, TabScreenSharedViewModel viewModel) {
        this.viewModel = viewModel;
        context = ctx;
        picasso = Picasso.with(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_NORMAL_CARD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_card_view, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_more, parent, false);
        }
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.viewType == VIEW_TYPE_NORMAL_CARD) {
            holder.bindData(position);
        } else {
            holder.bindMoreCard(position);
        }
    }

    @Override
    public int getItemCount() {
        itemCount = list.size();
        if (itemCount > 0) itemCount++;
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == list.size()) {
            return VIEW_TYPE_MORE_CARD;
        } else return VIEW_TYPE_NORMAL_CARD;
    }

    public void updatePosition(int p) {
        selectedCategoryIndex = p;
        for (Subcategory subcategory : viewModel.categoryList.get(p).getSubcategories()) {
            list.addAll(subcategory.getVideoDataList());
        }
        list = list.subList(0, min(list.size(), 10));
        Log.d("RecyclerViewTest", "updatePosition: size" + list.size());
        Log.d("RecyclerViewTest", "updatePosition: pos" + p);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView selectedText, videoLengthText, videoTitleText, releaseDateText, calBurntText, irNameText;
        ImageView thumbnailImage, downloadStateIconImage;
        View view;
        public int viewType;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_NORMAL_CARD) {
                bindNormalViewType(itemView);
            } else {
                bindMoreCardViewType(itemView);
            }
            this.viewType = viewType;
            view = itemView;
        }

        private void bindNormalViewType(View itemView) {
            selectedText = itemView.findViewById(R.id.selectedText);
            videoLengthText = itemView.findViewById(R.id.videoLength);
            videoTitleText = itemView.findViewById(R.id.videoTitle);
            releaseDateText = itemView.findViewById(R.id.releaseDate);
            calBurntText = itemView.findViewById(R.id.calBurnt);
            irNameText = itemView.findViewById(R.id.irName);
            thumbnailImage = itemView.findViewById(R.id.videoThumbNail);
            downloadStateIconImage = itemView.findViewById(R.id.downloadStateIcon);
        }

        private void bindMoreCardViewType(View itemView) {
        }

        public void bindData(int position) {
            VideoData vData = list.get(position);
            videoTitleText.setText(vData.getVideoTitle());
            videoLengthText.setText(vData.getVideoTime());
            releaseDateText.setText(StringUtils.RELEASE_DATE_PREFIX + vData.getReleaseDate());
            calBurntText.setText(StringUtils.CALORIE_COUNT_PREFIX + vData.getCalorie() + "kCal");
            irNameText.setText(vData.getIrName());
            picasso.load(vData.getThumbnailUrl()).into(thumbnailImage);
            if (downloadedVideoIds.contains(vData.getVideoId())) {
                downloadStateIconImage.setImageResource(R.drawable.ic_download_completed);
            } else {
                downloadStateIconImage.setImageResource(R.drawable.ic_download);
            }
            DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (downloadedVideoIds.size() != EventRepo.downloadedVideosIds.size()) {
                        downloadedVideoIds = EventRepo.downloadedVideosIds;
                        notifyDataSetChanged();
                    } else downloadedVideoIds = EventRepo.downloadedVideosIds;
                }
            };

            View.OnClickListener clickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoPopupDialog videoPopupDialog = new VideoPopupDialog(context, vData, downloadStateIconImage);
                    videoPopupDialog.setOnDismissListener(dismissListener);
                    videoPopupDialog.show();
                }
            };

            view.setOnClickListener(clickListener);

        }

        public void bindMoreCard(int position) {
            View.OnClickListener moreCardClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "header clicked", Toast.LENGTH_SHORT).show();
                    viewModel.selectedCategoryIndex = selectedCategoryIndex;
                    viewModel.selectedViewTypeLiveData.setValue(Selection.CATEGORY_DETAILED);
                }
            };
            view.setOnClickListener(moreCardClickListener);
        }

        public void bindDataForMoreCard() {

        }
    }
}
