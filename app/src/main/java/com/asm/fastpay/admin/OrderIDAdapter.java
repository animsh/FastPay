package com.asm.fastpay.admin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.R;

import java.util.List;

public class OrderIDAdapter extends RecyclerView.Adapter<OrderIDAdapter.ViewHolder> {

    private List<OrderIDModel> orderIDModelList;

    public OrderIDAdapter(List<OrderIDModel> orderIDModelList) {
        this.orderIDModelList = orderIDModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_id_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String orderID = orderIDModelList.get(position).getOrderID();
        holder.setData(orderID);
    }

    @Override
    public int getItemCount() {
        return orderIDModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView orderID;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.tv_order_id);
        }


        public void setData(final String orderId){
            orderID.setText("Order ID: " +  orderId);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderIdItemsIntent = new Intent(itemView.getContext(),OrderIdItemsActivity.class);
                    orderIdItemsIntent.putExtra("ORDER ID", orderId);
                    itemView.getContext().startActivity(orderIdItemsIntent);
                }
            });
        }
    }
}
