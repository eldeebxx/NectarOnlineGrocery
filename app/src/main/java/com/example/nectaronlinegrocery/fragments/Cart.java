package com.example.nectaronlinegrocery.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nectaronlinegrocery.Checkout;
import com.example.nectaronlinegrocery.R;
import com.example.nectaronlinegrocery.adapter.CartAdapter;
import com.example.nectaronlinegrocery.model.CartItems;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Cart extends Fragment {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextView cartTotal;
    Button checkoutBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference cartRef;
    private CartAdapter cartAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        setCartRecyclerView(view);

        cartTotal = view.findViewById(R.id.cart_total);
        getItemsTotal(cartTotal);

        checkoutBtn = view.findViewById(R.id.cart_to_checkout_btn);
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItemsTotal(cartTotal);

                Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        double totalAmount = Double.parseDouble(cartTotal.getText().toString());
                        if (totalAmount != 0.0) {
                            Intent i = new Intent(getActivity(), Checkout.class);
                            i.putExtra("totalAmount", totalAmount);
                            startActivity(i);
                            Toast.makeText(getActivity(), "Total Amount is " + cartTotal.getText(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to Checkout, Please check your cart. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 2000);


            }
        });

        return view;
    }

    private void setCartRecyclerView(final View view) {

        cartRef = db.collection("users").document(user.getUid()).collection("userCart");

        FirestoreRecyclerOptions<CartItems> options = new FirestoreRecyclerOptions.Builder<CartItems>()
                .setQuery(cartRef, CartItems.class)
                .build();

        cartAdapter = new CartAdapter(options);

        RecyclerView cartRecyclerView = view.findViewById(R.id.cart_recycler);
        cartRecyclerView.setHasFixedSize(true);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        cartRecyclerView.setAdapter(cartAdapter);

    }

    private void getItemsTotal(final TextView view) {

        cartRef = db.collection("users").document(user.getUid()).collection("userCart");

        cartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    double totalPrice = 0.0;
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                        CartItems cartItems = snapshot.toObject(CartItems.class);

                        totalPrice += cartItems.getTotalPrice();

                        view.setText(String.valueOf(totalPrice));

                    }
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (cartAdapter != null) {
            cartAdapter.startListening();
            getItemsTotal(cartTotal);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        cartAdapter.stopListening();
        getItemsTotal(cartTotal);
    }

}