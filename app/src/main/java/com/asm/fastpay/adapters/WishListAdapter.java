package com.asm.fastpay.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.R;
import com.asm.fastpay.models.WishListModel;
import com.asm.fastpay.users.activities.ProductDetailsActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private boolean fromSearch;
    private List<WishListModel> wishListModelList;
    private boolean wishList;
    private int lastPosition = -1;

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public WishListAdapter(List<WishListModel> wishListModelList, boolean wishList) {
        this.wishListModelList = wishListModelList;
        this.wishList = wishList;
    }

    public List<WishListModel> getWishListModelList() {
        return wishListModelList;
    }

    public void setWishListModelList(List<WishListModel> wishListModelList) {
        this.wishListModelList = wishListModelList;
    }

    @NonNull
    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishListAdapter.ViewHolder holder, int position) {
        String productID = wishListModelList.get(position).getProductID();
        String resource = wishListModelList.get(position).getProductImage();
        String title = wishListModelList.get(position).getProductTitle();
        long freeCoupons = wishListModelList.get(position).getFreeCoupons();
        String rating = wishListModelList.get(position).getRating();
        long totalRating = wishListModelList.get(position).getTotalRatings();
        String productPrice = wishListModelList.get(position).getProductPrice();
        String cuttedPrice = wishListModelList.get(position).getCuttedPrice();
        boolean paymentMethod = wishListModelList.get(position).isCOD();
        boolean inStock = wishListModelList.get(position).isInStock();

        holder.setData(productID, resource, title, freeCoupons, rating, totalRating, productPrice, cuttedPrice, paymentMethod, position, inStock);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return wishListModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView freeCoupons;
        private ImageView couponIcon;
        private TextView rating;
        private TextView totalRatings;
        private View priceCut;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView paymentMethod;
        private ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCoupons = itemView.findViewById(R.id.free_coupon);
            couponIcon = itemView.findViewById(R.id.coupon_icon);
            rating = itemView.findViewById(R.id.tv_product_rating_miniview);
            totalRatings = itemView.findViewById(R.id.total_ratings);
            priceCut = itemView.findViewById(R.id.price_cut);
            productPrice = itemView.findViewById(R.id.cuttes_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            paymentMethod = itemView.findViewById(R.id.payment_method);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }

        private void setData(final String productID, String resource, String title, long freeCouponsNo, String averageRate, final long totalRatingsNo, String price, String cuttedPriceValue, boolean paytMethod, final int index, final boolean inStock) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder_icon)).into(productImage);
            productTitle.setText(title);
            if (freeCouponsNo != 0 & inStock) {
                couponIcon.setVisibility(View.VISIBLE);
                if (freeCouponsNo == 1) {
                    freeCoupons.setText("free " + freeCouponsNo + " coupon");
                } else {
                    freeCoupons.setText("free " + freeCouponsNo + " coupons");
                }
            } else {
                couponIcon.setVisibility(View.INVISIBLE);
                freeCoupons.setVisibility(View.INVISIBLE);
            }
            rating.setText(averageRate);
            totalRatings.setText(" ( " + totalRatingsNo + " total ratings) ");
            productPrice.setText("Rs. " + price + " /- ");
            cuttedPrice.setText("Rs. " + cuttedPriceValue + " /- ");
            if (!inStock) {
                paymentMethod.setText("Out of Stock");
                paymentMethod.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
            } else {
                paymentMethod.setText("Cash On Delivery Available");
                paymentMethod.setTextColor(itemView.getContext().getResources().getColor(R.color.intro_description_color));
            }
            if (paytMethod) {
                paymentMethod.setVisibility(View.VISIBLE);
            } else {
                paymentMethod.setVisibility(View.INVISIBLE);
            }

            if (wishList) {
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.GONE);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_wishlist_query) {
                        ProductDetailsActivity.running_wishlist_query = true;
                        DBQueries.removeFromWishList(index, itemView.getContext());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fromSearch) {
                        ProductDetailsActivity.fromSearch = true;
                    }
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID", productID);

                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }
}
