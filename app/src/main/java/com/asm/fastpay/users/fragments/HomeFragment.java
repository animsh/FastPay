package com.asm.fastpay.users.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.asm.fastpay.R;
import com.asm.fastpay.models.CategoryModel;
import com.asm.fastpay.models.HomePageModel;
import com.asm.fastpay.models.HorizontalProductScrollModel;
import com.asm.fastpay.models.SliderModel;
import com.bumptech.glide.Glide;
import com.asm.fastpay.adapters.CategoryAdapter;
import com.asm.fastpay.adapters.HomePageAdapter;
import com.asm.fastpay.models.WishListModel;

import java.util.ArrayList;
import java.util.List;

import static com.asm.fastpay.DBQueries.categoryModelList;
import static com.asm.fastpay.DBQueries.lists;
import static com.asm.fastpay.DBQueries.loadCategories;
import static com.asm.fastpay.DBQueries.loadFragmentData;
import static com.asm.fastpay.DBQueries.loadedCategoryName;
import static com.asm.fastpay.R.id.refresh_layout;

public class HomeFragment extends Fragment {

    public static SwipeRefreshLayout swipeRefreshLayout;
    public static ImageView noInternetImg;
    private Button retryBtn;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private RecyclerView homePageRecyclerView;
    private HomePageAdapter adapter;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private List<CategoryModel> categoryModelFakeList = new ArrayList<>();
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = view.findViewById(refresh_layout);
        categoryRecyclerView = view.findViewById(R.id.category_recyclerview);
        homePageRecyclerView = view.findViewById(R.id.home_page_recyclerview);

        noInternetImg = view.findViewById(R.id.no_internet_icon);
        retryBtn = view.findViewById(R.id.retry_btn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homePageRecyclerView.setLayoutManager(testingLayoutManager);

        //category fake list
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        //category fake list

        //home page fake list
        List<SliderModel> sliderModelFaleList = new ArrayList<>();
        sliderModelFaleList.add(new SliderModel("null", "#dfdfdf"));
        sliderModelFaleList.add(new SliderModel("null", "#dfdfdf"));
        sliderModelFaleList.add(new SliderModel("null", "#dfdfdf"));
        sliderModelFaleList.add(new SliderModel("null", "#dfdfdf"));
        sliderModelFaleList.add(new SliderModel("null", "#dfdfdf"));

        final List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));

        homePageModelFakeList.add(new HomePageModel(0, sliderModelFaleList));
        homePageModelFakeList.add(new HomePageModel(1, "", "#dfdfdf", horizontalProductScrollModelFakeList, new ArrayList<WishListModel>()));
        homePageModelFakeList.add(new HomePageModel(2, "", "#dfdfdf", horizontalProductScrollModelFakeList));
        //home page fake list

        categoryAdapter = new CategoryAdapter(categoryModelFakeList);

        adapter = new HomePageAdapter(homePageModelFakeList);


        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true) {
            noInternetImg.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.GONE);

            if (categoryModelList.size() == 0) {
                loadCategories(categoryRecyclerView, getContext());
            } else {
                categoryAdapter = new CategoryAdapter(categoryModelList);
                categoryAdapter.notifyDataSetChanged();
            }
            categoryRecyclerView.setAdapter(categoryAdapter);
            if (lists.size() == 0) {
                loadedCategoryName.add("HOME");
                lists.add(new ArrayList<HomePageModel>());

                loadFragmentData(homePageRecyclerView, getContext(), 0, "Home");
            } else {
                adapter = new HomePageAdapter(lists.get(0));
                adapter.notifyDataSetChanged();
            }
            homePageRecyclerView.setAdapter(adapter);
        } else {
            noInternetImg.setVisibility(View.VISIBLE);
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.GONE);
            retryBtn.setVisibility(View.VISIBLE);
            Glide.with(this).load(R.drawable.no_internet).into(noInternetImg);
        }
        //
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout();
            }
        });
        //
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout();
            }
        });

        return view;
    }

    private void refreshLayout() {
        swipeRefreshLayout.setRefreshing(true);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        categoryModelList.clear();
        lists.clear();
        loadedCategoryName.clear();

        if (networkInfo != null && networkInfo.isConnected() == true) {
            noInternetImg.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Internet Is Working", Toast.LENGTH_SHORT).show();
            categoryAdapter = new CategoryAdapter(categoryModelFakeList);
            adapter = new HomePageAdapter(homePageModelFakeList);
            categoryRecyclerView.setAdapter(categoryAdapter);
            homePageRecyclerView.setAdapter(adapter);

            loadCategories(categoryRecyclerView, getContext());
            loadedCategoryName.add("HOME");
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(homePageRecyclerView, getContext(), 0, "Home");
        } else {
            noInternetImg.setVisibility(View.VISIBLE);
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.GONE);
            retryBtn.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Internet Is not Working", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
