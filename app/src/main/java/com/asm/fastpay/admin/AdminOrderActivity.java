package com.asm.fastpay.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asm.fastpay.models.MyOrderItemModel;
import com.asm.fastpay.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminOrderActivity extends AppCompatActivity {

    private int position;

    private TextView title, price, quantity, cuttedPrice;
    private ImageView productImage;
    private TextView orderedDate, packedDate, shippedDate, deliveredDate, cancellationStatus, totalAmount, deliveryChanges;
    private TextView fullName, address, pincode;
    private TextView paymentType;
    private EditText orderStatus;
    private SimpleDateFormat simpleDateFormat;
    private Button updateButton;
    private String orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        position = getIntent().getIntExtra("Position", -1);
        final MyOrderItemModel model = AdminDBQueries.myOrderItemModelList.get(position);

        title = findViewById(R.id.product_title);
        price = findViewById(R.id.cuttes_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        quantity = findViewById(R.id.product_quantity);
        productImage = findViewById(R.id.product_image);
        orderedDate = findViewById(R.id.ordered_date);
        packedDate = findViewById(R.id.packed_date);
        shippedDate = findViewById(R.id.shipped_date);
        deliveredDate = findViewById(R.id.delivered_date);
        fullName = findViewById(R.id.full_name);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        paymentType = findViewById(R.id.payment_type);
        cancellationStatus = findViewById(R.id.cancellation_status);
        orderStatus = findViewById(R.id.order_status);
        deliveryChanges = findViewById(R.id.delivery_chanrges);
        totalAmount = findViewById(R.id.total_amount);
        updateButton = findViewById(R.id.update_buuton);

        simpleDateFormat = new SimpleDateFormat("EEE,dd MMM YYYY hh:mm aa");

        fullName.setText(model.getFullName());
        address.setText(model.getAddress());
        pincode.setText(model.getPincode());

        title.setText(model.getProductTitle());
        price.setText("Rs. " + model.getProductPrice() + "/-");
        cuttedPrice.setText("Rs. " + model.getCuttedPrice() + "/-");
        quantity.setText(String.valueOf("Quantity: " + model.getProductQuantity()));
        Glide.with(this).load(model.getProductImage()).into(productImage);
        paymentType.setText("Payment Type: " + model.getPaymentMethod());


        if (model.isCancellationRequested()) {
            cancellationStatus.setText("Cancellation Requested:  " + model.isCancellationRequested());
        } else {
            cancellationStatus.setText("Cancellation Requested:  " + model.isCancellationRequested());
        }

        orderID = model.getOrderID();
        final String status = orderStatus.getText().toString().trim();

        long totalItemsPriceValue;
        totalItemsPriceValue = model.getProductQuantity() * Long.valueOf(model.getProductPrice());
        if (model.getDeliveryPrice().equals("FREE")) {
            deliveryChanges.setText("Delivery Changes: " + model.getDeliveryPrice());
            totalAmount.setText("Total Amount: " + "Rs. " + String.valueOf(totalItemsPriceValue) + "/-");
        } else {
            deliveryChanges.setText("Delivery Changes: " + "Rs. " + model.getDeliveryPrice() + "/-");
            totalAmount.setText("Total Amount: " + "Rs. " + (totalItemsPriceValue + Long.valueOf(model.getDeliveryPrice())) + "/-");
        }

        loadStatus(model);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminOrderActivity.this,"Done",Toast.LENGTH_SHORT).show();
                if(orderStatus.getText().toString().trim().equals(status)){
                    Toast.makeText(AdminOrderActivity.this,"Not Available",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AdminOrderActivity.this,"Done",Toast.LENGTH_SHORT).show();
                    String dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss aa", Locale.getDefault()).format(System.currentTimeMillis());
                    Map<String,Object> map = new HashMap<>();
                    if(orderStatus.getText().toString().equals("Packed")){
                        map.put("Order Status","Packed");
                        map.put("Packed Date", FieldValue.serverTimestamp());
                    } else if (orderStatus.getText().toString().equals("Shipped")){
                        map.put("Order Status","Shipped");
                        map.put("Shipped Date",FieldValue.serverTimestamp());
                    } else if (orderStatus.getText().toString().equals("Delivered")){
                        map.put("Order Status","Delivered");
                        map.put("Delivered Date",FieldValue.serverTimestamp());
                    } else if(orderStatus.getText().toString().equals("Cancelled")){
                        map.put("Order Status","Cancelled");
                        map.put("Cancelled Date",FieldValue.serverTimestamp());
                    }

                    /*FirebaseFirestore.getInstance().collection("CANCELLED ORDERS").document().set(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("OrderID", "onComplete: " + String.valueOf(model.getOrderID()));
                                        Log.d("ProductID", "onComplete: " + String.valueOf(model.getProductID()));

                                    }else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(OrderDetailsActivity.this,"Error: "+ error,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
*/
                    FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderID()).collection("OrderItems").document(model.getProductID()).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AdminOrderActivity.this,"Done",Toast.LENGTH_SHORT).show();
                                        AdminDBQueries.loadOrders(AdminOrderActivity.this,OrderIdItemsActivity.myOrderAdapter,true,orderID);
                                        finish();
                                    }else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(AdminOrderActivity.this,"Error: "+ error,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void loadStatus(MyOrderItemModel model){
        switch (model.getOrderStatus()) {
            case "Ordered":
                orderedDate.setText("Ordered Date: " + (simpleDateFormat.format(model.getOrderedDate())));
                orderStatus.setText("Ordered");
                packedDate.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                break;

            case "Packed":
                orderedDate.setText("Ordered Date: " + (simpleDateFormat.format(model.getOrderedDate())));
                packedDate.setText("Packed Date: " + (simpleDateFormat.format(model.getPackedDate())));
                orderStatus.setText("Packed");
                shippedDate.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                break;

            case "Shipped":
                orderedDate.setText("Ordered Date: " + (simpleDateFormat.format(model.getOrderedDate())));
                packedDate.setText("Packed Date: " + (simpleDateFormat.format(model.getPackedDate())));
                shippedDate.setText("Shipped Date: " + (simpleDateFormat.format(model.getShippedDate())));
                orderStatus.setText("Shipped");
                deliveredDate.setVisibility(View.GONE);
                break;

            case "Out for Delivery":
                orderedDate.setText("Ordered Date: " + (simpleDateFormat.format(model.getOrderedDate())));
                packedDate.setText("Packed Date: " + (simpleDateFormat.format(model.getPackedDate())));
                shippedDate.setText("Shipped Date: " + (simpleDateFormat.format(model.getShippedDate())));
                deliveredDate.setText("Delivered Date: " + (simpleDateFormat.format(model.getDeliveredDate())));
                orderStatus.setText("Out for Delivery");

                break;
            case "Delivered":
                orderedDate.setText("Ordered Date: " + (simpleDateFormat.format(model.getOrderedDate())));
                packedDate.setText("Packed Date: " + (simpleDateFormat.format(model.getPackedDate())));
                shippedDate.setText("Shipped Date: " + (simpleDateFormat.format(model.getShippedDate())));
                deliveredDate.setText("Delivered Date: " + (simpleDateFormat.format(model.getDeliveredDate())));
                orderStatus.setText("Delivered");
                break;

            case "Cancelled":

                if (model.getPackedDate().after(model.getOrderedDate())) {
                    if (model.getShippedDate().after(model.getPackedDate())) {
                        orderedDate.setText("Ordered Date: " + (simpleDateFormat.format(model.getOrderedDate())));
                        packedDate.setText("Packed Date: " + (simpleDateFormat.format(model.getPackedDate())));
                        shippedDate.setText("Shipped Date: " + (simpleDateFormat.format(model.getShippedDate())));
                        deliveredDate.setText("Cancelled Date: " + (simpleDateFormat.format(model.getCancelledDate())));
                    } else {
                        orderedDate.setText("Ordered Date: " + (simpleDateFormat.format(model.getOrderedDate())));
                        packedDate.setText("Packed Date: " + (simpleDateFormat.format(model.getPackedDate())));
                        shippedDate.setText("Cancelled Date: " + (simpleDateFormat.format(model.getCancelledDate())));
                        deliveredDate.setVisibility(View.GONE);
                    }
                } else {
                    orderedDate.setText("Ordered Date: " + (simpleDateFormat.format(model.getOrderedDate())));
                    packedDate.setText("Cancelled Date: " + (simpleDateFormat.format(model.getCancelledDate())));
                    shippedDate.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                }
                cancellationStatus.setVisibility(View.GONE);
                orderStatus.setText("Cancelled");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OrderIdItemsActivity.myOrderAdapter.notifyDataSetChanged();
    }
}
