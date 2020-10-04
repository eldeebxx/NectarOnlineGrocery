package com.example.nectaronlinegrocery.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nectaronlinegrocery.ProductDetails;
import com.example.nectaronlinegrocery.R;
import com.example.nectaronlinegrocery.adapter.FavouriteAdapter;
import com.example.nectaronlinegrocery.model.Item;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Favourite extends Fragment {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference favRef = db.collection("users")
            .document(user.getUid()).collection("favourite");

    private FavouriteAdapter favouriteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        setFavRecyclerView(view);

        return view;
    }

    private void setFavRecyclerView (final View view) {

        Query query = favRef.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        favouriteAdapter = new FavouriteAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.favourite_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(favouriteAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        favouriteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        favouriteAdapter.stopListening();
    }
}