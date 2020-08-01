package com.asm.fastpay.users.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.R;
import com.asm.fastpay.adapters.MyRewardsAdapter;
import com.asm.fastpay.models.RewardModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRewardsFragment extends Fragment {


    private RecyclerView rewardsRecyclerView;

    public MyRewardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_rewards, container, false);

        rewardsRecyclerView = view.findViewById(R.id.my_rewards_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rewardsRecyclerView.setLayoutManager(layoutManager);

        List<RewardModel> rewardModelList = new ArrayList<>();
        rewardModelList.add(new RewardModel("Cash Back", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Discount", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Buy 1 get 1 free", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Cash Back", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Discount", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Buy 1 get 1 free", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Cash Back", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Discount", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Buy 1 get 1 free", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));

        MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(rewardModelList, false);
        rewardsRecyclerView.setAdapter(myRewardsAdapter);
        myRewardsAdapter.notifyDataSetChanged();

        return view;
    }

}
