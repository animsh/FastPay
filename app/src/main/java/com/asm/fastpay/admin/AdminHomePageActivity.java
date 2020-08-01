package com.asm.fastpay.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.asm.fastpay.R;
import com.asm.fastpay.users.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class AdminHomePageActivity extends AppCompatActivity {

    private ChipNavigationBar adminNavigationBar;
    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);

        getSupportActionBar().setTitle("FastPay - Admin");

        adminNavigationBar = findViewById(R.id.bottom_menu);
        adminNavigationBar.setItemSelected(R.id.online_orders, true);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new OnlineOrdersFragment()).commit();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);


        adminNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.online_orders:
                        selectedFragment = new OnlineOrdersFragment();
                        break;

                    case  R.id.offline_orders:
                        selectedFragment = new OfflineOrdersFragment();
                        break;

                    case R.id.products:
                        selectedFragment = new ProductsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.btn_logout) {
            FirebaseAuth.getInstance().signOut();
            deleteSavedData();
            AdminDBQueries.clearAdminData();
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteSavedData() {
        SharedPreferences pref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isAdminLogin", false);
        editor.commit();
    }
}
