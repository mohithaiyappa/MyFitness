package com.example.myfitness.tab_screen.videos_tab.all_subcategory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfitness.R;
import com.example.myfitness.tab_screen.TabScreenSharedViewModel;

public class VideosSubCategoryFragment extends Fragment {


    private TabScreenSharedViewModel viewModel;

    private VideosSubCategoryAdapter adapter;
    private RecyclerView recyclerView;
    private int selectedCategoryPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(TabScreenSharedViewModel.class);

        selectedCategoryPosition = getArguments() != null ? getArguments().getInt("categoryPosition") : 0;

        recyclerView = view.findViewById(R.id.staggeredRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this.getActivity());
        adapter = new VideosSubCategoryAdapter(this.getActivity(), viewModel, selectedCategoryPosition);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


        viewModel.hasLoadingFinished.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }
}
