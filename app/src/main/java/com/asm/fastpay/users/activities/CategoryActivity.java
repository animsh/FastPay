package com.asm.fastpay.users.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.R;
import com.asm.fastpay.adapters.HomePageAdapter;
import com.asm.fastpay.models.HomePageModel;
import com.asm.fastpay.models.SliderModel;

import java.util.ArrayList;
import java.util.List;

import static com.asm.fastpay.DBQueries.lists;
import static com.asm.fastpay.DBQueries.loadedCategoryName;


public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private HomePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String title = getIntent().getStringExtra("categoryName");

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryRecyclerView = findViewById(R.id.category__recyclerview);

        List<SliderModel> sliderModelList = new ArrayList<SliderModel>();

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(this);
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(testingLayoutManager);

        int listPosition = 0;
        for (int x = 0; x < loadedCategoryName.size(); x++) {
            if (loadedCategoryName.get(x).equals(title.toUpperCase())) {
                listPosition = x;
            }
        }

        if (listPosition == 0) {
            loadedCategoryName.add(title.toUpperCase());
            lists.add(new ArrayList<HomePageModel>());
            adapter = new HomePageAdapter(lists.get(loadedCategoryName.size() - 1));
            DBQueries.loadFragmentData(categoryRecyclerView, this, loadedCategoryName.size() - 1, title);
        } else {
            adapter = new HomePageAdapter(lists.get(listPosition));
        }

        categoryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.btn_search) {
            Intent searchIntent = new Intent(CategoryActivity.this, SearchActivity.class);
            startActivity(searchIntent);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
