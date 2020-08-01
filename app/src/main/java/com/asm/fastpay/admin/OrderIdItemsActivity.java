package com.asm.fastpay.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;

import com.asm.fastpay.adapters.MyOrderAdapter;
import com.asm.fastpay.R;

public class OrderIdItemsActivity extends AppCompatActivity {

    private RecyclerView ordersIdItemsRecyclerView;
    public static MyOrderAdapter myOrderAdapter;
    private String orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_id_items);

        orderID = getIntent().getStringExtra("ORDER ID");

        ordersIdItemsRecyclerView = findViewById(R.id.order_id_items_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        ordersIdItemsRecyclerView.setLayoutManager(layoutManager);


        myOrderAdapter = new MyOrderAdapter(AdminDBQueries.myOrderItemModelList,new Dialog(this));
        ordersIdItemsRecyclerView.setAdapter(myOrderAdapter);
        AdminDBQueries.loadOrders(this,myOrderAdapter,true,orderID);

    }

    @Override
    protected void onStart() {
        super.onStart();
        myOrderAdapter.notifyDataSetChanged();
    }
}
