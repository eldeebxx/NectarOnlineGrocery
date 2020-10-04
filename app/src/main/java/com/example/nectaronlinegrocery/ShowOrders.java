package com.example.nectaronlinegrocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nectaronlinegrocery.fragments.Account;
import com.example.nectaronlinegrocery.model.Order;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ShowOrders extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    FirestoreRecyclerAdapter<Order, ShowOrdersViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_orders);

        setUpRecyclerView();
    }


    private void setUpRecyclerView() {

        Query query = db.collection("users").document(user.getUid()).collection("orders")
                .orderBy("timePlaced", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Order, ShowOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ShowOrdersViewHolder holder, int position, @NonNull Order model) {
                holder.order_id.setText(model.getId());
                holder.order_address.setText(model.getAddressId());
                holder.order_time.setText(model.getTimePlaced().toDate().toString());
                holder.order_total.setText(String.valueOf(model.getTotalOrder()));
                holder.order_status.setText(model.getStatus());
            }

            @NonNull
            @Override
            public ShowOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card, parent, false);
                return new ShowOrdersViewHolder(view);
            }
        };

        RecyclerView ordersRecycler = findViewById(R.id.show_orders_recycler);
        ordersRecycler.setHasFixedSize(true);
        ordersRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        ordersRecycler.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    static class ShowOrdersViewHolder extends RecyclerView.ViewHolder {

        TextView order_id, order_address, order_total, order_time, order_status;

        public ShowOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            order_id = itemView.findViewById(R.id.address_card_id);
            order_address = itemView.findViewById(R.id.address_card_address);
            order_total = itemView.findViewById(R.id.order_card_total);
            order_time = itemView.findViewById(R.id.address_card_time);
            order_status = itemView.findViewById(R.id.address_card_label);

        }
    }
}