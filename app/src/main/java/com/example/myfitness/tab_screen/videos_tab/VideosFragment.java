package com.example.myfitness.tab_screen.videos_tab;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfitness.R;
import com.example.myfitness.tab_screen.TabScreenSharedViewModel;
import com.example.myfitness.tab_screen.videos_tab.all_category.VideosCategoryFragment;
import com.example.myfitness.tab_screen.videos_tab.all_subcategory.VideosSubCategoryFragment;
import com.example.myfitness.tab_screen.videos_tab.detailed.VideosDetailedViewFragment;
import com.example.myfitness.utils.Selection;

import java.io.File;

public class VideosFragment extends Fragment {

    private TextView emptySpaceText;
    private Button backButton;

    private FragmentManager childFragmentManager;

    private TabScreenSharedViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);

        viewModel = new ViewModelProvider(getActivity()).get(TabScreenSharedViewModel.class);

        findEmptySpace();


        viewModel.selectedViewTypeLiveData.observe(getViewLifecycleOwner(), new Observer<Selection>() {
            @Override
            public void onChanged(Selection viewType) {
                attachFragment(getRightFragment(viewType));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        //viewModel.loadDataFromNetwork();
        //loadVideoData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void attachFragment(Fragment fragment) {
        //Bundle bundle = new Bundle();
        //Fragment fragment = new VideosCategoryFragment();

        FragmentTransaction transaction = childFragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private Fragment getRightFragment(Selection viewType) {
        Fragment fragment;
        switch (viewType) {
            case CATEGORY_STAGGERED:
                fragment = new VideosCategoryFragment();
                break;

            case SUBCATEGORY_STAGGERED: {
                fragment = new VideosSubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("categoryPosition", viewModel.selectedCategoryIndex);
                fragment.setArguments(bundle);
                break;
            }
            case CATEGORY_DETAILED: {
                fragment = new VideosDetailedViewFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("categoryPosition", viewModel.selectedCategoryIndex);
                bundle.putSerializable("viewType", Selection.CATEGORY_DETAILED);
                fragment.setArguments(bundle);
                break;
            }
            case SUBCATEGORY_DETAILED: {
                fragment = new VideosDetailedViewFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("categoryPosition", viewModel.selectedCategoryIndex);
                bundle.putInt("subCategoryPosition", viewModel.selectedSubcategoryIndex);
                bundle.putSerializable("viewType", Selection.SUBCATEGORY_DETAILED);
                fragment.setArguments(bundle);
                break;
            }

            default:
                fragment = new VideosCategoryFragment();
        }

        return fragment;
    }

    private void bindViews(View view) {
        childFragmentManager = getChildFragmentManager();

        emptySpaceText = view.findViewById(R.id.storageText);
        backButton = view.findViewById(R.id.backButton);

        //setting Listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childFragmentManager.getBackStackEntryCount() > 1) {
                    childFragmentManager.popBackStack();
                }
            }
        });
    }

    private void findEmptySpace() {
        String spaceText = "空き容量 【" + getAvailableInternalMemorySize() + "】";
        emptySpaceText.setText(spaceText);
        viewModel.displayText = spaceText;
    }

    private String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return formatSize(availableBlocks * blockSize);
    }

    private String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}
