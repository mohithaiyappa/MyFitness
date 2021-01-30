package com.example.myfitness.tab_screen.videos_tab.detailed;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfitness.R;
import com.example.myfitness.customdialog.AcknowledgementDialog;
import com.example.myfitness.customdialog.DownloadTaskDialog;
import com.example.myfitness.customdialog.VideoPopupDialog;
import com.example.myfitness.model.Subcategory;
import com.example.myfitness.model.VideoData;
import com.example.myfitness.repository.EventRepo;
import com.example.myfitness.tab_screen.TabScreenSharedViewModel;
import com.example.myfitness.utils.Selection;
import com.example.myfitness.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoDetailedViewAdapter extends RecyclerView.Adapter<VideoDetailedViewAdapter.ViewHolder> {

    private final Picasso picasso;
    List<VideoData> list = new ArrayList<>();
    private TabScreenSharedViewModel viewModel;
    private Selection viewType;
    private Context context;
    private int pos;

    private List<String> downloadedVideoIds = EventRepo.downloadedVideosIds;

    public VideoDetailedViewAdapter(Context ctx, TabScreenSharedViewModel viewModel, Selection viewType) {
        this.viewModel = viewModel;
        context = ctx;
        picasso = Picasso.with(context);
        this.viewType = viewType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_card_view_detailed, parent, false);
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
        ImageView thumbnailImage, downloadStateIconImage;
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
            downloadStateIconImage = itemView.findViewById(R.id.downloadStateIcon);
            view = itemView;
        }

        public void bindData(int position) {
            VideoData vData = list.get(position);
            videoTitleText.setText(vData.getVideoTitle());
            videoLengthText.setText(vData.getVideoTime());
            releaseDateText.setText(StringUtils.RELEASE_DATE_PREFIX + vData.getReleaseDate().replace("-", "/"));
            calBurntText.setText(StringUtils.CALORIE_COUNT_PREFIX + vData.getCalorie() + "kCal");
            irNameText.setText(vData.getIrName());
            picasso.load(vData.getThumbnailUrl()).into(thumbnailImage);
            if (downloadedVideoIds.contains(vData.getVideoId())) {
                downloadStateIconImage.setImageResource(R.drawable.ic_download_completed);
            } else {
                downloadStateIconImage.setImageResource(R.drawable.ic_download);
            }
            if (EventRepo.selectedVideosIds.contains(vData.getVideoId().trim())) {
                selectedText.setVisibility(View.VISIBLE);
            } else {
                selectedText.setVisibility(View.INVISIBLE);
            }
            DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (downloadedVideoIds.size() != EventRepo.downloadedVideosIds.size()) {
                        downloadedVideoIds = EventRepo.downloadedVideosIds;
                        notifyDataSetChanged();
                    } else downloadedVideoIds = EventRepo.downloadedVideosIds;
                    notifyItemChanged(position);
                }
            };

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoPopupDialog videoPopupDialog = new VideoPopupDialog(context, vData, downloadStateIconImage, selectedText);
                    videoPopupDialog.setOnDismissListener(dismissListener);
                    videoPopupDialog.show();
                }
            };

            View.OnClickListener downloadClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (EventRepo.downloadedVideosIds.contains(vData.getVideoId().trim())) {
                        return;
                    }
                    File[] storageDir = context.getExternalFilesDirs(null);
                    if (storageDir.length == 1) {

                        //return for now show dialog in download task
                        AcknowledgementDialog noUsbAcknowledgementDialog = new AcknowledgementDialog(context,
                                StringUtils.CONNECT_USB_STORAGE);
                        noUsbAcknowledgementDialog.show();
                        return;
                    }
                    DownloadTaskDialog downloadTaskDialog = new DownloadTaskDialog(context, vData, downloadStateIconImage);
                    //downloadTaskDialog.setOnDismissListener(dismissListener);
                    downloadTaskDialog.show();
                }
            };
            downloadStateIconImage.setOnClickListener(downloadClickListener);

            view.setOnClickListener(clickListener);
        }
    }
}
