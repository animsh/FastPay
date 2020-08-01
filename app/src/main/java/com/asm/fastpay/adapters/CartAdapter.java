package com.asm.fastpay.adapters;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.asm.fastpay.models.CartItemModel;
import com.asm.fastpay.DBQueries;
import com.asm.fastpay.users.activities.DeliveryActivity;
import com.asm.fastpay.users.activities.HomePageActivity;
import com.asm.fastpay.users.activities.ProductDetailsActivity;
import com.asm.fastpay.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;
    private int lastPosition = -1;
    private TextView cartTotalAmount;
    private boolean showDeleteBtn;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotalAmount, boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new CartItemViewHolder(cartItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                return new CartTotalAmountViewHolder(cartTotalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String resource = cartItemModelList.get(position).getProductImgae();
                String title = cartItemModelList.get(position).getProductTitle();
                long freeCoupon = cartItemModelList.get(position).getFreeCoupons();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                long offersApplied = cartItemModelList.get(position).getOfferApplied();
                String productId = cartItemModelList.get(position).getProductID();
                boolean inStock = cartItemModelList.get(position).isInStock();
                long productQuantity = cartItemModelList.get(position).getProductQuantity();
                long maxQuantity = cartItemModelList.get(position).getMaxQuantity();
                boolean qtyError = cartItemModelList.get(position).isQtyError();
                List<String> qtyIds = cartItemModelList.get(position).getQtyIDs();
                long stockQty = cartItemModelList.get(position).getStockQuantity();
                boolean COD = cartItemModelList.get(position).isCOD();

                ((CartItemViewHolder) holder).setItemDetails(productId, resource, title, freeCoupon, productPrice, cuttedPrice, offersApplied, position, inStock, String.valueOf(productQuantity), maxQuantity, qtyError, qtyIds, stockQty, COD);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems = 0;
                int totalItemPrice = 0;
                String deliveryPrice;
                int totalAmount;
                String oPrice = cartItemModelList.get(position).getProductPrice();
                String cPrice = cartItemModelList.get(position).getCuttedPrice();
                int savedAmount = 0;

                for (int x = 0; x < cartItemModelList.size(); x++) {
                    int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()) {
                        totalItems = totalItems + quantity;
                        totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice()) * quantity;
                    }

                    if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())) {
                        savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getCuttedPrice()) - Integer.parseInt(cartItemModelList.get(x).getProductPrice())) * quantity;
                    }
                }

                if (DBQueries.atHome) {
                    if (totalItemPrice > 500) {
                        deliveryPrice = "FREE";
                        totalAmount = totalItemPrice;
                    } else {
                        deliveryPrice = "60";
                        totalAmount = totalItemPrice + 60;
                    }
                } else {
                    deliveryPrice = "FREE";
                    totalAmount = totalItemPrice;
                }
                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);

                ((CartTotalAmountViewHolder) holder).setTotalAmount(totalItems, totalItemPrice, deliveryPrice, totalAmount, savedAmount);
                break;
            default:
                return;
        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private ImageView freeCouponIcon;
        private TextView productTitle;
        private TextView productPrice;
        private TextView freeCoupons;
        private TextView cuttedPrice;
        private TextView offerApplied;
        private TextView couponsApplied;
        private TextView productQuantity;
        private LinearLayout couponRedemptionLayout;

        private LinearLayout deleteBtn;
        private TextView codIndi;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCouponIcon = itemView.findViewById(R.id.free_coupen_icon);
            freeCoupons = itemView.findViewById(R.id.tv_free_coupen);
            productPrice = itemView.findViewById(R.id.cuttes_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            offerApplied = itemView.findViewById(R.id.offer_applied);
            couponsApplied = itemView.findViewById(R.id.coupon_applied);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            deleteBtn = itemView.findViewById(R.id.remove_item_button);
            couponRedemptionLayout = itemView.findViewById(R.id.coupon_redemption_layout);
            codIndi = itemView.findViewById(R.id.cod_indi);
        }

        private void setItemDetails(final String productId, String resources, String title, long freeCouponNo, String productPriceText, String cuttedPriceText, long offerAppliedNo, final int position, final boolean inStock, final String quantity, final long maxQuantity, final boolean qtyError, final List<String> qtyIds, final long stockQty, boolean COD) {
            Glide.with(itemView.getContext()).load(resources).apply(new RequestOptions().placeholder(R.drawable.placeholder_icon)).into(productImage);
            productTitle.setText(title);

            if (COD) {
                codIndi.setVisibility(View.VISIBLE);
            } else {
                codIndi.setVisibility(View.GONE);
            }
            if (inStock) {

                if (freeCouponNo > 0) {
                    freeCouponIcon.setVisibility(View.INVISIBLE);
                    freeCoupons.setVisibility(View.INVISIBLE);
                    if (freeCouponNo == 1) {
                        freeCoupons.setText("free " + freeCouponNo + " coupons");
                    } else {
                        freeCoupons.setText("free " + freeCouponNo + " coupons");
                    }
                } else {
                    freeCouponIcon.setVisibility(View.INVISIBLE);
                    freeCoupons.setVisibility(View.INVISIBLE);
                }
                productPrice.setText("Rs. " + productPriceText + " /- ");
                cuttedPrice.setText(" Rs. " + cuttedPriceText + " /- ");
                couponRedemptionLayout.setVisibility(View.GONE);

                productQuantity.setText("Quantity: " + quantity);
                if (!showDeleteBtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.red)));
                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.intro_title_color));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.intro_title_color)));
                    }
                }
                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);
                        final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_count);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okBtn = quantityDialog.findViewById(R.id.ok_btn);

                        quantityNo.setHint("Max " + maxQuantity);
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });

                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.parseLong(quantityNo.getText().toString()) <= maxQuantity && Long.parseLong(quantityNo.getText().toString()) != 0) {
                                        if (itemView.getContext() instanceof HomePageActivity) {
                                            DBQueries.cartItemModelList.get(position).setProductQuantity(Long.parseLong(quantityNo.getText().toString()));
                                        } else {
                                            if (DeliveryActivity.fromCart) {
                                                DBQueries.cartItemModelList.get(position).setProductQuantity(Long.parseLong(quantityNo.getText().toString()));
                                            } else {
                                                if (DeliveryActivity.cartItemModelList != null) {
                                                    DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.parseLong(quantityNo.getText().toString()));
                                                }
                                            }
                                        }
                                        productQuantity.setText("Quantity: " + quantityNo.getText());
                                        notifyItemChanged(cartItemModelList.size() - 1);
                                        if (!showDeleteBtn) {
                                            DeliveryActivity.loadingDialog.show();
                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            final int initialQuatity = Integer.parseInt(quantity);
                                            final int finalQuantity = Integer.parseInt(quantityNo.getText().toString());
                                            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                            if (finalQuantity > initialQuatity) {
                                                for (int y = 0; y < finalQuantity - initialQuatity; y++) {
                                                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);
                                                    Map<String, Object> timestamp = new HashMap<>();
                                                    timestamp.put("time", FieldValue.serverTimestamp());
                                                    final int finalY = y;
                                                    firebaseFirestore.collection("PRODUCTS").document(productId).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    qtyIds.add(quantityDocumentName);
                                                                    if (finalY + 1 == finalQuantity - initialQuatity) {
                                                                        firebaseFirestore.collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(stockQty).get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            List<String> serverQuantity = new ArrayList<>();
                                                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                                                            }
                                                                                            long availableQty = 0;
                                                                                            for (String qtyId : qtyIds) {
                                                                                                if (!serverQuantity.contains(qtyId)) {
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                                    Toast.makeText(itemView.getContext(), "Sorry! all products may not be available in required quantity...", Toast.LENGTH_SHORT).show();

                                                                                                } else {
                                                                                                    availableQty++;
                                                                                                }
                                                                                            }
                                                                                            DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                                        } else {
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(itemView.getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            } else if (initialQuatity > finalQuantity) {
                                                for (int x = 0; x < initialQuatity - finalQuantity; x++) {
                                                    final String qtyID = qtyIds.get(qtyIds.size() - 1 - x);
                                                    final int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productId).collection("QUANTITY").document(qtyID).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    qtyIds.remove(qtyID);
                                                                    DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                    if (finalX + 1 == initialQuatity - finalQuantity) {
                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(itemView.getContext(), "Max Quantity: " + maxQuantity, Toast.LENGTH_LONG).show();
                                    }
                                    quantityDialog.dismiss();
                                }
                            }
                        });
                        quantityDialog.show();
                    }
                });

                if (offerAppliedNo > 0) {
                    offerApplied.setVisibility(View.INVISIBLE);
                    offerApplied.setText(offerAppliedNo + " offers applied");
                } else {
                    offerApplied.setVisibility(View.INVISIBLE);
                }
            } else {
                productPrice.setText("Rs. " + productPriceText + " /- ");
                cuttedPrice.setText(" Rs. " + cuttedPriceText + " /- ");
                couponRedemptionLayout.setVisibility(View.GONE);
                freeCoupons.setVisibility(View.VISIBLE);
                freeCoupons.setText("Out of Stock");
                couponsApplied.setVisibility(View.GONE);
                offerApplied.setVisibility(View.GONE);
                freeCouponIcon.setVisibility(View.GONE);
                productQuantity.setVisibility(View.GONE);
                productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.light_gray_background_color));
                productQuantity.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f8f8f8")));
            }

            if (showDeleteBtn) {
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.GONE);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_cart_query) {
                        ProductDetailsActivity.running_cart_query = true;
                        DBQueries.removeFromCart(position, itemView.getContext(), cartTotalAmount);
                    }
                }
            });
        }
    }

    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder {

        private TextView totalItem;
        private TextView totalItemsPrice;
        private TextView deliveryPrice;
        private TextView totalAmount;
        private TextView savedAmount;

        public CartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);

            totalItem = itemView.findViewById(R.id.total_items);
            totalItemsPrice = itemView.findViewById(R.id.total_items_price);
            deliveryPrice = itemView.findViewById(R.id.delievery_charge);
            totalAmount = itemView.findViewById(R.id.total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);
        }

        private void setTotalAmount(int totalItemText, int totalItemsPriceText, String deliveryPriceText, int totalAmountText, int savedAmountText) {
            totalItem.setText("Price(" + totalItemText + " items)");
            totalItemsPrice.setText("Rs. " + totalItemsPriceText + " /- ");

            if (deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            } else {
                deliveryPrice.setText("Rs. " + deliveryPriceText + " /-");
            }

            totalAmount.setText("Rs. " + totalAmountText + " /-");
            cartTotalAmount.setText("Rs. " + totalAmountText + " /-");
            savedAmount.setText("You saved Rs. " + savedAmountText + " /- on this order!!!");

            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if (totalItemsPriceText == 0) {
                if (DeliveryActivity.fromCart) {
                    DBQueries.cartItemModelList.remove(DBQueries.cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }
                if (showDeleteBtn) {
                    DBQueries.cartItemModelList.remove(DBQueries.cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            } else {
                parent.setVisibility(View.VISIBLE);
            }

        }
    }
}
