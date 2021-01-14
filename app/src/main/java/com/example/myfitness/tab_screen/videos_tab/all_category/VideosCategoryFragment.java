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
import com.example.myfitness.model.Category;
import com.example.myfitness.model.Subcategory;
import com.example.myfitness.model.VideoData;
import com.example.myfitness.tab_screen.TabScreenSharedViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class VideosCategoryFragment extends Fragment {

    private TabScreenSharedViewModel viewModel;

    private VideosCategoryAdapter adapter;
    private RecyclerView recyclerView;

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
        LinearLayoutManager manager = new LinearLayoutManager(this.getActivity());
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

    private void displayData() {
        //Gson gson = new Gson();
        String stringData = "";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        int numberOfItems = 0;
        List<String> strings = new ArrayList<>();
        for (Category category : viewModel.categoryList) {
            stringData = stringData + "\ncategory: " + category.getCategoryName();
            for (Subcategory subCat : category.getSubcategories()) {
                stringData = stringData + "\nsubCat: " + subCat.getSubcategoryName();
                for (VideoData videoData : subCat.getVideoDataList()) {
                    strings.add(videoData.getVideoId());
                    stringData = stringData + "\nvideoData: " + gson.toJson(videoData);
                    numberOfItems++;
                }
                stringData = stringData + "\n ending videoData------------- ";
            }
            stringData = stringData + "\n ending subCat------------- ";

        }
        stringData = stringData + "\nnumberOfItems: " + numberOfItems;
        for (String id : strings) {
            stringData = stringData + "\nid: " + id;
        }
    }
}
