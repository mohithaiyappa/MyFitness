package com.example.myfitness.tab_screen.videos_tab.all_category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfitness.R;
import com.example.myfitness.model.Subcategory;
import com.example.myfitness.tab_screen.TabScreenSharedViewModel;
import com.example.myfitness.utils.Selection;
import com.example.myfitness.utils.StringUtils;

public class VideosCategoryAdapter extends RecyclerView.Adapter<VideosCategoryAdapter.VideosViewHolder> {

    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    public TabScreenSharedViewModel viewModel;
    private Context context;

    public VideosCategoryAdapter(Context ctx, TabScreenSharedViewModel vm) {
        context = ctx;
        viewModel = vm;
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_staggered_view, parent, false);
        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return viewModel.categoryList.size();
    }

    class VideosViewHolder extends RecyclerView.ViewHolder {

        TextView headerText;
        RecyclerView recyclerView;
        VideosCategoryHorizontalAdapter adapter;

        public VideosViewHolder(@NonNull View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.staggeredViewHeading);
            recyclerView = itemView.findViewById(R.id.horizontalStaggeredVideoView);
            adapter = new VideosCategoryHorizontalAdapter(context, viewModel);
            LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(manager);
            recyclerView.setRecycledViewPool(viewPool);
        }

        public void bindData(int position) {
            adapter.updatePosition(position);
            int itemCount = 0;
            for (Subcategory subcategory : viewModel.categoryList.get(position).getSubcategories()) {
                itemCount = itemCount + subcategory.getVideoDataList().size();
            }
            String text = viewModel.categoryList.get(position).getCategoryName()
                    + "    [ " + itemCount + " ]";
            headerText.setText(text);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "header clicked", Toast.LENGTH_SHORT).show();
                    if (viewModel.categoryList.get(position).getCategoryName().equals(StringUtils.RECENTLY_ADDED)) {
                        viewModel.selectedCategoryIndex = position;
                        viewModel.selectedViewTypeLiveData.setValue(Selection.CATEGORY_DETAILED);
                        return;
                    }
                    viewModel.selectedCategoryIndex = position;
                    viewModel.selectedViewTypeLiveData.setValue(Selection.SUBCATEGORY_STAGGERED);
                }
            };


            headerText.setOnClickListener(clickListener);
        }
    }
}
