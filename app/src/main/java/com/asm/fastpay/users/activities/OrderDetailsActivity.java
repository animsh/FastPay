package com.asm.fastpay.users.activities;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.R;
import com.asm.fastpay.models.MyOrderItemModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    private int position;

    private TextView title, price, quantity;
    private ImageView productImage, orderedIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar O_P_progress, P_S_progress, S_D_progress;
    private TextView orderedTitle, packedTitle, shippedTitle, deliveredTitle;
    private TextView orderedDate, packedDate, shippedDate, deliveredDate;
    private TextView orderedBody, packedBody, shippedBody, deliveredBody;
    private LinearLayout rateNowContainer;
    private int rating;
    private TextView fullName, address, pincode;
    private TextView totalItems, totalItemsPrice, deliveryprice, totalAmount, savedAmount;
    private Dialog loadingDialog, cancelOrderDialog;
    private SimpleDateFormat simpleDateFormat;
    private Button cancelOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        setContentView(R.layout.activity_order_details);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cancelOrderDialog = new Dialog(this);
        cancelOrderDialog.setContentView(R.layout.order_cancel_dialog);
        cancelOrderDialog.setCancelable(true);
        cancelOrderDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        //cancelOrderDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        position = getIntent().getIntExtra("Position", -1);
        final MyOrderItemModel model = DBQueries.myOrderItemModelList.get(position);

        title = findViewById(R.id.product_title);
        price = findViewById(R.id.product_price);
        quantity = findViewById(R.id.product_quantity);

        cancelOrderBtn = findViewById(R.id.order_cancel_btn);
        productImage = findViewById(R.id.product_image);
        orderedIndicator = findViewById(R.id.ordered_indicator);
        packedIndicator = findViewById(R.id.packed_indicator);
        shippedIndicator = findViewById(R.id.shipping_indicator);
        deliveredIndicator = findViewById(R.id.delivered_indicator);

        O_P_progress = findViewById(R.id.ordered_packed_progress);
        P_S_progress = findViewById(R.id.packed_shipping_progress);
        S_D_progress = findViewById(R.id.shipping_delivered_progress);

        orderedTitle = findViewById(R.id.ordered_title);
        packedTitle = findViewById(R.id.packed_title);
        shippedTitle = findViewById(R.id.shipping_title);
        deliveredTitle = findViewById(R.id.delivered_title);

        orderedDate = findViewById(R.id.textView21);
        packedDate = findViewById(R.id.packed_date);
        shippedDate = findViewById(R.id.shipping_date);
        deliveredDate = findViewById(R.id.delivered_date);

        orderedBody = findViewById(R.id.ordered_body);
        packedBody = findViewById(R.id.packed_body);
        shippedBody = findViewById(R.id.shipping_body);
        deliveredBody = findViewById(R.id.delivered_body);

        rateNowContainer = findViewById(R.id.rate_now_container);

        fullName = findViewById(R.id.fullname);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);

        totalItems = findViewById(R.id.total_items);
        totalItemsPrice = findViewById(R.id.total_items_price);
        deliveryprice = findViewById(R.id.delievery_charge);
        totalAmount = findViewById(R.id.total_price);
        savedAmount = findViewById(R.id.saved_amount);

        title.setText(model.getProductTitle());
        price.setText("Rs. " + model.getProductPrice() + "/-");
        quantity.setText(String.valueOf("Quantity: " + model.getProductQuantity()));
        Glide.with(this).load(model.getProductImage()).into(productImage);

        simpleDateFormat = new SimpleDateFormat("EEE,dd MMM YYYY hh:mm aa");

        switch (model.getOrderStatus()) {
            case "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                O_P_progress.setVisibility(View.GONE);
                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;

            case "Packed":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    O_P_progress.setProgress(100, true);
                }

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;

            case "Shipped":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    O_P_progress.setProgress(100, true);
                    P_S_progress.setProgress(100, true);
                }

                S_D_progress.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;

            case "Out for Delivery":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    O_P_progress.setProgress(100, true);
                    P_S_progress.setProgress(100, true);
                    S_D_progress.setProgress(100, true);
                }

                deliveredTitle.setText("Out for Delivery");
                deliveredBody.setText("Your order is out for delivery.");
                break;
            case "Delivered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    O_P_progress.setProgress(100, true);
                    P_S_progress.setProgress(100, true);
                    S_D_progress.setProgress(100, true);
                }
                break;

            case "Cancelled":

                if (model.getPackedDate().after(model.getOrderedDate())) {
                    if (model.getShippedDate().after(model.getPackedDate())) {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        deliveredTitle.setText("Cancelled");
                        deliveredBody.setText("Your order has been cancelled.");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            O_P_progress.setProgress(100, true);
                            P_S_progress.setProgress(100, true);
                            S_D_progress.setProgress(100, true);
                        }
                    } else {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        shippedTitle.setText("Cancelled");
                        shippedBody.setText("Your order has been cancelled.");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            O_P_progress.setProgress(100, true);
                            P_S_progress.setProgress(100, true);
                        }

                        S_D_progress.setVisibility(View.GONE);

                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredBody.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);
                    }
                } else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                    packedTitle.setText("Cancelled");
                    packedBody.setText("Your order has been cancelled.");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        O_P_progress.setProgress(100, true);
                    }

                    P_S_progress.setVisibility(View.GONE);
                    S_D_progress.setVisibility(View.GONE);

                    shippedIndicator.setVisibility(View.GONE);
                    shippedBody.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                }
                break;
        }

        rating = model.getRating();
        setRating(rating);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDialog.show();
                    setRating(starPosition);
                    final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS").document(model.getProductID());

                    FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                        @Nullable
                        @Override
                        public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                            if (rating != 0) {
                                long increase = documentSnapshot.getLong(starPosition + 1 + "_star") + 1;
                                long decrease = documentSnapshot.getLong(rating + 1 + "_star") - 1;
                                transaction.update(documentReference, starPosition + 1 + "_star", increase);
                                transaction.update(documentReference, rating + 1 + "_star", decrease);

                            } else {
                                long increase = documentSnapshot.getLong(starPosition + 1 + "_star") + 1;
                                transaction.update(documentReference, starPosition + 1 + "_star", increase);
                            }
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            DBQueries.myOrderItemModelList.get(position).setRating(starPosition + 1);
                            Map<String, Object> myRating = new HashMap<>();
                            if (DBQueries.myRatedIds.contains(model.getProductID())) {
                                myRating.put("rating_" + DBQueries.myRatedIds.indexOf(model.getProductID()), starPosition + 1);

                            } else {
                                myRating.put("list_size", (long) DBQueries.myRatedIds.size() + 1);
                                myRating.put("product_ID_" + DBQueries.myRatedIds.size(), model.getProductID());
                                myRating.put("rating_" + DBQueries.myRating.size(), (long) starPosition + 1);
                            }

                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        DBQueries.myOrderItemModelList.get(position).setRating(starPosition);
                                        if (DBQueries.myRatedIds.contains(model.getProductID())) {
                                            DBQueries.myRating.set(DBQueries.myRatedIds.indexOf(model.getProductID()), Long.parseLong(String.valueOf(starPosition + 1)));
                                        } else {
                                            DBQueries.myRatedIds.add(model.getProductID());
                                            DBQueries.myRating.add(Long.parseLong(String.valueOf(starPosition + 1)));
                                        }
                                    }else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(OrderDetailsActivity.this,"Error: " + error, Toast.LENGTH_SHORT).show();
                                    }loadingDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            });
        }

        if(model.isCancellationRequested()){
            cancelOrderBtn.setVisibility(View.VISIBLE);
            cancelOrderBtn.setEnabled(false);
            cancelOrderBtn.setText(" Order Cancelled");
            cancelOrderBtn.setTextColor(getResources().getColor(R.color.flipkart));
            cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        }else {
            if(model.getOrderStatus().equals("Ordered") || model.getOrderStatus().equals("Packed")){
                cancelOrderBtn.setVisibility(View.VISIBLE);
                cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrderDialog.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelOrderDialog.dismiss();
                            }
                        });
                        cancelOrderDialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelOrderDialog.dismiss();
                                loadingDialog.show();
                                Map<String,Object> map = new HashMap<>();
                                map.put("Order Id",model.getOrderID());
                                map.put("Product Id",model.getProductID());
                                map.put("Order Cancelled", false);

                                FirebaseFirestore.getInstance().collection("CANCELLED ORDERS").document().set(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Log.d("OrderID", "onComplete: " + String.valueOf(model.getOrderID()));
                                                    Log.d("ProductID", "onComplete: " + String.valueOf(model.getProductID()));
                                                    FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderID()).collection("OrderItems").document(model.getProductID()).update("Cancellation Request",true)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        model.setCancellationRequested(true);
                                                                        cancelOrderBtn.setEnabled(false);
                                                                        cancelOrderBtn.setText("Cancellation in Progress...");
                                                                        cancelOrderBtn.setTextColor(getResources().getColor(R.color.flipkart));
                                                                        cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                                                    }else {
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(OrderDetailsActivity.this,"Error: "+ error,Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    loadingDialog.dismiss();
                                                                }
                                                            });
                                                }else {
                                                    loadingDialog.dismiss();
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(OrderDetailsActivity.this,"Error: "+ error,Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                        cancelOrderDialog.show();
                    }
                });
            }
        }

        fullName.setText(model.getFullName());
        address.setText(model.getAddress());
        pincode.setText(model.getPincode());

        long totalItemsPriceValue;
        totalItemsPriceValue = model.getProductQuantity() * Long.valueOf(model.getProductPrice());

        totalItems.setText("Price (" + model.getProductQuantity() + " items)");
        totalItemsPrice.setText("Rs. " + totalItemsPriceValue  + "/-");
        Log.d("Delivery:", String.valueOf(model.getDeliveryPrice()));
        if (model.getDeliveryPrice().equals("FREE")){
            deliveryprice.setText(model.getDeliveryPrice());
            totalAmount.setText(totalItemsPrice.getText());
        }else {
            deliveryprice.setText("Rs. " + model.getDeliveryPrice() + "/-");
            totalAmount.setText("Rs. " + (totalItemsPriceValue + Long.valueOf(model.getDeliveryPrice())) + "/-");
        }

        /*if (totalItemsPriceValue > 500){
            deliveryprice.setText("FREE");
            totalAmount.setText(totalItemsPrice.getText());
        }else {
            deliveryprice.setText("Rs. 60/-");
            totalAmount.setText("Rs. " + (totalItemsPriceValue + 60) + "/-");
        }*/

        if(!model.getCuttedPrice().equals("")){
            savedAmount.setText("You have saved Rs." + model.getProductQuantity()*(Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getProductPrice())) + "/- on this order");
        }else {
            savedAmount.setText("You have saved Rs.0/- on this order");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#656565")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#21bf73")));
            }
        }
    }

}
