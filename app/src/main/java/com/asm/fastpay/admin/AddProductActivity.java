package com.asm.fastpay.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.asm.fastpay.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.internal.LogExceptionRunnable;

public class AddProductActivity extends AppCompatActivity {

    private EditText productTitle, productPrice, productDescription, quantity, cuttedPrice, barcode, productSubTitle, productSubDetails, productTags;
    private Button addProductInButton, uploadImageButton, deleteProductButton;
    private AutoCompleteTextView productType;
    private ImageView productImage;
    StorageReference storageReference;
    public Uri imageUri;
    private String productID;
    private Dialog loadingDialog;
    private boolean update;
    private String resource, newResources = "null", tgs, averageRatings;
    boolean updatePhoto = false;
    private List<Long> ratings;

    private static final String[] TYPES = new String[]{
            "biscuit", "chocolates", "vegetables", "dairy"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        getSupportActionBar().setTitle("Add Product");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        productID = getIntent().getStringExtra("PRODUCT_ID");
        update = getIntent().getBooleanExtra("UPDATE", false);

        productTitle = findViewById(R.id.product_title);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.product_cutted_price);
        quantity = findViewById(R.id.quantity);
        productDescription = findViewById(R.id.product_description);
        addProductInButton = findViewById(R.id.save_btn);
        barcode = findViewById(R.id.barcode);
        productImage = findViewById(R.id.product_image);
        productSubTitle = findViewById(R.id.product_sub_title);
        productSubDetails = findViewById(R.id.product_size);
        productTags = findViewById(R.id.tags);
        uploadImageButton = findViewById(R.id.upload_image);
        deleteProductButton = findViewById(R.id.delete_btn);
        storageReference = FirebaseStorage.getInstance().getReference("Products/");

        productType = findViewById(R.id.product_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, TYPES);
        productType.setAdapter(adapter);

        Glide.with(this).load(R.drawable.horizontal).into(productImage);

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 1);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);
                }
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadPhoto();
                } else {
                    Toast.makeText(AddProductActivity.this, "Please select image first by clicking on image.", Toast.LENGTH_LONG);
                }
            }
        });
        if (update) {
            loadingDialog.show();
            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("PRODUCTS").document(productID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        final DocumentSnapshot documentSnapshot = task.getResult();
                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            resource = documentSnapshot.get("product_image_1").toString();
                                            productTitle.setText(documentSnapshot.get("product_title").toString());
                                            productPrice.setText(documentSnapshot.get("product_price").toString());
                                            cuttedPrice.setText(documentSnapshot.get("cutted_price").toString());
                                            barcode.setText(productID);
                                            productDescription.setText(documentSnapshot.get("product_other_details").toString());
                                            productSubTitle.setText(documentSnapshot.get("product_sub_title").toString());
                                            productSubDetails.setText(documentSnapshot.get("product_sub_details").toString());
                                            quantity.setText(String.valueOf((long) documentSnapshot.get("stock_quantity")));
                                            Glide.with(AddProductActivity.this).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder_icon)).into(productImage);
                                            List<String> oldTags = (List<String>) documentSnapshot.get("tags");
                                            tgs = oldTags.get(0).toString();
                                            for (int i = 1; i < oldTags.size(); i++) {
                                                tgs = tgs + "," + oldTags.get(i);
                                            }
                                            ratings = new ArrayList<>();
                                            for (int i = 1; i < 6; i++) {
                                                ratings.add((long) documentSnapshot.get(i + "_star"));
                                                Log.e("Rating: " + i, String.valueOf((long) documentSnapshot.get(i + "_star")));
                                            }
                                            averageRatings = String.valueOf(documentSnapshot.get("average_rating"));
                                            productTags.setText(tgs);
                                            productType.setText(documentSnapshot.get("type").toString());
                                            loadingDialog.dismiss();
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(AddProductActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismiss();
                                        }
                                    }
                                });
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(AddProductActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                }
            });
            deleteProductButton.setVisibility(View.VISIBLE);
            deleteProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDialog.show();
                    firebaseFirestore.collection("PRODUCTS").document(productID)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loadingDialog.dismiss();
                                    Log.d("Product Deleted", "DocumentSnapshot successfully deleted!");
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingDialog.dismiss();
                                    Log.w("Product Deleted", "Error deleting document", e);
                                }
                            });
                }
            });
        }


        addProductInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!productTitle.getText().toString().equals("")) {
                    if (!productPrice.getText().toString().equals("")) {
                        if (!cuttedPrice.getText().toString().equals("")) {
                            if (!quantity.getText().toString().equals("")) {
                                if (!productDescription.getText().toString().equals("")) {
                                    if (!barcode.getText().toString().equals("")) {
                                        if (!productSubTitle.getText().toString().equals("")) {
                                            if (!productSubDetails.getText().toString().equals("")) {
                                                addProductData();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(AddProductActivity.this, "Barcode can't be empty!!!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(AddProductActivity.this, "Description can't be empty!!!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AddProductActivity.this, "Quantity can't be empty!!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddProductActivity.this, "MRP can't be empty!!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddProductActivity.this, "Discounted MRP can't be empty!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddProductActivity.this, "Product title can't be empty!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void uploadPhoto() {
        loadingDialog.show();
        if (imageUri != null) {
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExtension(imageUri));
            /*reference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri;
                                    Toast.makeText(AddProductActivity.this, "Image is uploaded", Toast.LENGTH_LONG);
                                    Log.e("Image Link: " , downloadUrl.toString());
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });*/

            Glide.with(this).asBitmap().load(imageUri).into(new ImageViewTarget<Bitmap>(productImage) {

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    final UploadTask uploadTask = reference.putBytes(data);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            imageUri = task.getResult();
                                            newResources = task.getResult().toString();
                                            updatePhoto = true;
                                            Log.e("New", "onComplete: new Resources: " + newResources);
                                            Glide.with(AddProductActivity.this).load(newResources).into(productImage);
                                            loadingDialog.dismiss();
                                        } else {
                                            updatePhoto = false;
                                            loadingDialog.dismiss();
                                            String error = task.getException().getMessage();
                                            Toast.makeText(AddProductActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                updatePhoto = false;
                                loadingDialog.dismiss();
                                String error = task.getException().getMessage();
                                //String error = uploadTask.getException().getMessage();
                                Toast.makeText(AddProductActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    return;
                }

                @Override
                protected void setResource(@Nullable Bitmap resource) {
                    productImage.setImageResource(R.drawable.horizontal);
                }
            });
        }

    }

    private void addProductData() {
        loadingDialog.show();

        boolean imageSelected;
        if (!update) {
            if (imageUri == null) {
                Toast.makeText(AddProductActivity.this, "Error: Please select image by clicking on above image.", Toast.LENGTH_SHORT).show();
                imageSelected = false;
            } else {
                imageSelected = true;
            }
        } else {
            imageSelected = true;
        }
        List<String> uploadTag = Arrays.asList(productTags.getText().toString().split(","));
        Map<String, Object> productDetails = new HashMap<>();
        productDetails.put("product_title", productTitle.getText().toString());
        //productDetails.put("product_sub_title", productSubTitle.getText().toString());
        productDetails.put("product_sub_title", productSubTitle.getText().toString());
        productDetails.put("product_sub_details", productSubDetails.getText().toString());
        productDetails.put("COD", true);

        productDetails.put("cutted_price", cuttedPrice.getText().toString());
        productDetails.put("free_coupon_body", "Rewards");
        productDetails.put("free_coupon_title", "Discount");
        productDetails.put("free_coupons", Long.parseLong("0"));
        productDetails.put("max_quantity", Long.parseLong("5"));
        productDetails.put("no_of_product_images", Long.parseLong("1"));
        productDetails.put("product_description", productDescription.getText().toString());
        if (update) {
            for (int i = 1; i < 6; i++) {
                productDetails.put(i + "_star", ratings.get(i - 1));
                Log.e("Rating: " + i, String.valueOf(ratings.get(i - 1)));
            }
            productDetails.put("average_rating", averageRatings);
        } else {
            for (int i = 1; i < 6; i++) {
                productDetails.put(i + "_star", Long.parseLong("0"));
            }
            productDetails.put("average_rating", 0);
        }
        if (!updatePhoto) {
            productDetails.put("product_image_1", resource);
        } else {
            productDetails.put("product_image_1", newResources);
        }
        productDetails.put("product_other_details", productDescription.getText().toString());
        productDetails.put("product_price", productPrice.getText().toString());
        productDetails.put("stock_quantity", Long.parseLong(quantity.getText().toString()));
        productDetails.put("total_ratings", Long.parseLong("0"));
        productDetails.put("total_spec_title", Long.parseLong("0"));
        productDetails.put("use_tab_layout", false);
        productDetails.put("tags", uploadTag);
        productDetails.put("type", productType.getText().toString());

        boolean uploadData;

        if(!update){
            if(!updatePhoto){
                Toast.makeText(AddProductActivity.this,"Please upload image",Toast.LENGTH_LONG).show();;
                uploadData = false;
            } else {
                uploadData = true;
            }
        } else {
            if(imageUri != null){
                if(!updatePhoto){
                    Toast.makeText(AddProductActivity.this,"Please upload image",Toast.LENGTH_LONG).show();;
                    uploadData = false;
                } else {
                    uploadData = true;
                }
            } else {
                uploadData = true;
            }
        }


        if (uploadData) {

            FirebaseFirestore.getInstance().collection("PRODUCTS").document(barcode.getText().toString()).set(productDetails)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddProductActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(AddProductActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                            finish();
                        }
                    });
        } else {
            loadingDialog.dismiss();
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == this.RESULT_OK) {
                if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    imageUri = data.getData();
                    productImage.setImageURI(imageUri);
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
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
