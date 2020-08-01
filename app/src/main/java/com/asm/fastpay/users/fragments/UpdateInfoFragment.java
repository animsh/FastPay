package com.asm.fastpay.users.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateInfoFragment extends Fragment {

    public UpdateInfoFragment() {
        // Required empty public constructor
    }

    private CircleImageView circleImageView;
    private Button changePhotoBtn, removePhotoBtn, updateBtn, doneBtn;
    private EditText nameField, emailField, passwordField;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private Dialog loadingDialog, passwordDialog;
    private String name;
    private String email;
    private String photo;
    private Uri imageUri;
    private boolean updatePhoto = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_info, container, false);

        circleImageView = view.findViewById(R.id.profile_image);
        changePhotoBtn = view.findViewById(R.id.change_photo_btn);
        removePhotoBtn = view.findViewById(R.id.remove_photo_btn);
        updateBtn = view.findViewById(R.id.update);

        nameField = view.findViewById(R.id.name);
        emailField = view.findViewById(R.id.email);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        passwordDialog = new Dialog(getContext());
        passwordDialog.setContentView(R.layout.password_confirmation_dialog);
        passwordDialog.setCancelable(true);
        passwordDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        passwordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        passwordField = passwordDialog.findViewById(R.id.password);
        doneBtn = passwordDialog.findViewById(R.id.done_btn);

        assert getArguments() != null;
        name = getArguments().getString("Name");
        email = getArguments().getString("Email");
        photo = getArguments().getString("Photo");



        Glide.with(getContext()).load(photo).into(circleImageView);
        nameField.setText(name);
        emailField.setText(email);

        changePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, 1);
                    } else {
                        getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    }
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 1);
                }
            }
        });

        removePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                updatePhoto = true;
                Glide.with(getContext()).load(R.drawable.profile_male).into(circleImageView);
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nameField.getText())) {
                    Toast.makeText(getContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(emailField.getText())) {
                    Toast.makeText(getContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                    if (emailField.getText().toString().matches(emailPattern)) {
                        Toast.makeText(getContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(emailField.getText().toString().toLowerCase().trim().equals(email.toLowerCase().trim())){
                        //same email
                        loadingDialog.show();
                        updatePhoto(user);
                    }else {
                        //update email
                        passwordDialog.show();
                        doneBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadingDialog.show();
                                final String userPassword = passwordField.getText().toString().trim();
                                passwordDialog.dismiss();
                                AuthCredential credential = EmailAuthProvider.getCredential(email,userPassword);

                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            user.updateEmail(emailField.getText().toString().toLowerCase().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull final Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        //updating photo
                                                        updatePhoto(user);
                                                        //updating photo
                                                    }else {
                                                        loadingDialog.dismiss();
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getContext(),"Error: "+ error,Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }else {
                                            loadingDialog.dismiss();
                                            String error = task.getException().getMessage();
                                            Toast.makeText(getContext(),"Error: "+ error,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }

                }
            }
        });
        return view;
    }

    private void updatePhoto(final FirebaseUser user){
        if(updatePhoto){
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile/" + user.getUid() + ".jpg");
            if(imageUri != null){

                Glide.with(getContext()).asBitmap().load(imageUri).circleCrop().into(new ImageViewTarget<Bitmap>(circleImageView) {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        final UploadTask uploadTask = storageReference.putBytes(data);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if(task.isSuccessful()){
                                                imageUri = task.getResult();
                                                DBQueries.profile = task.getResult().toString();
                                                Glide.with(getContext()).load(DBQueries.profile).into(circleImageView);

                                                Map<String,Object> updateData = new HashMap<>();
                                                updateData.put("email",emailField.getText().toString());
                                                updateData.put("fullName",nameField.getText().toString());
                                                updateData.put("profile",DBQueries.profile);

                                                updateFields(user,updateData);
                                            }else {
                                                loadingDialog.dismiss();
                                                DBQueries.profile = "";
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getContext(),"Error: "+ error,Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    //String error = uploadTask.getException().getMessage();
                                    Toast.makeText(getContext(),"Error: "+ error,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        return;
                    }

                    @Override
                    protected void setResource(@Nullable Bitmap resource) {
                        circleImageView.setImageResource(R.drawable.profile_male);
                    }
                });
            }else {
                //remove photo
                storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            DBQueries.profile = "";

                            Map<String,Object> updateData = new HashMap<>();
                            updateData.put("email",emailField.getText().toString());
                            updateData.put("fullName",nameField.getText().toString());
                            updateData.put("profile","");

                            updateFields(user,updateData);
                        }else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(),"Error: "+ error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else {
            Map<String,Object> updateData = new HashMap<>();
            updateData.put("email",emailField.getText().toString());
            updateData.put("fullName",nameField.getText().toString());
            updateFields(user,updateData);
        }
    }

    private void updateFields(FirebaseUser user, final Map<String,Object> updateData){
        FirebaseFirestore.getInstance().collection("USERS").document(user.getUid()).update(updateData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(updateData.size() > 1){
                                DBQueries.email = emailField.getText().toString().trim();
                                DBQueries.fullName = nameField.getText().toString().trim();
                            }else {
                                DBQueries.fullName = nameField.getText().toString().trim();
                            }
                            getActivity().finish();
                            Toast.makeText(getContext(),"Successfully Updated!!",Toast.LENGTH_LONG).show();
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(),"Error: "+ error,Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    updatePhoto = true;
                    Glide.with(getContext()).load(imageUri).into(circleImageView);
                } else {
                    Toast.makeText(getContext(), "Image not found!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
