package com.asm.fastpay.users.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.asm.fastpay.R;
import com.asm.fastpay.admin.AdminHomePageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    EditText getUserEmail;
    EditText getPassword;
    ImageView imageLogin;
    TextView txtSignUpPage;
    TextView adminPage;
    TextView notAdminPage;
    public static boolean isNotAdmin = true;
    public static boolean isAdmin = false;
    TextView txtForgetPassword;
    FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    private String parentDBName = "Users";
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // make the activity full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // check activity id already opened

        if (restorePrefData()) {
            Intent homePageActivity = new Intent(MainActivity.this, HomePageActivity.class);
            startActivity(homePageActivity);
            finish();
        }

        if (restoreAdminPrefData()) {
            Intent adminHomePageActivity = new Intent(MainActivity.this, AdminHomePageActivity.class);
            startActivity(adminHomePageActivity);
            finish();
        }

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        // ini objects

        btnLogin = findViewById(R.id.btn_login);
        getUserEmail = findViewById(R.id.login_username);
        getPassword = findViewById(R.id.login_password);
        imageLogin = findViewById(R.id.image_login);
        txtSignUpPage = findViewById(R.id.txt_sign_up_page);
        adminPage = findViewById(R.id.txt_admin);
        notAdminPage = findViewById(R.id.txt_not_admin);
        txtForgetPassword = findViewById(R.id.txt_forgot_password_page);

        firebaseAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        imageLogin.setImageResource(R.drawable.signin);

        txtSignUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();

            }
        });

        adminPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnLogin.setText("Login as Admin");
                adminPage.setVisibility(View.INVISIBLE);
                notAdminPage.setVisibility(View.VISIBLE);
                parentDBName = "Admins";
                isNotAdmin = false;
                isAdmin = true;
            }
        });

        notAdminPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnLogin.setText("Login");
                adminPage.setVisibility(View.VISIBLE);
                notAdminPage.setVisibility(View.INVISIBLE);
                parentDBName = "Users";
                isNotAdmin = true;
                isAdmin = false;
            }
        });

        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);

            }
        });
    }

    private void loginUser() {

        String password = getPassword.getText().toString();
        String email = getUserEmail.getText().toString();

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            if (password.length() < 8) {
                Toast.makeText(this, "Password length should be 8 characters", Toast.LENGTH_SHORT).show();
            }
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            if (getUserEmail.getText().toString().matches(emailPattern)) {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            }
        } else {
            loadingBar.setTitle("Login You In");
            loadingBar.setMessage("Please wait while we are checking your details");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(email, password);

        }

    }

    private void allowAccessToAccount(final String email, final String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(btnLogin.getText() =="Login as Admin") {
                                saveAdminPrefsData();
                                Intent intent = new Intent(MainActivity.this, AdminHomePageActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                savePrefsData();
                                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

    }

    public boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isActivityOpenedBefore = pref.getBoolean("isUserLogin", false);
        return isActivityOpenedBefore;


    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isUserLogin", true);
        editor.commit();
    }

    public boolean restoreAdminPrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isActivityOpenedBefore = pref.getBoolean("isAdminLogin", false);
        return isActivityOpenedBefore;


    }

    private void saveAdminPrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isAdminLogin", true);
        editor.commit();
    }
}
