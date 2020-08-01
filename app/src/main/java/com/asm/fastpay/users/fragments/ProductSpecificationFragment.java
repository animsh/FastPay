package com.asm.fastpay.users.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.R;
import com.asm.fastpay.adapters.ProductSpecificationAdapter;
import com.asm.fastpay.models.ProductSpecificationModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductSpecificationFragment extends Fragment {

    public List<ProductSpecificationModel> productSpecificationModelList;
    private RecyclerView productSpecificationRecyclerView;


    public ProductSpecificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_specification, container, false);

        productSpecificationRecyclerView = view.findViewById(R.id.product_specification_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        productSpecificationRecyclerView.setLayoutManager(linearLayoutManager);

       /* productSpecificationModelList.add(new ProductSpecificationModel(0, "General"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(0, "Display"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(0, "General"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(0, "Display"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));
        productSpecificationModelList.add(new ProductSpecificationModel(1, "Quantity", "1 KG"));*/


        ProductSpecificationAdapter productSpecificationAdapter = new ProductSpecificationAdapter(productSpecificationModelList);
        productSpecificationRecyclerView.setAdapter(productSpecificationAdapter);
        productSpecificationAdapter.notifyDataSetChanged();
        return view;
    }

}
