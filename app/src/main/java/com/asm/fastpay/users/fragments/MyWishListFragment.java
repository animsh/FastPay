package com.asm.fastpay.users.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.R;
import com.asm.fastpay.adapters.WishListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyWishListFragment extends Fragment {


    public static WishListAdapter wishListAdapter;
    public static RecyclerView wishListRecyclerView;
    private Dialog loadingDialog;
    public static ImageView emptyWishListIcon;
    public static TextView tvEmptyWishList;

    private TextView emptyTv1, emptyTv2;
    private ImageView emptyImg;

    public MyWishListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_wish_list, container, false);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        emptyImg = view.findViewById(R.id.empty_cart_imag);
        emptyTv1 = view.findViewById(R.id.empty_cart_tv1);
        emptyTv2 = view.findViewById(R.id.empty_cart_tv2);

        wishListRecyclerView = view.findViewById(R.id.my_wish_list_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        wishListRecyclerView.setLayoutManager(linearLayoutManager);


        wishListAdapter = new WishListAdapter(DBQueries.wishListModelList, true);
        wishListRecyclerView.setAdapter(wishListAdapter);
        wishListAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        wishListAdapter.notifyDataSetChanged();
        if (DBQueries.wishListModelList.size() == 0) {
            DBQueries.wishList.clear();
            DBQueries.loadWishList(getContext(), loadingDialog, true);


        } else {
            loadingDialog.dismiss();
        }


        if(DBQueries.wishListSize == 0){
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
