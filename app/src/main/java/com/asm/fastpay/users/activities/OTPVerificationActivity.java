package com.asm.fastpay.users.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asm.fastpay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPVerificationActivity extends AppCompatActivity {

    private TextView phoneNo;
    private EditText otp;
    private Button verifyBtn;
    private String userNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        phoneNo = findViewById(R.id.phone_no);
        otp = findViewById(R.id.otp);
        verifyBtn = findViewById(R.id.verify_btn);
        userNo = getIntent().getStringExtra("mobileNo");
        phoneNo.setText("Verification code has been sent to +91" + userNo);

        Random random = new Random();
        final int OTP_NUMBER = random.nextInt(999999 - 111111) + 111111;
        String SMS_API = "https://www.fast2sms.com/dev/bulk";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (otp.getText().toString().equals(String.valueOf(OTP_NUMBER))){
                            Map<String, Object> updateStatus = new HashMap<>();
                            updateStatus.put("Order Status", "Ordered");
                            final String OrderID = getIntent().getStringExtra("OrderID");
                            FirebaseFirestore.getInstance().collection("ORDERS").document(OrderID).update(updateStatus)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Map<String,Object> userOrders = new HashMap<>();
                                                userOrders.put("order_id", OrderID);
                                                userOrders.put("time", FieldValue.serverTimestamp());
                                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(OrderID).set(userOrders)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    DeliveryActivity.codOrderConfirmed = true;
                                                                    finish();
                                                                }else {
                                                                    Toast.makeText(OTPVerificationActivity.this, "Oops! failed to update user order list...", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(OTPVerificationActivity.this, "Order Cancelled", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        }else {
                            Toast.makeText(OTPVerificationActivity.this,"OTP does not match!!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(OTPVerificationActivity.this,"Failed to send OTP verification code." + error,Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization","jRIqNDuL34PKexrWO1FZY8ftgwMkb7AVnims6hdaz52lG9BCEXz5PbhZ0E67iCmAYTSdloys28xva4KM");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("sender_id","FSTSMS");
                body.put("language","english");
                body.put("route","qt");
                body.put("numbers",userNo);
                body.put("message","23666");
                body.put("variables","{#BB#}");
                body.put("variables_values", String.valueOf(OTP_NUMBER));
                return body;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(OTPVerificationActivity.this);
        requestQueue.add(stringRequest);
    }
}
