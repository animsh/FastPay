package com.asm.fastpay.users.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.asm.fastpay.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDescriptionFragment extends Fragment {


    public String body;
    private TextView descriptionBody;

    public ProductDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_description, container, false);
        descriptionBody = view.findViewById(R.id.tv_product_description);
        descriptionBody.setText(body);
        return view;
    }

}
