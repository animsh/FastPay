package com.asm.fastpay.admin;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asm.fastpay.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineOrdersFragment extends Fragment {

    private RecyclerView myOrdersIDRecyclerView;
    public static OrderIDAdapter orderIDAdapter;
    private ImageView emptyImg;
    private TextView emptyTv;
    private Dialog loadingDialog;

    public OnlineOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_orders, container, false);

        emptyImg = view.findViewById(R.id.order_id_not_available_img);
        emptyTv = view.findViewById(R.id.order_not_found_tv);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        myOrdersIDRecyclerView = view.findViewById(R.id.order_manager_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        myOrdersIDRecyclerView.setLayoutManager(layoutManager);


        orderIDAdapter = new OrderIDAdapter(AdminDBQueries.orderIDModelList);
        myOrdersIDRecyclerView.setAdapter(orderIDAdapter);

        AdminDBQueries.loadOrdersID(getContext(), orderIDAdapter,loadingDialog);

        if(!AdminDBQueries.orderIDAvailable){
            Toast.makeText(getContext(),"No Orders Found!........",Toast.LENGTH_SHORT).show();
            myOrdersIDRecyclerView.setVisibility(View.GONE);
            emptyImg.setVisibility(View.VISIBLE);
            emptyTv.setVisibility(View.VISIBLE);
        } else {
            myOrdersIDRecyclerView.setVisibility(View.VISIBLE);
            emptyImg.setVisibility(View.GONE);
            emptyTv.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        orderIDAdapter.notifyDataSetChanged();
    }
}
