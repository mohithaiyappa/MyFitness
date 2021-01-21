package com.example.myfitness.tab_screen.videos_tab.detailed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfitness.R;
import com.example.myfitness.tab_screen.TabScreenSharedViewModel;
import com.example.myfitness.tab_screen.videos_tab.VideosFragment;
import com.example.myfitness.utils.Selection;

public class VideosDetailedViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private VideoDetailedViewAdapter adapter;
    private GridLayoutManager manager;
    private TextView textView;

    private TabScreenSharedViewModel viewModel;
    private int selectedCategoryPosition = 0;
    private int selectedSubCategoryPosition = 0;
    private Selection viewType;/*= Selection.CATEGORY_DETAILED;*/


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_staggered_view, container, false);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(lp);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(TabScreenSharedViewModel.class);

        selectedCategoryPosition = getArguments() != null ? getArguments().getInt("categoryPosition") : 0;

        selectedSubCategoryPosition = getArguments() != null ? getArguments().getInt("subCategoryPosition") : 0;

        viewType = (Selection) getArguments().get("viewType");

        VideosFragment frag = ((VideosFragment) VideosDetailedViewFragment.this.getParentFragment());
        if (frag != null) frag.showBackButton();

        recyclerView = view.findViewById(R.id.horizontalStaggeredVideoView);
        textView = view.findViewById(R.id.staggeredViewHeading);
        if (viewType == Selection.SUBCATEGORY_DETAILED) {
            textView.setText(viewModel.categoryList.get(selectedCategoryPosition)
                    .getSubcategories().get(selectedSubCategoryPosition).getSubcategoryName());
        } else {
            textView.setText(viewModel.categoryList.get(selectedCategoryPosition).getCategoryName());
        }

        manager = new GridLayoutManager(this.getActivity(), 7);


    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new VideoDetailedViewAdapter(this.getActivity(), viewModel, viewType);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.updatePosition(selectedCategoryPosition, selectedSubCategoryPosition);

    }
}
