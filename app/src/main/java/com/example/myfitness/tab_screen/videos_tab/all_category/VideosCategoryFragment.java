package com.example.myfitness.tab_screen.videos_tab.all_category;

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
import com.example.myfitness.tab_screen.videos_tab.VideosFragment;

public class VideosCategoryFragment extends Fragment {

    private TabScreenSharedViewModel viewModel;

    private VideosCategoryAdapter adapter;
    private RecyclerView recyclerView;
    LinearLayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(TabScreenSharedViewModel.class);

        recyclerView = view.findViewById(R.id.staggeredRecyclerview);
        manager = new LinearLayoutManager(this.getActivity());

        VideosFragment frag = ((VideosFragment) VideosCategoryFragment.this.getParentFragment());
        if (frag != null) frag.hideBackButton();


    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new VideosCategoryAdapter(this.getActivity(), viewModel);
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
