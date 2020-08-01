package com.asm.fastpay.users.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.users.activities.DeliveryActivity;
import com.asm.fastpay.R;
import com.asm.fastpay.adapters.CartAdapter;
import com.asm.fastpay.models.CartItemModel;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private RecyclerView cartItemrecyclerView;
    private Button continueBtn;

    private RadioGroup locations;
    private RadioButton home, shop;
    private Button cancelBtn, dialogContinueBtn;

    public static CartAdapter cartAdapter;
    private Dialog loadingDialog, locationDialog;
    private TextView totalAmount;

    private TextView emptyTv1, emptyTv2;
    private ImageView emptyImg;

    public CartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);


        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        locationDialog = new Dialog(getContext());
        locationDialog.setContentView(R.layout.location_dialog);
        locationDialog.setCancelable(false);
        locationDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        locationDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cartItemrecyclerView = view.findViewById(R.id.cart_items_recyclerview);
        continueBtn = view.findViewById(R.id.cart_continue_btn);
        totalAmount = view.findViewById(R.id.total_cart_amount);

        locations = locationDialog.findViewById(R.id.locations);
        home = locationDialog.findViewById(R.id.home);
        shop = locationDialog.findViewById(R.id.shop);
        cancelBtn = locationDialog.findViewById(R.id.cancel_dialog);
        dialogContinueBtn = locationDialog.findViewById(R.id.continue_dialog);

        emptyImg = view.findViewById(R.id.empty_cart_imag);
        emptyTv1 = view.findViewById(R.id.empty_cart_tv1);
        emptyTv2 = view.findViewById(R.id.empty_cart_tv2);

        emptyImg.setVisibility(View.INVISIBLE);
        emptyTv1.setVisibility(View.INVISIBLE);
        emptyTv2.setVisibility(View.INVISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemrecyclerView.setLayoutManager(layoutManager);


        cartAdapter = new CartAdapter(DBQueries.cartItemModelList, totalAmount, true);
        cartItemrecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDialog.show();

                dialogContinueBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (home.isChecked()) {
                            DBQueries.atHome = true;
                        } else if (shop.isChecked()) {
                            DBQueries.atHome = false;
                        }
                        if (!home.isChecked() && !shop.isChecked()) {
                            Toast.makeText(getContext(), "Please select your location!!", Toast.LENGTH_SHORT).show();
                        } else {
                            DeliveryActivity.cartItemModelList = new ArrayList<>();
                            DeliveryActivity.fromCart = true;
                            for (int x = 0; x < DBQueries.cartItemModelList.size(); x++) {
                                CartItemModel cartItemModel = DBQueries.cartItemModelList.get(x);
                                if (cartItemModel.isInStock()) {
                                    DeliveryActivity.cartItemModelList.add(cartItemModel);
                                }
                            }
                            DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                            loadingDialog.show();
                            if(DBQueries.atHome) {
                                if (DBQueries.addressesModelList.size() == 0) {
                                    DBQueries.loadAddresses(getContext(), loadingDialog, true);
                                } else {
                                    loadingDialog.dismiss();
                                    Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                                    startActivity(deliveryIntent);
                                }
                            } else {
                                loadingDialog.dismiss();
                                Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                                startActivity(deliveryIntent);
                            }
                        }
                        locationDialog.dismiss();
                    }

                });
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDialog.dismiss();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();
        if (DBQueries.cartItemModelList.size() == 0) {
            DBQueries.cartList.clear();
            DBQueries.loadCartList(getContext(), loadingDialog, true, new TextView(getContext()), totalAmount);
        } else {
            if (DBQueries.cartItemModelList.get(DBQueries.cartItemModelList.size() - 1).getType() == CartItemModel.TOTAL_AMOUNT) {
                LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }

        if(DBQueries.cartListSize == 0){
            emptyImg.setVisibility(View.VISIBLE);
            emptyTv1.setVisibility(View.VISIBLE);
            emptyTv2.setVisibility(View.VISIBLE);
        } else {
            emptyImg.setVisibility(View.INVISIBLE);
            emptyTv1.setVisibility(View.INVISIBLE);
            emptyTv2.setVisibility(View.INVISIBLE);
        }


    }
}
