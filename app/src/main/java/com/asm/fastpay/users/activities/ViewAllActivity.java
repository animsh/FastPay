package com.asm.fastpay.users.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.R;
import com.asm.fastpay.adapters.WishListAdapter;
import com.asm.fastpay.adapters.GridProductLayoutAdaptor;
import com.asm.fastpay.models.WishListModel;
import com.asm.fastpay.models.HorizontalProductScrollModel;

import java.util.List;

public class ViewAllActivity extends AppCompatActivity {

    public static List<HorizontalProductScrollModel> horizontalProductScrollModelList;
    public static List<WishListModel> wishListModelList;
    private RecyclerView recyclerView;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recyclerView);
        gridView = findViewById(R.id.gridView);

        int layout_code = getIntent().getIntExtra("layout_code", -1);

        if (layout_code == 0) {
            recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            WishListAdapter adapter = new WishListAdapter(wishListModelList, false);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else if (layout_code == 1) {

            gridView.setVisibility(View.VISIBLE);

            GridProductLayoutAdaptor gridProductLayoutAdaptor = new GridProductLayoutAdaptor(horizontalProductScrollModelList);
            gridView.setAdapter(gridProductLayoutAdaptor);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
