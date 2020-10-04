package com.example.nectaronlinegrocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.util.TimeUnit;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nectaronlinegrocery.adapter.AddressAdapter;
import com.example.nectaronlinegrocery.model.Address;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.util.Util;

import java.util.Calendar;

public class ShowAddresses extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference addressesRef;

    AddressAdapter addressAdapter;

    ImageView btnAddAddress;

    //Bottom Sheet
    EditText cityName, addressDetails;
    Button saveAddress;

    BottomSheetBehavior behavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_addresses);

        setAddressesRecycler();

        btnAddAddress = findViewById(R.id.show_add_address);

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
            }
        });

        View bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheet();

        //Bottom Sheet Data
        final Spinner spinner = findViewById(R.id.sheet_spinner_label);
        cityName = findViewById(R.id.sheet_city_name);
        addressDetails = findViewById(R.id.sheet_address_details);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.address_label, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        saveAddress = findViewById(R.id.sheet_save_address);
        saveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = String.valueOf(Calendar.getInstance().getTimeInMillis());
                String label = spinner.getSelectedItem().toString();
                String city = cityName.getText().toString();
                String details = addressDetails.getText().toString();
                addressesRef = db.collection("users").document(user.getUid()).collection("addresses");

                addressesRef.add(new Address(id, label, city, details, Timestamp.now()));

                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Toast.makeText(ShowAddresses.this, "New Address Saved Successfully.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void bottomSheet() {

        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Toast.makeText(ShowAddresses.this, "STATE_HIDDEN", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        Toast.makeText(ShowAddresses.this, "STATE_EXPANDED", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING: {
                        Toast.makeText(ShowAddresses.this, "Dragging", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case BottomSheetBehavior.STATE_SETTLING: {
                        Toast.makeText(ShowAddresses.this, "STATE_SETTLING", Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    public void toggleBottomSheet() {
        if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void setAddressesRecycler() {

        addressesRef = db.collection("users").document(user.getUid()).collection("addresses");

        Query query = addressesRef.orderBy("timeAdded", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Address> options = new FirestoreRecyclerOptions.Builder<Address>()
                .setQuery(query, Address.class)
                .build();

        addressAdapter = new AddressAdapter(options);

        RecyclerView addressesRecycler = findViewById(R.id.show_addresses_recycler);
        addressesRecycler.setHasFixedSize(true);
        addressesRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        addressesRecycler.setAdapter(addressAdapter);

    }


    @Override
    protected void onStop() {
        super.onStop();
        addressAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (addressAdapter != null) {
            addressAdapter.startListening();
        }
    }
}