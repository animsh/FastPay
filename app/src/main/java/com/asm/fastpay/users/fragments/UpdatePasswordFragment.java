package com.asm.fastpay.users.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asm.fastpay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdatePasswordFragment extends Fragment {

    public UpdatePasswordFragment() {
        // Required empty public constructor
    }

    private EditText oldPassword, newPassword, confirmNewPassword;
    private Button updateButton;
    private Dialog loadingDialog;
    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);

        oldPassword = view.findViewById(R.id.old_password);
        newPassword = view.findViewById(R.id.new_password);
        confirmNewPassword = view.findViewById(R.id.confirm_new_password);

        updateButton = view.findViewById(R.id.update_password_btn);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        email = getArguments().getString("Email");

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(oldPassword.getText())) {
                    Toast.makeText(getContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(newPassword.getText())) {
                    Toast.makeText(getContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                    if (newPassword.getText().length() < 8) {
                        Toast.makeText(getContext(), "Password length should be 8 characters", Toast.LENGTH_SHORT).show();
                    }
                } else if (TextUtils.isEmpty(confirmNewPassword.getText())) {
                    Toast.makeText(getContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                    if (newPassword.getText() != confirmNewPassword.getText()) {
                        Toast.makeText(getContext(), "Password does not match.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadingDialog.show();
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(email,oldPassword.getText().toString());

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            oldPassword.setText(null);
                                            newPassword.setText(null);
                                            confirmNewPassword.setText(null);
                                            getActivity().finish();
                                            Toast.makeText(getContext(),"Password has been updated!!!",Toast.LENGTH_SHORT).show();
                                        }else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(getContext(),"",Toast.LENGTH_SHORT).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }else {
                                loadingDialog.dismiss();
                                String error = task.getException().getMessage();
                                Toast.makeText(getContext(),"",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });
        return view;
    }
}
