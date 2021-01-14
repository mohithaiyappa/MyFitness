package com.example.myfitness.tab_screen.videos_tab.all_subcategory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfitness.R;
import com.example.myfitness.tab_screen.TabScreenSharedViewModel;
import com.example.myfitness.utils.Selection;

public class VideosSubCategoryAdapter extends RecyclerView.Adapter<VideosSubCategoryAdapter.VideosViewHolder> {

    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    public TabScreenSharedViewModel viewModel;
    private Context context;
    private int selectedCategoryPosition;

    public VideosSubCategoryAdapter(Context ctx, TabScreenSharedViewModel vm, int position) {
        context = ctx;
        viewModel = vm;
        this.selectedCategoryPosition = position;
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
        return viewModel.categoryList.get(selectedCategoryPosition).getSubcategories().size();
    }

    class VideosViewHolder extends RecyclerView.ViewHolder {

        TextView headerText;
        RecyclerView recyclerView;
        VideosSubCategoryHorizontalAdapter adapter;

        public VideosViewHolder(@NonNull View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.staggeredViewHeading);
            recyclerView = itemView.findViewById(R.id.horizontalStaggeredVideoView);
            adapter = new VideosSubCategoryHorizontalAdapter(context, viewModel);
            LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(manager);
            recyclerView.setRecycledViewPool(viewPool);
        }

        public void bindData(int position) {
            adapter.updatePosition(position, selectedCategoryPosition);
            String text = viewModel.categoryList.get(selectedCategoryPosition).getSubcategories().get(position)
                    .getSubcategoryName()
                    + "    [ " + adapter.getItemCount() + " ]";
            headerText.setText(text);
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "header clicked", Toast.LENGTH_SHORT).show();
                    viewModel.selectedCategoryIndex = selectedCategoryPosition;
                    viewModel.selectedSubcategoryIndex = position;
                    viewModel.selectedViewTypeLiveData.setValue(Selection.SUBCATEGORY_DETAILED);
                }
            };
            headerText.setOnClickListener(clickListener);
        }
    }
}
