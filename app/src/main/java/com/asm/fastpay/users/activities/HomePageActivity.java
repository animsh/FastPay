package com.asm.fastpay.users.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.R;
import com.asm.fastpay.fragments.NewHomeFragment;
import com.asm.fastpay.users.fragments.CartFragment;
import com.asm.fastpay.users.fragments.MyOrdersFragment;
import com.asm.fastpay.users.fragments.MyWishListFragment;
import com.asm.fastpay.users.fragments.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomePageActivity extends AppCompatActivity {

    public static ChipNavigationBar chipNavigationBar;
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseUser currentUser;
    public static Fragment selectedFragment = new NewHomeFragment();
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    public static Activity homeActivity;
    public static boolean resetHomePage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // make activity full screen

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("FastPay");
        setContentView(R.layout.activity_home_page);
        //getSupportActionBar().hide();

        // ini view


        chipNavigationBar = findViewById(R.id.bottom_navigation_bar);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (ProductDetailsActivity.OPRNING_CART_FROM_PRODUCT_DETAILS) {
            ProductDetailsActivity.OPRNING_CART_FROM_PRODUCT_DETAILS = false;
            chipNavigationBar.setItemSelected(R.id.btn_cart, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CartFragment()).commit();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        } else {
            chipNavigationBar.setItemSelected(R.id.btn_home, true);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new NewHomeFragment()).commit();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

        }

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                switch (id) {
                    case R.id.btn_home:
                        selectedFragment = new NewHomeFragment();
                        break;
                    case R.id.btn_cart:
                        selectedFragment = new CartFragment();
                        break;
                    case R.id.btn_my_order:
                        selectedFragment = new MyOrdersFragment();
                        break;
                    case R.id.btn_wish_list:
                        selectedFragment = new MyWishListFragment();
                        break;
                    case R.id.btn_profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.btn_scanner) {
            Intent scannerIntent = new Intent(HomePageActivity.this, ScannerActivity.class);
            startActivity(scannerIntent);
            return true;
        }

        if (id == R.id.btn_search) {
            Intent searchIntent = new Intent(HomePageActivity.this, SearchActivity.class);
            startActivity(searchIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        homeActivity = HomePageActivity.this;
        if (DBQueries.wishList.size() >= 1) {
            if(MyWishListFragment.wishListAdapter != null) {
                MyWishListFragment.wishListAdapter.notifyDataSetChanged();
            }
        }
        DBQueries.loadWishList(HomePageActivity.this, new Dialog(HomePageActivity.this), false);
        if(DBQueries.cartList.size() >=1){
            if(CartFragment.cartAdapter != null) {
                CartFragment.cartAdapter.notifyDataSetChanged();
            }
        }
        DBQueries.loadCartList(HomePageActivity.this, new Dialog(HomePageActivity.this), false, new TextView(HomePageActivity.this),new TextView(HomePageActivity.this));
        if(resetHomePage){
            resetHomePage = false;
        }

        FirebaseFirestore.getInstance().collection("USERS").document(firebaseAuth.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DBQueries.fullName = task.getResult().getString("fullName");
                    DBQueries.email = task.getResult().getString("email");
                    DBQueries.profile = task.getResult().getString("profile");
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(HomePageActivity.this,"Error: "+ error,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
