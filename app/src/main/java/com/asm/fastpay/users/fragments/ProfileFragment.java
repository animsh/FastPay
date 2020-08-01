package com.asm.fastpay.users.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.users.activities.MainActivity;
import com.asm.fastpay.users.activities.MyAddressesActivity;
import com.asm.fastpay.R;
import com.asm.fastpay.users.activities.UpdateUserInfoActivity;
import com.asm.fastpay.models.MyOrderItemModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    public static final int MANAGE_ADDRESS = 1;
    private FloatingActionButton settingsButton;
    private Button signOutBtn;
    private Button viewAllAddressBtn;
    private CircleImageView profileView, currentOrderImage;
    private TextView name, email, tvCurrentOrderStatus;
    private LinearLayout layoutContainer, recentOrdersContainer;
    private Dialog loadingDialog;
    private ImageView orderIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar O_P_progress, P_S_progress, S_D_progress;
    private TextView yourRecentOrdersTitle;
    private TextView addressName, address, pincode;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        layoutContainer = view.findViewById(R.id.layout_container);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        profileView = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        currentOrderImage = view.findViewById(R.id.current_order_image);
        tvCurrentOrderStatus = view.findViewById(R.id.tv_current_order_status);

        orderIndicator = view.findViewById(R.id.ordered_indicator);
        packedIndicator = view.findViewById(R.id.packed_indicator);
        shippedIndicator = view.findViewById(R.id.shipped_indicator);
        deliveredIndicator = view.findViewById(R.id.delivered_indicator);

        O_P_progress = view.findViewById(R.id.order_packed_progress);
        P_S_progress = view.findViewById(R.id.packed_shipped_progress);
        S_D_progress = view.findViewById(R.id.shipped_delivered_progress);

        yourRecentOrdersTitle = view.findViewById(R.id.your_recent_orders_title);
        recentOrdersContainer = view.findViewById(R.id.recent_orders_container);

        addressName = view.findViewById(R.id.address_fullname);
        address = view.findViewById(R.id.address);
        pincode = view.findViewById(R.id.address_pincode);

        settingsButton = view.findViewById(R.id.settings_btn);

        layoutContainer.getChildAt(1).setVisibility(View.GONE);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadingDialog.setOnDismissListener(null);
            }
        });

        if(layoutContainer.getChildAt(1).getVisibility() == View.GONE){
            loadCurrentOrder();
        }
        DBQueries.loadOrders(getContext(), null, loadingDialog);


        signOutBtn = view.findViewById(R.id.sign_out_btn);
        viewAllAddressBtn = view.findViewById(R.id.view_all_addresses_btn);

        viewAllAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressesIntent = new Intent(getContext(), MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", MANAGE_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                deleteSavedData();
                DBQueries.clearData();
                Intent mainActivity = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(mainActivity);
                getActivity().finish();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUserInfo = new Intent(getContext(), UpdateUserInfoActivity.class);
                updateUserInfo.putExtra("Name",DBQueries.fullName);
                updateUserInfo.putExtra("Email",DBQueries.email);
                updateUserInfo.putExtra("Photo",DBQueries.profile);
                startActivity(updateUserInfo);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        name.setText(DBQueries.fullName);
        email.setText(DBQueries.email);
        if(DBQueries.profile != null) {
            if (!DBQueries.profile.equals("")) {
                Glide.with(getContext()).load(DBQueries.profile).apply(new RequestOptions().placeholder(R.drawable.profile_simple)).into(profileView);
            }else {
                profileView.setImageResource(R.drawable.profile_male);
            }
        }
        if(!loadingDialog.isShowing()){
            if (DBQueries.addressesModelList.size() == 0) {
                addressName.setText("No Address");
                address.setText("-");
                pincode.setText("-");
            } else {
                setAddress();
            }
        }
    }

    private void loadCurrentOrder(){
        for (MyOrderItemModel orderItemModel : DBQueries.myOrderItemModelList) {
            Log.d("In for Loop", " " + String.valueOf(!orderItemModel.isCancellationRequested()));
            if (!orderItemModel.isCancellationRequested()) {
                Log.d(" Delivered", " " + !orderItemModel.getOrderStatus().equals("Delivered") + !orderItemModel.getOrderStatus().equals("Delivered"));
                if (!orderItemModel.getOrderStatus().equals("Delivered") && !orderItemModel.getOrderStatus().equals("Delivered")) {
                    layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
                    Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder_icon)).into(currentOrderImage);
                    tvCurrentOrderStatus.setText(orderItemModel.getOrderStatus());

                    switch (orderItemModel.getOrderStatus()) {
                        case "Ordered":
                            orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            break;

                        case "Packed":
                            orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            O_P_progress.setProgress(100);
                            break;

                        case "Shipped":
                            orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            O_P_progress.setProgress(100);
                            P_S_progress.setProgress(100);
                            break;

                        case "Out for Delivery":
                            orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            O_P_progress.setProgress(100);
                            P_S_progress.setProgress(100);
                            S_D_progress.setProgress(100);
                            break;
                    }
                }
            }
        }
        int i = 0;
        for (MyOrderItemModel myorderItemModel : DBQueries.myOrderItemModelList) {
            if (i < 4) {
                if (myorderItemModel.getOrderStatus().equals("Delivered")) {
                    Glide.with(getContext()).load(myorderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder_icon)).into((CircleImageView) recentOrdersContainer.getChildAt(i));
                    i++;
                }
            } else {
                break;
            }
        }
        if (i == 0) {
            yourRecentOrdersTitle.setText("No recent orders.");
        }
        if (i < 3) {
            for (int x = i; x < 4; x++) {
                recentOrdersContainer.getChildAt(x).setVisibility(View.GONE);
            }
        }
        loadingDialog.show();
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadingDialog.setOnDismissListener(null);
                if (DBQueries.addressesModelList.size() == 0) {
                    addressName.setText("No Address");
                    address.setText("-");
                    pincode.setText("-");
                } else {
                    setAddress();
                }
            }
        });
        DBQueries.loadAddresses(getContext(), loadingDialog, false);
        Log.d("Container", "Diab ");
    }

    private void setAddress() {
        String nameText, mobileNo;
        nameText = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getName();
        mobileNo = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getMobileNo();
        if(DBQueries.addressesModelList.get(DBQueries.selctedAddress).getAlternateMobileNo().equals("")) {
            addressName.setText(nameText + " - " + mobileNo);
        }else {
            addressName.setText(nameText + " - " + mobileNo + " or " + DBQueries.addressesModelList.get(DBQueries.selctedAddress).getAlternateMobileNo());
        }

        String flatNo = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getFlatNo();
        String locality = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getLocality();
        String landmark = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getLandmark();
        String city = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getCity();
        String state = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getState();

        if(landmark.equals("")) {
            address.setText(flatNo + ", " + locality + ", " + city + ", " + state);
        }else {
            address.setText(flatNo + ", " + locality + ", " + landmark + ", " + city + ", " + state);
        }
        pincode.setText(DBQueries.addressesModelList.get(DBQueries.selctedAddress).getPincode());
    }

    private void deleteSavedData() {
        SharedPreferences pref = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isUserLogin", false);
        editor.commit();
    }
}
