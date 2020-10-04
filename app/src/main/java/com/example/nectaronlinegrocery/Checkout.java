package com.example.nectaronlinegrocery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nectaronlinegrocery.model.CartItems;
import com.example.nectaronlinegrocery.model.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class Checkout extends AppCompatActivity {

    public static final int STATIC_INTEGER_VALUE = 1;
    String addressDetails = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextView totalAmount;

    Button placeOrder;
    TextView selectAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Intent i = getIntent();
        double total = i.getDoubleExtra("totalAmount", 0.0);

        totalAmount = findViewById(R.id.checkout_total);
        totalAmount.setText(String.valueOf(total));

        placeOrder = findViewById(R.id.place_order_btn);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceOrder(v);
            }
        });

        selectAddress = findViewById(R.id.checkout_selected_address);
        selectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Checkout.this, ShowAddresses.class);
                startActivityForResult(i, STATIC_INTEGER_VALUE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (STATIC_INTEGER_VALUE): {
                if (resultCode == Activity.RESULT_OK) {
                    String selectedAdLabel = data.getStringExtra("label");
                    String selectedAdDetails = data.getStringExtra("details");
                    selectAddress.setText(selectedAdLabel);
                    addressDetails = selectedAdDetails;
                }
            }
        }
    }

    private void setPlaceOrder(final View v) {
        final CollectionReference cartRef = db.collection("users").document(user.getUid()).collection("userCart");
        cartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        CartItems cartItems = snapshot.toObject(CartItems.class);
                        int itemId = Integer.parseInt(snapshot.getId());
                        double Q = cartItems.getQuantity();
                        double itemTotal = cartItems.getTotalPrice();

                        final String i = String.valueOf(Timestamp.now().getSeconds());

                        HashMap<String, Object> orderMap = new HashMap<>();
                        orderMap.put("id", i);
                        orderMap.put("itemId", itemId);
                        orderMap.put("Q", Q);
                        orderMap.put("itemTotal", itemTotal);

                        DocumentReference orderRef = db.collection("users").document(user.getUid())
                                .collection("orders").document(i);

                        orderRef.collection("items").document(String.valueOf(itemId)).set(orderMap);

                        Double orderTotal = Double.parseDouble(totalAmount.getText().toString());

                        // check if the customer chooses the address or not
                        if (addressDetails == "") {
                            Toast.makeText(Checkout.this, "Please choose your delivery address", Toast.LENGTH_SHORT).show();
                            return;
                        } else  {

                            Order order = new Order(i, user.getUid(), addressDetails, "Not Shipped", orderTotal, Timestamp.now());

                            orderRef.set(order);

                            Toast.makeText(Checkout.this, snapshot.getId() + " Added.", Toast.LENGTH_SHORT).show();

                        }

                        //Empty the Cart
                        cartRef.document(snapshot.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Checkout.this, " Cart items deleted.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                    showSuccessDialog(v);

                    Intent showOrders = new Intent(v.getContext(), ShowOrders.class);
                    startActivity(showOrders);
                    finish();
                } else {
                    showFailedDialog(v);
                }
            }
        });
    }

    private void showSuccessDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.order_accepted_card, viewGroup, false);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showFailedDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.order_accepted_card, viewGroup, false);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}