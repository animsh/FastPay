package com.asm.fastpay.admin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asm.fastpay.R;
import com.asm.fastpay.adapters.WishListAdapter;
import com.asm.fastpay.fragments.homefragment.StaggeredRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    public ProductsFragment() {
        // Required empty public constructor
    }

    private RecyclerView productsRecyclerView;
    public static WishListAdapter productAdapter;
    private Button addNewProducts;

    public  static StaggeredRecyclerAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private GridLayoutManager gridLayoutManager;
    private Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        addNewProducts = view.findViewById(R.id.add_product_btn);

       /* LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        productsRecyclerView.setLayoutManager(linearLayoutManager);

        productAdapter = new WishListAdapter(AdminDBQueries.productModelList, false);
        productsRecyclerView.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();*/

        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        productsRecyclerView.setLayoutManager(manager);

        adapter = new StaggeredRecyclerAdapter(getContext(), AdminDBQueries.straggeredRecyclerModelList);
        productsRecyclerView.setAdapter(adapter);

        addNewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewProduct = new Intent(getContext(), AddProductActivity.class);
                getContext().startActivity(addNewProduct);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*productAdapter.notifyDataSetChanged();*/
        AdminDBQueries.straggeredRecyclerModelList.clear();
        adapter.notifyDataSetChanged();
        AdminDBQueries.loadProducts(getContext(),loadingDialog);
    }

}
