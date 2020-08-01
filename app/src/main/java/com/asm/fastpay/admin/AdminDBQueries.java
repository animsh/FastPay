package com.asm.fastpay.admin;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asm.fastpay.adapters.MyOrderAdapter;
import com.asm.fastpay.fragments.homefragment.StraggeredRecyclerModel;
import com.asm.fastpay.models.MyOrderItemModel;
import com.asm.fastpay.models.WishListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminDBQueries {

    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static List<OrderIDModel> orderIDModelList = new ArrayList<>();

    public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();

    public static List<WishListModel> productModelList = new ArrayList<>();

    public static List<StraggeredRecyclerModel> straggeredRecyclerModelList = new ArrayList<>();

    public static boolean orderIDAvailable = false;

    public static void loadOrdersID(final Context context, @Nullable final OrderIDAdapter orderIDAdapter, final Dialog dialog) {
        orderIDModelList.clear();
        dialog.show();
        firebaseFirestore.collection("ORDERS").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot orderIDs : task.getResult().getDocuments()) {
                                final OrderIDModel myOrderItemModel = new OrderIDModel(orderIDs.getId());
                                orderIDModelList.add(myOrderItemModel);
                                if(orderIDModelList.size() > 0){
                                    orderIDAvailable = true;
                                } else {
                                    orderIDAvailable = false;
                                }
                            }
                            if (orderIDAdapter != null) {
                                orderIDAdapter.notifyDataSetChanged();
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    public static void loadOrders(final Context context, @Nullable final MyOrderAdapter myOrderAdapter, boolean loadUserOrders, final String orderID) {
        myOrderItemModelList.clear();
        if (loadUserOrders) {
            firebaseFirestore.collection("ORDERS").document(orderID).collection("OrderItems").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("Orders: ", "Loding order " + orderID);
                                for (DocumentSnapshot orderItems : task.getResult().getDocuments()) {
                                    final MyOrderItemModel myOrderItemModel = new MyOrderItemModel(orderItems.getString("Product ID")
                                            , orderItems.getString("Order Status")
                                            , orderItems.getString("Address")
                                            , orderItems.getString("Cutted Price")
                                            , orderItems.getDate("Ordered Date")
                                            , orderItems.getDate("Packed Date")
                                            , orderItems.getDate("Shipped Date")
                                            , orderItems.getDate("Delivered Date")
                                            , orderItems.getDate("Cancelled Date")
                                            , orderItems.getString("Full Name")
                                            , orderItems.getString("ORDER ID")
                                            , orderItems.getString("Payment Method")
                                            , orderItems.getString("Pincode")
                                            , orderItems.getString("Product Price")
                                            , orderItems.getLong("Product Quantity")
                                            , orderItems.getString("User ID")
                                            , orderItems.getString("Product Image")
                                            , orderItems.getString("Product Title")
                                            , orderItems.getString("Delivery Price")
                                            , orderItems.getBoolean("Cancellation Request"));

                                    myOrderItemModelList.add(myOrderItemModel);
                                }
                                if (myOrderAdapter != null) {
                                    myOrderAdapter.notifyDataSetChanged();
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public static void loadProducts(final Context context, final Dialog dialog) {
        straggeredRecyclerModelList.clear();
        dialog.show();
        firebaseFirestore.collection("PRODUCTS").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final DocumentSnapshot productIDs : task.getResult().getDocuments()) {
                                final String IDS = productIDs.getId();
                                firebaseFirestore.collection("PRODUCTS").document(IDS)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            final DocumentSnapshot documentSnapshot = task.getResult();
                                            FirebaseFirestore.getInstance().collection("PRODUCTS").document(IDS).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                    straggeredRecyclerModelList.add(new StraggeredRecyclerModel(IDS
                                                                            , documentSnapshot.get("product_image_1").toString()
                                                                            , documentSnapshot.get("product_sub_title").toString()
                                                                            , documentSnapshot.get("product_sub_details").toString()
                                                                            , documentSnapshot.get("product_price").toString()));
                                                                } else {
                                                                    straggeredRecyclerModelList.add(new StraggeredRecyclerModel(IDS
                                                                            , documentSnapshot.get("product_image_1").toString()
                                                                            , documentSnapshot.get("product_sub_title").toString()
                                                                            , documentSnapshot.get("product_sub_details").toString()
                                                                            , documentSnapshot.get("product_price").toString()));
                                                                }
                                                                if (ProductsFragment.adapter != null) {
                                                                    ProductsFragment.adapter.notifyDataSetChanged();
                                                                }
                                                                dialog.dismiss();
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            dialog.dismiss();
                                            String error = task.getException().getMessage();
                                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }else {
                            dialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void clearAdminData(){
        orderIDModelList.clear();
        myOrderItemModelList.clear();
        straggeredRecyclerModelList.clear();
    }

}

