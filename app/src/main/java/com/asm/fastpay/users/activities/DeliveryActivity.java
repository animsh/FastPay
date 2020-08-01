package com.asm.fastpay.users.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.asm.fastpay.DBQueries;
import com.asm.fastpay.R;
import com.asm.fastpay.Upload;
import com.asm.fastpay.models.CartItemModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.asm.fastpay.adapters.CartAdapter;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {


    public static final int SELECT_ADDRESS = 0;
    private static final int STORAGE_CODE = 1000;
    private RecyclerView deliveryRecyclerView;
    private Button changeOrAddAddressBtn;
    private TextView totalAmount;
    private TextView fullName;
    private String name, mobileNo;
    private TextView fullAddress;
    private TextView pincode;
    public static List<CartItemModel> cartItemModelList;
    private Button continueBtn;
    public static Dialog loadingDialog;
    private String paymentMethod = "PAYTM";
    private Dialog paymentMethodDialog;
    private RadioGroup paymentMthodGroup;
    private RadioButton paytm;
    private RadioButton cashOnDelivery;
    private Button pay, cancelDialog;
    private ConstraintLayout orderConfirmationLayout;
    private ImageButton continueShoppingBtn;
    private TextView orderID;
    private String order_ID;
    private boolean successResponce = false;
    public static boolean fromCart;
    public static boolean codOrderConfirmed = false;
    private FirebaseFirestore firebaseFirestore;
    public static boolean getQtyIDs = true;
    public static CartAdapter cartAdapter;
    public static boolean isOfflineMode = false;
    private ConstraintLayout constraintLayout;
    private ProgressDialog progressDialog;
    private String userIdentity = FirebaseAuth.getInstance().getUid();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        paymentMethodDialog = new Dialog(this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;


        deliveryRecyclerView = findViewById(R.id.delivery_recyclerview);
        changeOrAddAddressBtn = findViewById(R.id.change_or_add_address_btn);
        totalAmount = findViewById(R.id.total_cart_amount);
        paymentMthodGroup = paymentMethodDialog.findViewById(R.id.payment_methods);
        paytm = paymentMethodDialog.findViewById(R.id.paytm);
        cashOnDelivery = paymentMethodDialog.findViewById(R.id.cash_on_delivery);
        pay = paymentMethodDialog.findViewById(R.id.pay);
        cancelDialog = paymentMethodDialog.findViewById(R.id.cancel_dialog);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.continue_shopping_btn);
        orderID = findViewById(R.id.order_id);
        fullName = findViewById(R.id.fullname);
        fullAddress = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        continueBtn = findViewById(R.id.cart_continue_btn);
        order_ID = UUID.randomUUID().toString().substring(0, 28);
        constraintLayout = findViewById(R.id.delivery_constraint_layout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrAddAddressBtn.setVisibility(View.VISIBLE);
        changeOrAddAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getQtyIDs = false;
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allProductsAvailable = true;
                for (CartItemModel cartItemModel : cartItemModelList) {
                    if (cartItemModel.isQtyError()) {
                        allProductsAvailable = false;
                        break;
                    }
                    if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCOD()) {
                            cashOnDelivery.setEnabled(false);
                            //cashOnDelivery.setVisibility(View.GONE);
                            break;
                        } else {
                            cashOnDelivery.setEnabled(true);
                            //cashOnDelivery.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (allProductsAvailable) {
                    if(!DBQueries.atHome){
                        cashOnDelivery.setEnabled(false);
                    }else {
                        cashOnDelivery.setEnabled(true);
                    }
                    paymentMethodDialog.show();
                }

            }
        });
        verifyStoragePermissions(this);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethodDialog.dismiss();
                loadingDialog.show();
                if (paytm.isChecked()) {
                    paymentMethod = "PAYTM";
                    if (DBQueries.atHome) {
                        placeOrderDetails();
                    } else {
                        placeOfflineOrder();
                    }
                } else if (cashOnDelivery.isChecked()) {
                    paymentMethod = "COD";
                    if (DBQueries.atHome) {
                        placeOrderDetails();
                    } else {
                        placeOfflineOrder();
                    }
                } else if (!paytm.isChecked() && !cashOnDelivery.isChecked()) {
                    Toast.makeText(DeliveryActivity.this, "Please select payment method!!!", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }
        });

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethodDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //accessing quantity
        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++) {
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);
                    Map<String, Object> timestamp = new HashMap<>();
                    timestamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                    if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()) {
                                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            List<String> serverQuantity = new ArrayList<>();
                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                            }
                                                            long availableQty = 0;
                                                            boolean noLongerAvailable = true;
                                                            for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()) {
                                                                if (!serverQuantity.contains(qtyId)) {
                                                                    if (noLongerAvailable) {
                                                                        cartItemModelList.get(finalX).setInStock(false);
                                                                    } else {
                                                                        cartItemModelList.get(finalX).setQtyError(true);
                                                                        cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                        Toast.makeText(DeliveryActivity.this, "Sorry! all products may not be available in required quantity...", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } else {
                                                                    availableQty++;
                                                                    noLongerAvailable = false;
                                                                }
                                                            }
                                                            cartAdapter.notifyDataSetChanged();
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(DeliveryActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        } else {
            getQtyIDs = true;
        }
        cartAdapter.notifyDataSetChanged();
        //accessing quantity
        if (DBQueries.atHome) {
            if (DBQueries.addressesModelList.size() != 0) {
                name = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getName();
                mobileNo = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getMobileNo();
                if (DBQueries.addressesModelList.get(DBQueries.selctedAddress).getAlternateMobileNo().equals("")) {
                    fullName.setText(name + " - " + mobileNo);
                } else {
                    fullName.setText(name + " - " + mobileNo + " or " + DBQueries.addressesModelList.get(DBQueries.selctedAddress).getAlternateMobileNo());
                }

                String flatNo = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getFlatNo();
                String locality = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getLocality();
                String landmark = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getLandmark();
                String city = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getCity();
                String state = DBQueries.addressesModelList.get(DBQueries.selctedAddress).getState();

                if (landmark.equals("")) {
                    fullAddress.setText(flatNo + ", " + locality + ", " + city + ", " + state);
                } else {
                    fullAddress.setText(flatNo + ", " + locality + ", " + landmark + ", " + city + ", " + state);
                }
                pincode.setText(DBQueries.addressesModelList.get(DBQueries.selctedAddress).getPincode());
            }
        } else {
            constraintLayout.getChildAt(1).setVisibility(View.GONE);
        }

        if (codOrderConfirmed) {
            showConfirmationLayout();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if (!successResponce) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))) {
                                            cartItemModelList.get(finalX).getQtyIDs().clear();
                                        }
                                    }
                                });
                    }
                } else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (successResponce) {
            finish();
            return;
        }
        DBQueries.atHome = true;
        super.onBackPressed();
    }

    private void showConfirmationLayout() {
        successResponce = true;
        codOrderConfirmed = false;
        getQtyIDs = false;

        for (int x = 0; x < cartItemModelList.size() - 1; x++) {
            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());
            }

        }

        if (HomePageActivity.homeActivity != null) {
            HomePageActivity.homeActivity.finish();
            HomePageActivity.homeActivity = null;
        } else {
            HomePageActivity.resetHomePage = true;
        }
        if (ProductDetailsActivity.productDetailsActivity != null) {
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity = null;
        }
        //sent confirmation sms
        String SMS_API = "https://www.fast2sms.com/dev/bulk";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ///nothing
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //nothing 
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", "jRIqNDuL34PKexrWO1FZY8ftgwMkb7AVnims6hdaz52lG9BCEXz5PbhZ0E67iCmAYTSdloys28xva4KM");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("sender_id", "FSTSMS");
                body.put("language", "english");
                body.put("route", "qt");
                body.put("numbers", mobileNo);
                body.put("message", "23672");
                body.put("variables", "{#FF#}");
                body.put("variables_values", order_ID);
                return body;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(stringRequest);
        //sent confirmation sms


        if (fromCart) {
            loadingDialog.show();
            Map<String, Object> updateCartList = new HashMap<>();
            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();
            for (int x = 0; x < DBQueries.cartList.size(); x++) {
                /*if (!cartItemModelList.get(x).isInStock()) {
                    updateCartList.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                    cartListSize++;
                } else {*/
                indexList.add(x);
                //}
            }
            updateCartList.put("list_size", cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        /*for (int x = 0; x < indexList.size(); x++) {
                            DBQueries.cartList.remove(indexList.get(x).intValue());
                            DBQueries.cartItemModelList.remove(indexList.get(x).intValue());
                            DBQueries.cartItemModelList.remove(DBQueries.cartItemModelList.size() - 1);
                        }*/
                        DBQueries.cartList.clear();
                        DBQueries.cartItemModelList.clear();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(DeliveryActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });

        }
        continueBtn.setEnabled(false);
        changeOrAddAddressBtn.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderID.setText("Order ID: " + order_ID);
        orderConfirmationLayout.setVisibility(View.VISIBLE);

        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homePage = new Intent(DeliveryActivity.this, HomePageActivity.class);
                startActivity(homePage);
                finish();
            }
        });

    }

    private void placeOfflineOrder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, STORAGE_CODE);
            } else {
                isOfflineMode = true;
                if (paymentMethod.equals("PAYTM")) {
                    paytm();
                }
                //savePDF();
            }
        } else {
            if (paymentMethod.equals("PAYTM")) {
                paytm();
            }
            //savePDF();
        }
    }

    private void savePDF() {
        loadingDialog.show();
        //create object of Document Class
        String userID = FirebaseAuth.getInstance().getUid();
        Document mDoc = new Document();
        String dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss aa", Locale.getDefault()).format(System.currentTimeMillis());
        String mFileName = order_ID;
        String mFilePath = Environment.getExternalStorageDirectory() + "/" + mFileName + ".pdf";

        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));

            mDoc.open();

            mDoc.addAuthor("FastPay");

            mDoc.add(new Paragraph("Order ID: " + order_ID));
            mDoc.add(new Paragraph("User ID: " + userID));
            mDoc.add(new Paragraph("Time & Date: " + dateTime));
            mDoc.add(new Paragraph("\n"));
            mDoc.add(new Paragraph("Purchase Details:"));
            mDoc.add(new Paragraph("\n"));

            for (CartItemModel cartItemModel : cartItemModelList) {
                if (cartItemModel.getType() == CartItemModel.CART_ITEM) {

                    mDoc.add(new Paragraph("Product Name: " + cartItemModel.getProductTitle()));
                    mDoc.add(new Paragraph("Quantity: " + cartItemModel.getProductQuantity()));
                    mDoc.add(new Paragraph("Price: " + cartItemModel.getProductPrice()));
                    mDoc.add(new Paragraph("\n"));

                } else {
                    mDoc.add(new Paragraph("Total Items: " + cartItemModel.getTotalItems()));
                    mDoc.add(new Paragraph("Total Items Price: " + cartItemModel.getTotalItemPrice() + " Rs."));
                    if (!cartItemModel.getDeliveryPrice().equals("FREE")) {
                        mDoc.add(new Paragraph("Total Amount: " + (cartItemModel.getTotalAmount() - Integer.parseInt(cartItemModel.getDeliveryPrice())) + " Rs."));
                    } else {
                        mDoc.add(new Paragraph("Total Amount: " + cartItemModel.getTotalAmount() + " Rs."));
                    }
                    mDoc.add(new Paragraph("Payment Status: " + "Paid"));

                }
            }

            mDoc.close();
            Uri pdfUri;
            pdfUri = Uri.fromFile(new File(mFilePath));
            uploadFile(pdfUri);

            Toast.makeText(DeliveryActivity.this, "Bill is saved in internal storage", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(DeliveryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        loadingDialog.dismiss();
    }

    private void uploadFile(Uri pdfUri) {
        /*FirebaseStorage storage = FirebaseStorage.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        StorageReference storageReference = storage.getReference();
        storageReference.child("Offline Orders").child(FirebaseAuth.getInstance().getUid()).child(order_ID).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url  = taskSnapshot.getUploadSessionUri().toString();

                        *//*DatabaseReference reference = database.getReference();
        //progressDialog.show();

                        reference.child(order_ID).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(DeliveryActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(DeliveryActivity.this,"Not Done",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });*//*

                        DatabaseReference reference = database.getReference("uploads");
                        Upload upload = new Upload(order_ID, url);
                        reference.child(reference.push().getKey()).setValue(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(DeliveryActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(DeliveryActivity.this,"Not Done",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DeliveryActivity.this,"Not Done---- Failed",Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
                if(progressDialog.getProgress() == 100){
                    progressDialog.dismiss();
                }
            }
        });*/
        loadingDialog.show();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("uploads");
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference sRef = mStorageReference.child("Offline Orders/" + FirebaseAuth.getInstance().getUid().toString() + "/" + DBQueries.fullName + " - " +order_ID + ".pdf");
        /*sRef.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //progressBar.setVisibility(View.GONE);
                        //textViewStatus.setText("File Uploaded Successfully");

                        Upload upload = new Upload(order_ID, sRef.getDownloadUrl().toString());
                        reference.child(reference.push().getKey()).setValue(upload);
                        Toast.makeText(DeliveryActivity.this,"Upload Done", Toast.LENGTH_LONG);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //textViewStatus.setText((int) progress + "% Uploading...");
                    }
                });
*/
        //final StorageReference ref = storageRef.child("images/mountains.jpg");
        UploadTask uploadTask = sRef.putFile(pdfUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return sRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String url = task.getResult().toString();
                    Upload upload = new Upload(DBQueries.fullName + " - " +order_ID, url);
                    reference.child(reference.push().getKey()).setValue(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(DeliveryActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }else {
                                Toast.makeText(DeliveryActivity.this,"Not Done",Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        }
                    });

                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(DeliveryActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void placeOrderDetails() {
        loadingDialog.show();
        String userID = FirebaseAuth.getInstance().getUid();
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {

                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER ID", order_ID);
                orderDetails.put("Product ID", cartItemModel.getProductID());
                orderDetails.put("Product Image", cartItemModel.getProductImgae());
                orderDetails.put("Product Title", cartItemModel.getProductTitle());
                orderDetails.put("User ID", userID);
                orderDetails.put("Product Quantity", cartItemModel.getProductQuantity());
                orderDetails.put("Cutted Price", cartItemModel.getCuttedPrice());
                orderDetails.put("Product Price", cartItemModel.getProductPrice());
                orderDetails.put("Ordered Date", FieldValue.serverTimestamp());
                orderDetails.put("Packed Date", FieldValue.serverTimestamp());
                orderDetails.put("Shipped Date", FieldValue.serverTimestamp());
                orderDetails.put("Delivered Date", FieldValue.serverTimestamp());
                orderDetails.put("Cancelled Date", FieldValue.serverTimestamp());
                orderDetails.put("Order Status", "Ordered");
                orderDetails.put("Payment Method", paymentMethod);
                orderDetails.put("Address", fullAddress.getText());
                orderDetails.put("Full Name", fullName.getText());
                orderDetails.put("Pincode", pincode.getText());
                orderDetails.put("Delivery Price", cartItemModelList.get(cartItemModelList.size() - 1).getDeliveryPrice());
                orderDetails.put("Cancellation Request", false);


                firebaseFirestore.collection("ORDERS").document(order_ID).collection("OrderItems").document(cartItemModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items", cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price", cartItemModel.getTotalItemPrice());
                orderDetails.put("Delivery Price", cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount", cartItemModel.getTotalAmount());
                orderDetails.put("Saved Amount", cartItemModel.getSavedAmount());
                orderDetails.put("Payment Status", "not paid");
                orderDetails.put("Order Status", "Cancelled");

                firebaseFirestore.collection("ORDERS").document(order_ID)
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (paymentMethod.equals("PAYTM")) {
                                paytm();
                            } else {
                                cod();
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    }

    private void paytm() {
        getQtyIDs = false;
        if (ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DeliveryActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
        final String M_id = "dFWguV76520992165584";
        final String customer_id = FirebaseAuth.getInstance().getUid();

        String url = "https://fastpayproject.000webhostapp.com/paytm/generateChecksum.php";
        final String callBackURL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("CHECKSUMHASH")) {
                        String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");

                        PaytmPGService paytmPGService = PaytmPGService.getStagingService("");
                        HashMap<String, String> paramMap = new HashMap<String, String>();
                        paramMap.put("MID", M_id);
                        paramMap.put("ORDER_ID", order_ID);
                        paramMap.put("CUST_ID", customer_id);
                        paramMap.put("CHANNEL_ID", "WAP");
                        paramMap.put("TXN_AMOUNT", totalAmount.getText().toString().substring(4, totalAmount.getText().length() - 3));
                        paramMap.put("WEBSITE", "WEBSTAGING");
                        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                        paramMap.put("CALLBACK_URL", callBackURL);
                        paramMap.put("CHECKSUMHASH", CHECKSUMHASH);

                        PaytmOrder order = new PaytmOrder(paramMap);
                        paytmPGService.initialize(order, null);
                        paytmPGService.startPaymentTransaction(DeliveryActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionResponse(Bundle inResponse) {
                                Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();

                                if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
                                    Map<String, Object> updateStatus = new HashMap<>();
                                    updateStatus.put("Payment Status", "Paid");
                                    if (!isOfflineMode) {
                                        updateStatus.put("Order Status", "Ordered");
                                        firebaseFirestore.collection("ORDERS").document(order_ID).update(updateStatus)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Object> userOrders = new HashMap<>();
                                                            userOrders.put("order_id", order_ID);
                                                            userOrders.put("time", FieldValue.serverTimestamp());
                                                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_ID).set(userOrders)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                showConfirmationLayout();
                                                                            } else {
                                                                                Toast.makeText(DeliveryActivity.this, "Oops! failed to update user order list...", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            Toast.makeText(DeliveryActivity.this, "Order Cancelled", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        isOfflineMode = false;
                                        savePDF();
                                        Map<String, Object> userOrders = new HashMap<>();
                                        userOrders.put("order_id", order_ID);
                                        userOrders.put("time", FieldValue.serverTimestamp());
                                        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_ID).set(userOrders)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            showConfirmationLayout();
                                                        } else {
                                                            Toast.makeText(DeliveryActivity.this, "Oops! failed to update user order list...", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }

                                }
                            }

                            @Override
                            public void networkNotAvailable() {
                                Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(DeliveryActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("MID", M_id);
                paramMap.put("ORDER_ID", order_ID);
                paramMap.put("CUST_ID", customer_id);
                paramMap.put("CHANNEL_ID", "WAP");
                paramMap.put("TXN_AMOUNT", totalAmount.getText().toString().substring(4, totalAmount.getText().length() - 3));
                paramMap.put("WEBSITE", "WEBSTAGING");
                paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                paramMap.put("CALLBACK_URL", callBackURL);

                return paramMap;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void cod() {
        getQtyIDs = false;
        Intent otpIntent = new Intent(DeliveryActivity.this, OTPVerificationActivity.class);
        otpIntent.putExtra("mobileNo", mobileNo.trim().substring(0, 10));
        otpIntent.putExtra("OrderID", order_ID);
        startActivity(otpIntent);
    }
}
