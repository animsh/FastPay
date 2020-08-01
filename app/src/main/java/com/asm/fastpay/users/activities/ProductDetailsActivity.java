package com.asm.fastpay.users.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.R;
import com.asm.fastpay.models.CartItemModel;
import com.asm.fastpay.models.ProductSpecificationModel;
import com.asm.fastpay.models.RewardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.asm.fastpay.adapters.MyRewardsAdapter;
import com.asm.fastpay.adapters.ProductDetailsAdapter;
import com.asm.fastpay.adapters.ProductImagesAdapter;
import com.asm.fastpay.models.WishListModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.asm.fastpay.users.activities.ScannerActivity.scannedFromScanner;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_rating_query = false;
    public static boolean running_wishlist_query = false;
    public static boolean running_cart_query = false;
    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;
    public static boolean OPRNING_CART_FROM_PRODUCT_DETAILS = false;
    private boolean inStock;

    public static boolean fromSearch = false;
    public static Activity productDetailsActivity;
    public static TextView couponTitle;
    public static TextView couponBody;
    public static TextView couponExpiryDate;
    public static FloatingActionButton addToWishlistButton;
    public static String productID;
    private static RecyclerView couponsRecyclerView;
    private static LinearLayout selectedCoupon;
    List<String> productImages = new ArrayList<>();
    private FirebaseUser currentUser;
    private ViewPager productImagesViewPager;
    private TabLayout viewPagerIndicator;
    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView totalRatingMiniView;
    private TextView productPrice;
    // Description
    private TextView cuttedPrice;
    private ImageView codIndicator;
    private TextView tvCodIndicator;
    private TextView rewardTitle;
    private TextView rewardBody;
    private Button couponRedeemButton;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;
    // Description
    private ConstraintLayout productDetailsOnlyContainer;
    // rating layout
    private ConstraintLayout productDetailsTabsCOntainer;
    private TextView productOnlyDescriptionBody;
    private String productDescription;
    private String productOtherDetails;
    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    // rating layout
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsNoContainer;
    //Coupon Dialog
    private LinearLayout couponRedemptionLayout;
    private TextView totalRatingsFigure;
    private LinearLayout ratingsProgressBarContainer;
    private TextView averageRating, productSubDetails;
    private Button buyNoewButton;
    private LinearLayout addToCartBtn;
    private FirebaseFirestore firebaseFirestore;
    private Dialog loadingDialog, locationDialog;

    private DocumentSnapshot documentSnapshot;
    public static MenuItem cartItem;
    public static MenuItem searchItem;

    private TextView badgeCount;
    private String size;

    private RadioGroup locations;
    private RadioButton home, shop;
    private Button cancelBtn, dialogContinueBtn;


    public static void showDialogRecyclerView() {
        if (couponsRecyclerView.getVisibility() == View.GONE) {
            couponsRecyclerView.setVisibility(View.VISIBLE);
            selectedCoupon.setVisibility(View.GONE);
        } else {
            couponsRecyclerView.setVisibility(View.GONE);
            selectedCoupon.setVisibility(View.VISIBLE);
        }
    }

    //Coupon Dialog
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImagesViewPager = findViewById(R.id.product_images_view_pager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        addToWishlistButton = findViewById(R.id.add_to_wishlist_btn);
        productDetailsViewPager = findViewById(R.id.product_details_view_pager);
        productDetailsTabLayout = findViewById(R.id.product_details_tab_layout);
        buyNoewButton = findViewById(R.id.buy_now_btn);
        couponRedeemButton = findViewById(R.id.coupen_redemption_btn);
        productTitle = findViewById(R.id.product_title);
        averageRatingMiniView = findViewById(R.id.tv_product_rating_miniview);
        totalRatingMiniView = findViewById(R.id.total_ratings_miniview);
        productPrice = findViewById(R.id.cuttes_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        tvCodIndicator = findViewById(R.id.tv_cod_indicator);
        codIndicator = findViewById(R.id.cod_indicator_image);
        rewardTitle = findViewById(R.id.rewards_title);
        rewardBody = findViewById(R.id.rewards_body);
        productDetailsOnlyContainer = findViewById(R.id.product_details_container);
        productDetailsTabsCOntainer = findViewById(R.id.product_details_tabs_container);
        productOnlyDescriptionBody = findViewById(R.id.product_detail_body);
        totalRatings = findViewById(R.id.total_ratings);
        ratingsNoContainer = findViewById(R.id.ratings_number_container);
        totalRatingsFigure = findViewById(R.id.total_ratings_figure);
        ratingsProgressBarContainer = findViewById(R.id.rating_progressbar_container);
        averageRating = findViewById(R.id.average_rating);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        couponRedemptionLayout = findViewById(R.id.coupon_redemption_layout);
        productSubDetails = findViewById(R.id.product_sub_details);
        initialRating = -1;

        //loading dialog

        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        locationDialog = new Dialog(ProductDetailsActivity.this);
        locationDialog.setContentView(R.layout.location_dialog);
        locationDialog.setCancelable(false);
        locationDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        locationDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        locations = locationDialog.findViewById(R.id.locations);
        home = locationDialog.findViewById(R.id.home);
        shop = locationDialog.findViewById(R.id.shop);
        cancelBtn = locationDialog.findViewById(R.id.cancel_dialog);
        dialogContinueBtn = locationDialog.findViewById(R.id.continue_dialog);

        firebaseFirestore = FirebaseFirestore.getInstance();
        if (scannedFromScanner) {
            scannedFromScanner = false;
            productID = getIntent().getStringExtra("PRODUCT_ID_FROM_SCANNER");
            firebaseFirestore.collection("PRODUCTS").document(productID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        documentSnapshot = task.getResult();
                        setProductData(documentSnapshot,loadingDialog);
                    } else {
                        loadingDialog.dismiss();
                        String error = task.getException().getMessage();
                        Toast.makeText(ProductDetailsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            productID = getIntent().getStringExtra("PRODUCT_ID");
            firebaseFirestore.collection("PRODUCTS").document(productID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        documentSnapshot = task.getResult();
                        setProductData(documentSnapshot,loadingDialog);
                    } else {
                        loadingDialog.dismiss();
                        String error = task.getException().getMessage();
                        Toast.makeText(ProductDetailsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        viewPagerIndicator.setupWithViewPager(productImagesViewPager, true);

        addToWishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running_wishlist_query) {
                    running_wishlist_query = true;
                    if (ALREADY_ADDED_TO_WISHLIST) {
                        int index = DBQueries.wishList.indexOf(productID);
                        DBQueries.removeFromWishList(index, ProductDetailsActivity.this);
                        addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#c0c0c0")));
                    } else {

                        Map<String, Object> addProduct = new HashMap<>();
                        addProduct.put("product_ID_" + DBQueries.wishList.size(), productID);
                        addProduct.put("list_size", (long) (DBQueries.wishList.size() + 1));
                        firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                .document("MY_WISHLIST")
                                .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (DBQueries.wishList.size() != 0) {
                                        DBQueries.wishListModelList.add(new WishListModel(productID
                                                , documentSnapshot.get("product_image_1").toString()
                                                , documentSnapshot.get("product_title").toString()
                                                , (long) documentSnapshot.get("free_coupons")
                                                , documentSnapshot.get("average_rating").toString()
                                                , (long) documentSnapshot.get("total_ratings")
                                                , documentSnapshot.get("product_price").toString()
                                                , documentSnapshot.get("cutted_price").toString()
                                                , (boolean) documentSnapshot.get("COD")
                                                , inStock));


                                    }
                                    ALREADY_ADDED_TO_WISHLIST = true;
                                    addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#C9379D")));
                                    DBQueries.wishList.add(productID);
                                    Toast.makeText(ProductDetailsActivity.this, "Added to wishlist", Toast.LENGTH_SHORT).show();
                                } else {
                                    addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#c0c0c0")));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(ProductDetailsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                }
                                running_wishlist_query = false;
                            }
                        });

                    }
                }
            }
        });


        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));

        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //rating layout

        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (starPosition != initialRating) {
                        if (!running_rating_query) {
                            running_rating_query = true;
                            Map<String, Object> updateRating = new HashMap<>();
                            setRating(starPosition);
                            if (DBQueries.myRatedIds.contains(productID)) {
                                TextView oldRating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
                                TextView finalRating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);

                                updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                updateRating.put(starPosition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                updateRating.put("average_rating", calculateAverageRating((long) starPosition - initialRating, true));
                            } else {
                                updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                updateRating.put("average_rating", calculateAverageRating((long) starPosition + 1, false));
                                updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                            }
                            firebaseFirestore.collection("PRODUCTS").document(productID)
                                    .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Map<String, Object> myRating = new HashMap<>();
                                        if (DBQueries.myRatedIds.contains(productID)) {
                                            myRating.put("rating_" + DBQueries.myRatedIds.indexOf(productID), starPosition + 1);

                                        } else {
                                            myRating.put("list_size", (long) DBQueries.myRatedIds.size() + 1);
                                            myRating.put("product_ID_" + DBQueries.myRatedIds.size(), productID);
                                            myRating.put("rating_" + DBQueries.myRating.size(), (long) starPosition + 1);
                                        }

                                        firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    if (DBQueries.myRatedIds.contains(productID)) {
                                                        DBQueries.myRating.set(DBQueries.myRatedIds.indexOf(productID), (long) starPosition + 1);

                                                        TextView oldRating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
                                                        TextView finalRating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
                                                        oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                        finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));

                                                    } else {
                                                        DBQueries.myRatedIds.add(productID);
                                                        DBQueries.myRating.add((long) starPosition + 1);

                                                        TextView rating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
                                                        rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                        totalRatingMiniView.setText(" (" + ((long) documentSnapshot.get("total_ratings") + 1) + " ratings) ");
                                                        totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                        totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));

                                                        Toast.makeText(ProductDetailsActivity.this, "Thank you for rating!", Toast.LENGTH_SHORT).show();
                                                    }

                                                    for (int x = 0; x < 5; x++) {
                                                        TextView ratingFigures = (TextView) ratingsNoContainer.getChildAt(x);
                                                        ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                        progressBar.setMax(Integer.parseInt(totalRatingsFigure.getText().toString()));
                                                        progressBar.setProgress(Integer.parseInt(ratingFigures.getText().toString()));
                                                    }
                                                    initialRating = starPosition;
                                                    averageRating.setText(calculateAverageRating(0, true));
                                                    averageRatingMiniView.setText(calculateAverageRating(0, true));
                                                    if (DBQueries.wishList.contains(productID) && DBQueries.wishListModelList.size() != 0) {
                                                        int index = DBQueries.wishList.indexOf(productID);
                                                        DBQueries.wishListModelList.get(index).setRating(averageRating.getText().toString());
                                                        DBQueries.wishListModelList.get(index).setTotalRatings(Long.parseLong(totalRatingsFigure.getText().toString()));
                                                    }
                                                } else {
                                                    setRating(initialRating);
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(ProductDetailsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                                }
                                                running_rating_query = false;
                                            }
                                        });
                                    } else {
                                        running_rating_query = false;
                                        setRating(initialRating);
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        //rating layout

        buyNoewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDialog.show();
                dialogContinueBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (home.isChecked()) {
                            DBQueries.atHome = true;
                        } else if (shop.isChecked()) {
                            DBQueries.atHome = false;
                        }
                        if (!home.isChecked() && !shop.isChecked()) {
                            Toast.makeText(ProductDetailsActivity.this, "Please select your location!!", Toast.LENGTH_SHORT).show();
                        } else {
                            DeliveryActivity.fromCart = false;
                            productDetailsActivity = ProductDetailsActivity.this;
                            loadingDialog.show();
                            //DeliveryActivity.cartItemModelList.clear();
                            DeliveryActivity.cartItemModelList = new ArrayList<>();
                            DeliveryActivity.cartItemModelList.add(new CartItemModel(documentSnapshot.getBoolean("COD")
                                    ,CartItemModel.CART_ITEM
                                    , productID
                                    , documentSnapshot.get("product_image_1").toString()
                                    , documentSnapshot.get("product_title").toString()
                                    , (long) documentSnapshot.get("free_coupons")
                                    , documentSnapshot.get("product_price").toString()
                                    , documentSnapshot.get("cutted_price").toString()
                                    , (long) 1
                                    , (long) 0
                                    , (long) 0
                                    , inStock
                                    , (long) documentSnapshot.get("max_quantity")
                                    , (long) documentSnapshot.get("stock_quantity")));
                            DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                            if (DBQueries.addressesModelList.size() == 0) {
                                DBQueries.loadAddresses(ProductDetailsActivity.this, loadingDialog,true);
                            } else {
                                loadingDialog.dismiss();
                                Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                                startActivity(deliveryIntent);
                            }
                        }
                        locationDialog.dismiss();
                    }

                });

            }

        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDialog.dismiss();
            }
        });


        ///  Coupon Dialog

        final Dialog checkCouponPriceDialog = new Dialog(ProductDetailsActivity.this);
        checkCouponPriceDialog.setContentView(R.layout.coupon_reddem_dialog);
        checkCouponPriceDialog.setCancelable(true);
        checkCouponPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView toggleRecyclerView = checkCouponPriceDialog.findViewById(R.id.toggle_recyclerview);
        couponsRecyclerView = checkCouponPriceDialog.findViewById(R.id.coupons_recyclerview);
        selectedCoupon = checkCouponPriceDialog.findViewById(R.id.selected_coupon);
        couponTitle = checkCouponPriceDialog.findViewById(R.id.coupon_title);
        couponExpiryDate = checkCouponPriceDialog.findViewById(R.id.coupon_validity);
        couponBody = checkCouponPriceDialog.findViewById(R.id.coupon_body);

        TextView originalPrice = checkCouponPriceDialog.findViewById(R.id.original_price);
        TextView couponPrice = checkCouponPriceDialog.findViewById(R.id.discounted_price);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        couponsRecyclerView.setLayoutManager(linearLayoutManager);

        List<RewardModel> rewardModelList = new ArrayList<>();
        rewardModelList.add(new RewardModel("Cash Back", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Discount", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Buy 1 get 1 free", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Cash Back", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Discount", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Buy 1 get 1 free", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Cash Back", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Discount", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Buy 1 get 1 free", "till 2nd Jun 2016", "Get 20% cash back on any product above Rs. 200/- and below Rs. 3000/-"));

        MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(rewardModelList, true);
        couponsRecyclerView.setAdapter(myRewardsAdapter);
        myRewardsAdapter.notifyDataSetChanged();

        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRecyclerView();
            }
        });

        //Coupon Dialog
        couponRedeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCouponPriceDialog.show();
            }
        });

        //
    }

    public void setProductData(final DocumentSnapshot documentSnapshot, final Dialog dialog) {
        dialog.show();
        firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                productImages.add(documentSnapshot.get("product_image_" + x).toString());
                            }
                            ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                            productImagesViewPager.setAdapter(productImagesAdapter);

                            productTitle.setText(documentSnapshot.get("product_title").toString());
                            averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                            totalRatingMiniView.setText(" (" + (long) documentSnapshot.get("total_ratings") + " ratings) ");
                            productPrice.setText(" Rs. " + documentSnapshot.get("product_price").toString() + " /- ");
                            cuttedPrice.setText(" Rs. " + documentSnapshot.get("cutted_price").toString() + " /- ");
                            productSubDetails.setText(documentSnapshot.get("product_sub_details").toString());

                            if ((boolean) documentSnapshot.get("COD")) {
                                codIndicator.setVisibility(View.VISIBLE);
                                tvCodIndicator.setVisibility(View.VISIBLE);
                            } else {
                                codIndicator.setVisibility(View.INVISIBLE);
                                tvCodIndicator.setVisibility(View.INVISIBLE);
                            }
                            rewardTitle.setText((long) documentSnapshot.get("free_coupons") + documentSnapshot.get("free_coupon_title").toString());
                            rewardBody.setText(documentSnapshot.get("free_coupon_body").toString());

                            if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                productDetailsTabsCOntainer.setVisibility(View.VISIBLE);
                                productDetailsOnlyContainer.setVisibility(View.GONE);
                                productDescription = documentSnapshot.get("product_description").toString();

                                productOtherDetails = documentSnapshot.get("product_other_details").toString();

                                for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {
                                    productSpecificationModelList.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));

                                    for (long y = 1; y < (long) documentSnapshot.get("spec_title_" + x + "_total_fields") + 1; y++) {
                                        productSpecificationModelList.add(new ProductSpecificationModel(1, documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString(), documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()));
                                    }
                                }

                            } else {

                                productDetailsTabsCOntainer.setVisibility(View.GONE);
                                productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                            }

                            totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");

                            for (int x = 0; x < 5; x++) {
                                TextView rating = (TextView) ratingsNoContainer.getChildAt(x);
                                rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star")));

                                ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                progressBar.setMax(Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings"))));
                                progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                            }
                            totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                            averageRating.setText(documentSnapshot.get("average_rating").toString());
                            productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList));

                            if (DBQueries.myRating.size() == 0) {
                                DBQueries.loadRatingList(ProductDetailsActivity.this);
                            }
                            if (DBQueries.myRatedIds.contains(productID)) {
                                int index = DBQueries.myRatedIds.indexOf(productID);
                                initialRating = Integer.parseInt(String.valueOf(DBQueries.myRating.get(index))) - 1;
                                setRating(initialRating);
                            }
                            if (DBQueries.cartList.size() == 0) {
                                DBQueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
                            }
                            if (DBQueries.wishList.size() == 0) {
                                DBQueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                            } else {
                                loadingDialog.dismiss();
                            }
                            if (DBQueries.cartList.contains(productID)) {
                                ALREADY_ADDED_TO_CART = true;
                            } else {
                                ALREADY_ADDED_TO_CART = false;
                            }
                            if (DBQueries.wishList.contains(productID)) {
                                ALREADY_ADDED_TO_WISHLIST = true;
                                addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#C9379D")));
                            } else {
                                ALREADY_ADDED_TO_WISHLIST = false;
                                addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#c0c0c0")));
                            }


                            if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                inStock = true;
                                addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!running_cart_query) {
                                            running_cart_query = true;
                                            if (ALREADY_ADDED_TO_CART) {
                                                running_cart_query = false;
                                                Toast.makeText(ProductDetailsActivity.this, "Already added to cart!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Map<String, Object> addProduct = new HashMap<>();
                                                addProduct.put("product_ID_" + DBQueries.cartList.size(), productID);
                                                addProduct.put("list_size", (long) (DBQueries.cartList.size() + 1));
                                                firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                                        .document("MY_CART")
                                                        .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            if (DBQueries.cartItemModelList.size() != 0) {
                                                                DBQueries.cartItemModelList.add(0, new CartItemModel(documentSnapshot.getBoolean("COD")
                                                                        ,CartItemModel.CART_ITEM
                                                                        , productID
                                                                        , documentSnapshot.get("product_image_1").toString()
                                                                        , documentSnapshot.get("product_title").toString()
                                                                        , (long) documentSnapshot.get("free_coupons")
                                                                        , documentSnapshot.get("product_price").toString()
                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                        , (long) 1
                                                                        , (long) 0
                                                                        , (long) 0
                                                                        , inStock
                                                                        , (long) documentSnapshot.get("max_quantity")
                                                                        , (long) documentSnapshot.get("stock_quantity")));

                                                            }
                                                            ALREADY_ADDED_TO_CART = true;
                                                            DBQueries.cartList.add(productID);
                                                            Toast.makeText(ProductDetailsActivity.this, "Added to cart successfully!", Toast.LENGTH_SHORT).show();
                                                            invalidateOptionsMenu();
                                                            running_cart_query = false;
                                                            dialog.dismiss();
                                                        } else {
                                                            dialog.dismiss();
                                                            running_cart_query = false;
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(ProductDetailsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                                            invalidateOptionsMenu();
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    }
                                });
                            } else {
                                inStock = false;
                                buyNoewButton.setVisibility(View.GONE);
                                TextView outOfStock = (TextView) addToCartBtn.getChildAt(0);
                                outOfStock.setText("Out of Stock");
                                outOfStock.setTextColor(getResources().getColor(R.color.flipkart));
                                outOfStock.setCompoundDrawables(null, null, null, null);
                                dialog.dismiss();
                            }

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductDetailsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    public static void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#656565")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#21bf73")));
            }
        }
    }

    private String calculateAverageRating(long currentUserRating, boolean update) {
        double totalStars = 0;
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingsNoContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString()) * x);
        }
        totalStars = totalStars + currentUserRating;
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(totalRatingsFigure.getText().toString())).substring(0, 3);

        } else {
            return String.valueOf(totalStars / (Long.parseLong(totalRatingsFigure.getText().toString()) + 1)).substring(0, 3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);
        cartItem = menu.findItem(R.id.btn_cart);
        searchItem = menu.findItem(R.id.btn_search);
        cartItem.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

        if (DBQueries.cartList.size() == 0) {
            DBQueries.loadCartList(this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
        } else {
            badgeCount.setVisibility(View.VISIBLE);
            if (DBQueries.cartList.size() < 99) {
                badgeCount.setText(Integer.toString(DBQueries.cartList.size()));
                Toast.makeText(ProductDetailsActivity.this, "This " + DBQueries.cartList.size(), Toast.LENGTH_LONG).show();
            } else {
                badgeCount.setText("99");
            }
        }
        cartItem.getActionView().

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OPRNING_CART_FROM_PRODUCT_DETAILS = true;
                        Intent cartIntent = new Intent(ProductDetailsActivity.this, HomePageActivity.class);
                        startActivity(cartIntent);
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.btn_search) {
            if(fromSearch){
                finish();
            }else {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
            }
            return true;
        } else if (id == R.id.btn_cart) {
            OPRNING_CART_FROM_PRODUCT_DETAILS = true;
            Intent cartIntent = new Intent(ProductDetailsActivity.this, HomePageActivity.class);
            startActivity(cartIntent);
            return true;
        } else if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (DBQueries.myRating.size() == 0) {
            DBQueries.loadRatingList(ProductDetailsActivity.this);
        }
        if (DBQueries.wishList.size() == 0) {
            DBQueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
        }

        if (DBQueries.myRatedIds.contains(productID)) {
            int index = DBQueries.myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DBQueries.myRating.get(index))) - 1;
            setRating(initialRating);
        }
        if (DBQueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }
        if (DBQueries.wishList.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#C9379D")));
        } else {
            ALREADY_ADDED_TO_WISHLIST = false;
            addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#c0c0c0")));
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch = false;
    }
}
