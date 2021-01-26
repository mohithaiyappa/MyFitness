package com.example.myfitness.tab_screen;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myfitness.model.Category;
import com.example.myfitness.model.InterRimCategory;
import com.example.myfitness.model.Subcategory;
import com.example.myfitness.model.VideoData;
import com.example.myfitness.network.RetrofitEvent;
import com.example.myfitness.repository.EventRepo;
import com.example.myfitness.utils.Selection;
import com.example.myfitness.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabScreenSharedViewModel extends ViewModel {

    public final List<Category> categoryList = new ArrayList<>();

    public final MutableLiveData<Boolean> hasLoadingFinished = new MutableLiveData<>(false);

    public MutableLiveData<Selection> selectedViewTypeLiveData = new MutableLiveData<>(Selection.CATEGORY_STAGGERED);
    public int selectedCategoryIndex = 0;
    public int selectedSubcategoryIndex = 0;

    public String displayText = "";

    public void loadDataFromNetwork() {
        EventRepo.getInstance().loadDownloadedVideoIds();
        new Thread() {
            @Override
            public void run() {

                createCategories();
            }
        }.start();
    }

    private void createCategories() {


        RetrofitEvent.getEventApi().getInterRimCategories().enqueue(new Callback<List<InterRimCategory>>() {
            @Override
            public void onResponse(Call<List<InterRimCategory>> call, Response<List<InterRimCategory>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.d("CatTest", "onResponse: " + response.toString());
                    return;
                }
                categoryList.clear();
                Category cat = new Category();
                cat.setCategoryName(StringUtils.RECENTLY_ADDED);
                Subcategory s = new Subcategory();
                s.setSubcategoryName(StringUtils.RECENTLY_ADDED);
                cat.addToSubcategoryList(s);
                categoryList.add(cat);

                for (InterRimCategory interRimCategory : response.body()) {
                    Category category = new Category();
                    category.setCategoryName(interRimCategory.getMainCategory());
                    String[] subCategories = interRimCategory.getSubCategory().split(",");
                    for (String subCat : subCategories) {
                        Subcategory subcategory = new Subcategory();
                        subcategory.setSubcategoryName(subCat);
                        category.addToSubcategoryList(subcategory);
                    }
                    categoryList.add(category);
                }


                //Gson gson = /*new GsonBuilder().setPrettyPrinting().create();*/ new Gson();
                //Log.d("CatTest", "onResponse: "+ gson.toJson(categoryList));

                loadVideoData();
            }

            @Override
            public void onFailure(Call<List<InterRimCategory>> call, Throwable t) {

            }
        });
    }

    private void loadVideoData() {
        RetrofitEvent.getEventApi().getVideoData().enqueue(new Callback<List<VideoData>>() {
            @Override
            public void onResponse(Call<List<VideoData>> call, Response<List<VideoData>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.d("CatTest", "onResponse: " + response.toString());
                    return;
                }
                List<String> usersOnlyList;
                for (VideoData videoData : response.body()) {
                    String[] users = videoData.getUsersOnly().split(",");
                    usersOnlyList = new ArrayList<>(Arrays.asList(users));
                    if (!usersOnlyList.contains(EventRepo.userName)) continue;
                    String[] subCategoryNames = videoData.getCategoryString().split(",");
                    Log.d("CatTest", "onResponse: " + subCategoryNames.toString());
                    for (Category cat : categoryList) {
                        for (Subcategory subCat : cat.getSubcategories()) {
                            for (String name : subCategoryNames) {
                                if (name.equals(subCat.getSubcategoryName())) {
                                    subCat.addToVideoDataList(videoData);
                                    Log.d("CatTest", "\nadding to: " + subCat.getSubcategoryName()
                                            + "\nvideo Id" + videoData.getVideoId());
                                }
                            }
                        }
                    }
                }





               /* //Gson gson = new Gson();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                int numberOfItems =0;
                List<String> strings = new ArrayList<>();
                for(Category category : categoryList){
                    Log.d("CatTest", "\ncategory: "+category.getCategoryName());
                    for(Subcategory subCat : category.getSubcategories()){
                        Log.d("CatTest", "\nsubCat: "+subCat.getSubcategoryName());
                        for(VideoData videoData : subCat.getVideoDataList()){
                            strings.add(videoData.getVideoId());
                            Log.d("CatTest", "\nvideoData: "+gson.toJson(videoData));
                            numberOfItems++;
                        }
                        Log.d("CatTest", "\n ending videoData------------- ");
                    }
                    Log.d("CatTest", "\n ending subCat------------- ");

                }
                Log.d("CatTest", "numberOfItems: " + numberOfItems);
                for(String id : strings){
                    Log.d("CatTest", "\nid: " + id);
                }*/
                hasLoadingFinished.postValue(true);
            }

            @Override
            public void onFailure(Call<List<VideoData>> call, Throwable t) {

            }
        });
    }


}
