package com.asm.fastpay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.fastpay.adapters.CategoryAdapter;
import com.asm.fastpay.adapters.HomePageAdapter;
import com.asm.fastpay.adapters.MyOrderAdapter;
import com.asm.fastpay.models.WishListModel;
import com.asm.fastpay.fragments.homefragment.StaggeredRecyclerAdapter;
import com.asm.fastpay.fragments.homefragment.StraggeredRecyclerModel;
import com.asm.fastpay.models.AddressesModel;
import com.asm.fastpay.models.CartItemModel;
import com.asm.fastpay.models.CategoryModel;
import com.asm.fastpay.models.HomePageModel;
import com.asm.fastpay.models.HorizontalProductScrollModel;
import com.asm.fastpay.models.MyOrderItemModel;
import com.asm.fastpay.models.SliderModel;
import com.asm.fastpay.users.activities.AddAddressActivity;
import com.asm.fastpay.users.activities.DeliveryActivity;
import com.asm.fastpay.users.activities.ProductDetailsActivity;
import com.asm.fastpay.users.fragments.CartFragment;
import com.asm.fastpay.users.fragments.HomeFragment;
import com.asm.fastpay.users.fragments.MyOrdersFragment;
import com.asm.fastpay.users.fragments.MyWishListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBQueries {


    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static String email, fullName, profile;
    public static Boolean atHome = true;

    public static List<CategoryModel> categoryModelList = new ArrayList<CategoryModel>();

    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoryName = new ArrayList<>();

    public static List<String> wishList = new ArrayList<>();
    public static List<WishListModel> wishListModelList = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static int selctedAddress = -1;
    public static List<AddressesModel> addressesModelList = new ArrayList<>();

    public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();

    //public static List<StraggeredRecyclerModel> straggeredRecyclerModelList = new ArrayList<>();

    public static String newCartList ;
    public static int cartListSize, orderListSize = 0, wishListSize;


    public static void loadCategories(final RecyclerView categoryRecyclerView, final Context context) {
        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            categoryRecyclerView.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadFragmentData(final RecyclerView homePageRecyclerView, final Context context, final int index, String categoryName) {

        String newCategoryName = categoryName.replaceAll(" ", "_");
        String newName = newCategoryName.replaceAll("&", "and");
        firebaseFirestore.collection("CATEGORIES")
                .document(newName.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                if ((long) documentSnapshot.get("view_type") == 0) {
                                    List<SliderModel> sliderModelList = new ArrayList<>();
                                    long no_of_banners = (long) documentSnapshot.get("no_of_banners");
                                    for (long x = 1; x < no_of_banners + 1; x++) {
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString()
                                                , documentSnapshot.get("banner_" + x + "_background").toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(0, sliderModelList));

                                } else if ((long) documentSnapshot.get("view_type") == 1) {

                                    List<WishListModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
                                    long no_of_products = (long) documentSnapshot.get("no_of_products");
                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_subtitle_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()));

                                        viewAllProductList.add(new WishListModel(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_full_title_" + x).toString()
                                                , (long) documentSnapshot.get("free_coupons_" + x)
                                                , documentSnapshot.get("average_rating_" + x).toString()
                                                , (long) documentSnapshot.get("total_ratings_" + x)
                                                , documentSnapshot.get("product_price_" + x).toString()
                                                , documentSnapshot.get("cutted_price_" + x).toString()
                                                , (boolean) documentSnapshot.get("COD_" + x)
                                                , (boolean) documentSnapshot.get("in_stock_" + x)));
                                    }
                                    lists.get(index).add(new HomePageModel(1, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList, viewAllProductList));

                                } else if ((long) documentSnapshot.get("view_type") == 2) {
                                    List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                    long no_of_products = (long) documentSnapshot.get("no_of_products");
                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        gridLayoutModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_subtitle_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString()
                                            , documentSnapshot.get("layout_background").toString(), gridLayoutModelList));
                                }
                            }
                            HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                            homePageRecyclerView.setAdapter(homePageAdapter);
                            homePageAdapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void loadWishList(final Context context, final Dialog dialog, final boolean loadProductData) {
        wishList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    wishListSize = Integer.parseInt(String.valueOf((long) task.getResult().get("list_size")));
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        wishList.add(task.getResult().get("product_ID_" + x).toString());
                        if (DBQueries.wishList.contains(ProductDetailsActivity.productID)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true;
                            if (ProductDetailsActivity.addToWishlistButton != null) {
                                ProductDetailsActivity.addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#C9379D")));
                            }
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                            if (ProductDetailsActivity.addToWishlistButton != null) {
                                ProductDetailsActivity.addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#c0c0c0")));
                            }
                        }
                        if (loadProductData) {
                            wishListModelList.clear();
                            final String productID = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(productID)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot documentSnapshot = task.getResult();
                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                wishListModelList.add(new WishListModel(productID
                                                                        , documentSnapshot.get("product_image_1").toString()
                                                                        , documentSnapshot.get("product_title").toString()
                                                                        , (long) documentSnapshot.get("free_coupons")
                                                                        , documentSnapshot.get("average_rating").toString()
                                                                        , (long) documentSnapshot.get("total_ratings")
                                                                        , documentSnapshot.get("product_price").toString()
                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                        , (boolean) documentSnapshot.get("COD")
                                                                        , true));
                                                            } else {
                                                                wishListModelList.add(new WishListModel(productID
                                                                        , documentSnapshot.get("product_image_1").toString()
                                                                        , documentSnapshot.get("product_title").toString()
                                                                        , (long) documentSnapshot.get("free_coupons")
                                                                        , documentSnapshot.get("average_rating").toString()
                                                                        , (long) documentSnapshot.get("total_ratings")
                                                                        , documentSnapshot.get("product_price").toString()
                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                        , (boolean) documentSnapshot.get("COD")
                                                                        , false));
                                                            }
                                                            if (MyWishListFragment.wishListAdapter != null) {
                                                                MyWishListFragment.wishListAdapter.notifyDataSetChanged();
                                                            }
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        if (wishList.size() == 0) {
                                            wishListModelList.clear();
                                        }
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromWishList(final int index, final Context context) {

        final String removeProductId = wishList.get(index);
        wishList.remove(index);
        Map<String, Object> updateWishList = new HashMap<>();

        for (int x = 0; x < wishList.size(); x++) {
            updateWishList.put("product_ID_" + x, wishList.get(x));
        }
        updateWishList.put("list_size", (long) wishList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .set(updateWishList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (wishListModelList.size() != 0) {
                        wishListModelList.remove(index);
                        MyWishListFragment.wishListAdapter.notifyDataSetChanged();
                    }
                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                    Toast.makeText(context, "Item removed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    if (ProductDetailsActivity.addToWishlistButton != null) {
                        ProductDetailsActivity.addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#C9379D")));
                    }
                    wishList.add(index, removeProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_wishlist_query = false;
            }
        });
    }

    public static void loadRatingList(final Context context) {
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;
            myRatedIds.clear();
            myRating.clear();

            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                    .document("MY_RATINGS")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> orderProductIds = new ArrayList<>();
                        for (int x = 0; x < myOrderItemModelList.size(); x++) {
                            orderProductIds.add(myOrderItemModelList.get(x).getProductID());
                        }
                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                            myRating.add((long) task.getResult().get("rating_" + x));

                            if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productID)) {
                                ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                if (ProductDetailsActivity.rateNowContainer != null) {
                                    ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                                }
                            }
                            if (orderProductIds.contains(task.getResult().get("product_ID_" + x).toString())) {
                                myOrderItemModelList.get(orderProductIds.indexOf(task.getResult().get("product_ID_" + x).toString())).setRating(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1);
                            }
                        }
                        if (MyOrdersFragment.myOrderAdapter != null) {
                            MyOrdersFragment.myOrderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_rating_query = false;
                }
            });
        }
    }

    public static void loadCartList(final Context context, final Dialog dialog, final boolean loadProductData, final TextView badgeCount, final TextView totalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    cartListSize = Integer.parseInt(String.valueOf((long) task.getResult().get("list_size")));
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        newCartList = String.valueOf((long) task.getResult().get("list_size"));
                        cartList.add(task.getResult().get("product_ID_" + x).toString());
                        if (DBQueries.cartList.contains(ProductDetailsActivity.productID)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                        }
                        if (loadProductData) {
                            cartItemModelList.clear();
                            final String productID = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(productID)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot documentSnapshot = task.getResult();
                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            int index = 0;
                                                            if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                cartItemModelList.add(index, new CartItemModel(documentSnapshot.getBoolean("COD")
                                                                        , CartItemModel.CART_ITEM
                                                                        , productID
                                                                        , documentSnapshot.get("product_image_1").toString()
                                                                        , documentSnapshot.get("product_title").toString()
                                                                        , (long) documentSnapshot.get("free_coupons")
                                                                        , documentSnapshot.get("product_price").toString()
                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                        , (long) 1
                                                                        , (long) 0
                                                                        , (long) 0
                                                                        , true
                                                                        , (long) documentSnapshot.get("max_quantity")
                                                                        , (long) documentSnapshot.get("stock_quantity")));
                                                            } else {
                                                                cartItemModelList.add(index, new CartItemModel(documentSnapshot.getBoolean("COD")
                                                                        , CartItemModel.CART_ITEM
                                                                        , productID
                                                                        , documentSnapshot.get("product_image_1").toString()
                                                                        , documentSnapshot.get("product_title").toString()
                                                                        , (long) documentSnapshot.get("free_coupons")
                                                                        , documentSnapshot.get("product_price").toString()
                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                        , (long) 1
                                                                        , (long) 0
                                                                        , (long) 0
                                                                        , false
                                                                        , (long) documentSnapshot.get("max_quantity")
                                                                        , (long) documentSnapshot.get("stock_quantity")));
                                                            }
                                                            if (DBQueries.cartList != null) {
                                                                if (cartList.size() == 1) {
                                                                    cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                                    LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                                                                    parent.setVisibility(View.VISIBLE);
                                                                } else if (DBQueries.cartItemModelList.get(DBQueries.cartItemModelList.size() - 1).getType() != CartItemModel.TOTAL_AMOUNT) {
                                                                    cartItemModelList.add(DBQueries.cartItemModelList.size(), new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                                    LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                                                                    parent.setVisibility(View.VISIBLE);
                                                                }
                                                            }
                                                            if (cartList.size() == 0) {
                                                                cartItemModelList.clear();
                                                            }
                                                            CartFragment.cartAdapter.notifyDataSetChanged();
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }


                    if (cartList.size() != 0) {
                        badgeCount.setVisibility(View.VISIBLE);
                    } else {
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if (DBQueries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DBQueries.cartList.size() - 1));
                    } else {
                        badgeCount.setText("99");
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromCart(final int index, final Context context, final TextView cartTotalAmount) {
        final String removeProductId = cartList.get(index);
        cartList.remove(index);
        Map<String, Object> updateCartList = new HashMap<>();
        for (int x = 0; x < cartList.size(); x++) {
            updateCartList.put("product_ID_" + x, cartList.get(x));
        }
        updateCartList.put("list_size", (long) cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (cartItemModelList.size() != 0) {
                        cartItemModelList.remove(index);
                        CartFragment.cartAdapter.notifyDataSetChanged();
                    }
                    if (cartList.size() == 0) {
                        LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }
                    Toast.makeText(context, "Item removed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    cartList.add(index, removeProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_cart_query = false;
            }
        });
    }

    public static void loadAddresses(final Context context, final Dialog loadingDialog, final boolean gotoDeliveryActivity) {
        addressesModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Intent deliveryIntent = null;
                    if ((long) task.getResult().get("list_size") == 0) {
                        deliveryIntent = new Intent(context, AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT", "deliveryIntent");
                    } else {
                        for (long x = 1; x < (long) task.getResult().get("list_size") + 1; x++) {
                            addressesModelList.add(new AddressesModel(task.getResult().getBoolean("selected_" + x)
                                    , task.getResult().getString("city_" + x)
                                    , task.getResult().getString("locality_" + x)
                                    , task.getResult().getString("flat_no_" + x)
                                    , task.getResult().getString("pincode_" + x)
                                    , task.getResult().getString("landmark_" + x)
                                    , task.getResult().getString("name_" + x)
                                    , task.getResult().getString("mobile_no_" + x)
                                    , task.getResult().getString("alternate_mobile_no_" + x)
                                    , task.getResult().getString("state_" + x)));
                            if ((boolean) task.getResult().get("selected_" + x) == true) {
                                selctedAddress = Integer.parseInt(String.valueOf(x - 1));
                            }
                        }
                        if (gotoDeliveryActivity) {
                            deliveryIntent = new Intent(context, DeliveryActivity.class);
                        }
                    }
                    if (gotoDeliveryActivity) {
                        context.startActivity(deliveryIntent);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }

    public static void loadOrders(final Context context, @Nullable final MyOrderAdapter myOrderAdapter, final Dialog dialog) {
        myOrderItemModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").orderBy("time", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                orderListSize = 1;
                                firebaseFirestore.collection("ORDERS").document(documentSnapshot.getString("order_id")).collection("OrderItems").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
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
                                                    loadRatingList(context);
                                                    if (myOrderAdapter != null) {
                                                        myOrderAdapter.notifyDataSetChanged();
                                                    }
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                            }
                        } else {
                            dialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    public static void loadTabProducts(final Context context, final String category, final StaggeredRecyclerAdapter adapter, final Dialog dialog, final List<StraggeredRecyclerModel> straggeredRecyclerModelList) {
        straggeredRecyclerModelList.clear();
        final Source source = Source.SERVER;
        final ArrayList<String> lst = new ArrayList<>();
        lst.clear();
        dialog.show();
        firebaseFirestore.collection("PRODUCTS").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final DocumentSnapshot productIDs : task.getResult().getDocuments()) {
                                final String IDS = productIDs.getId();
                                lst.add(IDS);
                                Log.e("IDS", "" + productIDs.getId());
                                Log.e("List Item: ", " " + lst.size());
                            }
                                for (int z = 0; z < lst.size(); z++) {
                                    final String productIDD = lst.get(z);
                                    Log.e("productIDD", "" + lst.get(z));
                                    firebaseFirestore.collection("PRODUCTS").document(productIDD)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                final DocumentSnapshot documentSnapshot = task.getResult();
                                                //if (documentSnapshot.getString("type").equals(category)) {
                                                straggeredRecyclerModelList.add(new StraggeredRecyclerModel(productIDD
                                                        , documentSnapshot.get("product_image_1").toString()
                                                        , documentSnapshot.get("product_sub_title").toString()
                                                        , documentSnapshot.get("product_sub_details").toString()
                                                        , documentSnapshot.get("product_price").toString()));
                                                Log.e("Product_Image:", documentSnapshot.get("product_image_1").toString());

                                                adapter.notifyDataSetChanged();
                                                // }
                                                if (adapter != null) {
                                                    adapter.notifyDataSetChanged();
                                                }
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            dialog.dismiss();

                        }
                        else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }


    public static void clearData() {
        categoryModelList.clear();
        lists.clear();
        loadedCategoryName.clear();
        wishList.clear();
        wishListModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedIds.clear();
        myRating.clear();
        addressesModelList.clear();
        myOrderItemModelList.clear();

    }
}
