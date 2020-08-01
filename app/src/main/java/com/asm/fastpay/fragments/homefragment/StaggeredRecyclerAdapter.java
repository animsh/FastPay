package com.asm.fastpay.fragments.homefragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.R;
import com.asm.fastpay.admin.AddProductActivity;
import com.asm.fastpay.users.activities.MainActivity;
import com.asm.fastpay.users.activities.ProductDetailsActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class StaggeredRecyclerAdapter extends RecyclerView.Adapter<StaggeredRecyclerAdapter.ViewHolder> {

    Context context;
    List<StraggeredRecyclerModel> mdata;

    public StaggeredRecyclerAdapter(Context context, List<StraggeredRecyclerModel> mdata) {
        this.context = context;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String productID = mdata.get(position).getProductID();
        String resource = mdata.get(position).getProductImage();
        String title = mdata.get(position).getProductTitle();
        String details = mdata.get(position).getProductSub();
        String price = mdata.get(position).getProductPrice();

        holder.setData(productID, resource, title, details, price);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productTitle;
        TextView productSub;
        TextView productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productSub = itemView.findViewById(R.id.product_sub_title);
            productPrice = itemView.findViewById(R.id.product_price);
        }

        private void setData(final String pID, String resource, String title, String details, String price) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder_icon)).into(productImage);
            productTitle.setText(title);
            productSub.setText(details);
            productPrice.setText(price);

            SharedPreferences pref = itemView.getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
            Boolean isAdminLogin = pref.getBoolean("isAdminLogin", false);
            if (!isAdminLogin) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                        productDetailsIntent.putExtra("PRODUCT_ID", pID);
                        Log.e("Admin", ": " + "False" );
                        itemView.getContext().startActivity(productDetailsIntent);
                    }
                });
            } else {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productDetailsIntent = new Intent(itemView.getContext(), AddProductActivity.class);
                        productDetailsIntent.putExtra("PRODUCT_ID", pID);
                        productDetailsIntent.putExtra("UPDATE",true);
                        Log.e("Admin", ": " + "True" );
                        itemView.getContext().startActivity(productDetailsIntent);
                    }
                });
            }
        }
    }
}
