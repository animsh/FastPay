package com.asm.fastpay.fragments.homefragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class VegetableFragment extends Fragment {

    private RecyclerView staggeredRecyclerView;
    public static StaggeredRecyclerAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private GridLayoutManager gridLayoutManager;
    private Dialog loadingDialog;
    private List<StraggeredRecyclerModel> straggeredRecyclerModelList = new ArrayList<>();

    public VegetableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_vegetable, container, false);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        staggeredRecyclerView = view.findViewById(R.id.straggered_veg);
        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        staggeredRecyclerView.setLayoutManager(manager);

        adapter = new StaggeredRecyclerAdapter(getContext(),straggeredRecyclerModelList);
        staggeredRecyclerView.setAdapter(adapter);

        loadingDialog.dismiss();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DBQueries.loadTabProducts(getContext(),"biscuit",adapter,loadingDialog,straggeredRecyclerModelList);
        adapter.notifyDataSetChanged();
    }
}
