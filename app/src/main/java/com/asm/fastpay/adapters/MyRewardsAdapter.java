package com.asm.fastpay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.users.activities.ProductDetailsActivity;
import com.asm.fastpay.R;
import com.asm.fastpay.models.RewardModel;

import java.util.List;

public class MyRewardsAdapter extends RecyclerView.Adapter<MyRewardsAdapter.ViewHolder> {

    private List<RewardModel> rewardModelLis;
    private boolean useMiniLayout = false;

    public MyRewardsAdapter(List<RewardModel> rewardModelLis, boolean useMiniLayout) {
        this.rewardModelLis = rewardModelLis;
        this.useMiniLayout = useMiniLayout;
    }

    @NonNull
    @Override
    public MyRewardsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (useMiniLayout) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_rewards_item_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards_item_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRewardsAdapter.ViewHolder holder, int position) {
        String title = rewardModelLis.get(position).getTitle();
        String date = rewardModelLis.get(position).getExpiryDate();
        String body = rewardModelLis.get(position).getCouponBody();

        holder.setData(title, date, body);
    }

    @Override
    public int getItemCount() {
        return rewardModelLis.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView couponTitle;
        private TextView couponExpiryDate;
        private TextView couponBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            couponTitle = itemView.findViewById(R.id.coupon_title);
            couponExpiryDate = itemView.findViewById(R.id.coupon_validity);
            couponBody = itemView.findViewById(R.id.coupon_body);
        }

        private void setData(final String title, final String date, final String body) {
            couponTitle.setText(title);
            couponExpiryDate.setText(date);
            couponBody.setText(body);

            if (useMiniLayout) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProductDetailsActivity.couponTitle.setText(title);
                        ProductDetailsActivity.couponExpiryDate.setText(date);
                        ProductDetailsActivity.couponBody.setText(body);
                        ProductDetailsActivity.showDialogRecyclerView();
                    }
                });
            }
        }
    }
}
