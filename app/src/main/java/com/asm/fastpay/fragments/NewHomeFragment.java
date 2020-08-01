package com.asm.fastpay.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.asm.fastpay.R;
import com.asm.fastpay.fragments.homefragment.ProductTabAdapter;
import com.asm.fastpay.fragments.homefragment.ViewPagerAdapter;
import com.asm.fastpay.users.activities.HomePageActivity;
import com.asm.fastpay.users.activities.SearchActivity;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewHomeFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private ViewPager viewPager;
    private ProductTabAdapter productTabAdapter;
    private TextView search;

    public NewHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_home, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        search = view.findViewById(R.id.search_et);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        createTabFragment();
/*
        adapter.AddFragment(new FruitFragment(), "Fruits");
        adapter.AddFragment(new VegetableFragment(), "Vegetables");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.getAdapter().notifyDataSetChanged();

 */
        return view;
    }

    private void createTabFragment() {
        productTabAdapter = new ProductTabAdapter(getActivity().getSupportFragmentManager(), tabLayout);
        viewPager.setAdapter(productTabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
