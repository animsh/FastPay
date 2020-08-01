package com.asm.fastpay.fragments.homefragment;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.material.tabs.TabLayout;

public class ProductTabAdapter extends FragmentStatePagerAdapter {

    private TabLayout tabLayout;
    public ProductTabAdapter(FragmentManager fm, TabLayout _tabLayout) {
        super(fm);
        this.tabLayout = _tabLayout;
    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new FruitFragment();
        }
        else if (position == 1)
        {
            fragment = new VegetableFragment();
        }
        else if (position == 2)
        {
            fragment = new NutsFragment();
        }
        else if (position == 3)
        {
            fragment = new DairyFragment();
        }
        return fragment;
    }
    @Override
    public int getCount() {
        return 4;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Fruit & Vegetables";
        }
        else if (position == 1)
        {
            title = "Personal Care";
        }
        else if (position == 2)
        {
            title = "Household Items";
        }
        else if (position == 3)
        {
            title = "Biscuits, Snacks & Chocolates";
        }
        return title;
    }
}
