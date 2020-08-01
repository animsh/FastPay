package com.asm.fastpay.users.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.asm.fastpay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgetPasswordActivity extends AppCompatActivity {

    TextView txtPasswordTitle;
    TextView txtPasswordDetail;
    Button btnForgetPassword;
    EditText getEmail;
    ImageView img;
    Button btnBack;
    TextView otpSentSuccessfully;
    ProgressBar otpProgress;
    String parentDBName = "Users";
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make activity full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().hide();


        //ini views

        txtPasswordTitle = findViewById(R.id.txt_forget_password);
        txtPasswordDetail = findViewById(R.id.txt_forget_password_details);
        getEmail = findViewById(R.id.get_email);
        btnForgetPassword = findViewById(R.id.btn_forget_password);
        img = findViewById(R.id.img_forget_password);
        btnBack = findViewById(R.id.btn_back);
        otpSentSuccessfully = findViewById(R.id.txt_otp_sent_msg);
        otpProgress = findViewById(R.id.otp_sent_progressbar);

        txtPasswordTitle.setText("Forgot Password ?");
        txtPasswordDetail.setText("We just need your registered phone number to send you password reset code");

        firebaseAuth = FirebaseAuth.getInstance();


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                otpProgress.setVisibility(View.VISIBLE);


                String email = getEmail.getText().toString().trim();

                if (email.equals("")) {
                    Toast.makeText(ForgetPasswordActivity.this, "Enter mail", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetPasswordActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                otpProgress.setVisibility(View.INVISIBLE);
                                otpSentSuccessfully.setVisibility(View.VISIBLE);
                            } else {
                                otpProgress.setVisibility(View.INVISIBLE);
                                String error = task.getException().getMessage();
                                Toast.makeText(ForgetPasswordActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}