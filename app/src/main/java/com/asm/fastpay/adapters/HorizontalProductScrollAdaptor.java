package com.asm.fastpay.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.asm.fastpay.models.HorizontalProductScrollModel;
import com.asm.fastpay.users.activities.ProductDetailsActivity;
import com.asm.fastpay.R;

import java.util.List;

public class HorizontalProductScrollAdaptor extends RecyclerView.Adapter<HorizontalProductScrollAdaptor.ViewHolder> {

    private List<HorizontalProductScrollModel> horizontalProductScrollModelsList;

    public HorizontalProductScrollAdaptor(List<HorizontalProductScrollModel> horizontalProductScrollModels) {
        this.horizontalProductScrollModelsList = horizontalProductScrollModels;
    }

    @NonNull
    @Override
    public HorizontalProductScrollAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdaptor.ViewHolder holder, int position) {
        String resource = horizontalProductScrollModelsList.get(position).getProductImage();
        String title = horizontalProductScrollModelsList.get(position).getProductTitle();
        String description = horizontalProductScrollModelsList.get(position).getProductDescription();
        String price = horizontalProductScrollModelsList.get(position).getProductPrice();
        String productID = horizontalProductScrollModelsList.get(position).getProductID();

        holder.setData(productID, resource, title, description, price);
    }

    @Override
    public int getItemCount() {
        if (horizontalProductScrollModelsList.size() > 8) {
            return 8;
        } else {
            return horizontalProductScrollModelsList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productDescription;
        private TextView productPrice;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.h_s_product_image);
            productTitle = itemView.findViewById(R.id.h_s_product_title);
            productDescription = itemView.findViewById(R.id.h_s_product_description);
            productPrice = itemView.findViewById(R.id.h_s_product_price);


        }

        private void setData(final String productID, String resource, String title, String description, String price) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions()).placeholder(R.drawable.placeholder_icon).into(productImage);
            productTitle.setText(title);
            productDescription.setText(description);
            productPrice.setText("Rs. " + price + " /-");

            if (!title.equals("")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                        productDetailsIntent.putExtra("PRODUCT_ID", productID);
                        itemView.getContext().startActivity(productDetailsIntent);
                    }
                });
            }
        }
    }
}
