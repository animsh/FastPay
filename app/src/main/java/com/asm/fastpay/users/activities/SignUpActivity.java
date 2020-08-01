package com.asm.fastpay.users.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.asm.fastpay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {


    EditText setName;
    EditText setPassword;
    EditText reEnterPassword;
    EditText setPhone;
    Button btnSignUp;
    TextView txt_login_page;
    TextView headerSignUp;
    CheckBox isUserAgreed;
    EditText setEmail;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private ProgressDialog loadingBar;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // make activity full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        // ini values

        setName = findViewById(R.id.set_name);
        setPassword = findViewById(R.id.set_password);
        reEnterPassword = findViewById(R.id.set_address);
        setPhone = findViewById(R.id.set_phone);
        btnSignUp = findViewById(R.id.btn_sign_up);
        txt_login_page = findViewById(R.id.txt_login_page);
        headerSignUp = findViewById(R.id.head_sign_up_page);
        loadingBar = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        isUserAgreed = findViewById(R.id.agreeMent);
        setEmail = findViewById(R.id.set_email);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        txt_login_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAccount();

            }
        });

    }

    private void createAccount() {

        String name = setName.getText().toString();
        String password = setPassword.getText().toString();
        String phoneNumber = setPhone.getText().toString();
        String email = setEmail.getText().toString();
        String rePassword = reEnterPassword.getText().toString();
        boolean agreedDetails = isUserAgreed.isChecked();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            if (password.length() < 8) {
                Toast.makeText(this, "Password length should be 8 characters", Toast.LENGTH_SHORT).show();
            }
        } else if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            if (phoneNumber.length() != 10) {
                Toast.makeText(this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
            }
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            if (setEmail.getText().toString().matches(emailPattern)) {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            }
        } else if (TextUtils.isEmpty(rePassword)) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
        } else if (!(agreedDetails)) {
            Toast.makeText(this, "Please mark the box", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(rePassword)){
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait while we are checking your details");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhoneNumber(name, phoneNumber, email, password, rePassword);
        }

    }

    private void validatePhoneNumber(final String name, final String phoneNumber, final String email, final String password, final String address) {

        firebaseAuth.createUserWithEmailAndPassword(setEmail.getText().toString(), setPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> userdata = new HashMap<>();
                            userdata.put("fullName", name);
                            userdata.put("phoneNumber", phoneNumber);
                            userdata.put("email",setEmail.getText().toString());
                            userdata.put("profile","");

                            firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                    .set(userdata)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");
                                                //MAPS
                                                Map<String, Object> wishListMap = new HashMap<>();
                                                wishListMap.put("list_size", (long) 0);

                                                Map<String, Object> ratingsMap = new HashMap<>();
                                                ratingsMap.put("list_size", (long) 0);

                                                Map<String, Object> cartMap = new HashMap<>();
                                                cartMap.put("list_size", (long) 0);

                                                Map<String, Object> myAddressesMap = new HashMap<>();
                                                myAddressesMap.put("list_size", (long) 0);
                                                //MAPS


                                                final List<String> documentNames = new ArrayList<>();
                                                documentNames.add("MY_WISHLIST");
                                                documentNames.add(("MY_RATINGS"));
                                                documentNames.add("MY_CART");
                                                documentNames.add("MY_ADDRESSES");

                                                final List<Map<String, Object>> documentFields = new ArrayList<>();
                                                documentFields.add(wishListMap);
                                                documentFields.add(ratingsMap);
                                                documentFields.add(cartMap);
                                                documentFields.add(myAddressesMap);

                                                for (int x = 0; x < documentNames.size(); x++) {
                                                    final int finalX = x;
                                                    userDataReference.document(documentNames.get(x))
                                                            .set(documentFields.get(x))
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        if (finalX == documentNames.size() - 1) {
                                                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    } else {
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                        loadingBar.dismiss();
                    }
                });


    }
}
