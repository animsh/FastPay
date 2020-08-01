package com.asm.fastpay.users.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.asm.fastpay.adapters.AddressesAdapter;

import java.util.HashMap;
import java.util.Map;

import static com.asm.fastpay.users.activities.DeliveryActivity.SELECT_ADDRESS;

public class MyAddressesActivity extends AppCompatActivity {

    private int previousAddress;
    private LinearLayout addNewAddressBtn;
    private static AddressesAdapter addressesAdapter;
    private RecyclerView myAddressesRecyclerView;
    private Button deliverHereBtn;
    private TextView addressSaved;
    private Dialog loadingDialog;
    private int MODE;

    public static void refreshItem(int deselect, int select) {
        addressesAdapter.notifyItemChanged(deselect);
        addressesAdapter.notifyItemChanged(select);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Addresses");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                addressSaved.setText(String.valueOf(DBQueries.addressesModelList.size()) + " addresses saved");

            }
        });

        myAddressesRecyclerView = findViewById(R.id.addresses_recyclerview);
        deliverHereBtn = findViewById(R.id.deliver_here_btn);
        addNewAddressBtn = findViewById(R.id.add_new_address_btn);
        addressSaved = findViewById(R.id.address_saved);
        previousAddress = DBQueries.selctedAddress;


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        myAddressesRecyclerView.setLayoutManager(layoutManager);


        MODE = getIntent().getIntExtra("MODE", -1);
        if (MODE == SELECT_ADDRESS) {
            deliverHereBtn.setVisibility(View.VISIBLE);
        } else {
            deliverHereBtn.setVisibility(View.GONE);
        }
        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DBQueries.selctedAddress != previousAddress) {
                    final int previousAddressIndex = previousAddress;
                    loadingDialog.show();
                    Map<String, Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_" + String.valueOf(previousAddress + 1), false);
                    updateSelection.put("selected_" + String.valueOf(DBQueries.selctedAddress + 1), true);

                    previousAddress = DBQueries.selctedAddress;
                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            } else {
                                previousAddress = previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(MyAddressesActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                } else {
                    finish();
                }
            }
        });
        addressesAdapter = new AddressesAdapter(DBQueries.addressesModelList, MODE, loadingDialog);
        myAddressesRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator) myAddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        addressesAdapter.notifyDataSetChanged();

        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressIntent = new Intent(MyAddressesActivity.this, AddAddressActivity.class);
                if (MODE != SELECT_ADDRESS) {
                    addAddressIntent.putExtra("INTENT", "manage");
                } else {
                    addAddressIntent.putExtra("INTENT", "null");
                }
                startActivity(addAddressIntent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        addressSaved.setText(String.valueOf(DBQueries.addressesModelList.size()) + " addresses saved");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (MODE == SELECT_ADDRESS) {
                if (DBQueries.selctedAddress != previousAddress) {
                    DBQueries.addressesModelList.get(DBQueries.selctedAddress).setSelected(false);
                    DBQueries.addressesModelList.get(previousAddress).setSelected(true);
                    DBQueries.selctedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (MODE == SELECT_ADDRESS) {
            if (DBQueries.selctedAddress != previousAddress) {
                DBQueries.addressesModelList.get(DBQueries.selctedAddress).setSelected(false);
                DBQueries.addressesModelList.get(previousAddress).setSelected(true);
                DBQueries.selctedAddress = previousAddress;
            }
        }
        super.onBackPressed();

    }
}
