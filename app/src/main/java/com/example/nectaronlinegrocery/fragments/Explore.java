package com.example.nectaronlinegrocery.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.nectaronlinegrocery.CategoryItems;
import com.example.nectaronlinegrocery.R;
import com.example.nectaronlinegrocery.adapter.CategoryAdapter;
import com.example.nectaronlinegrocery.adapter.ExploreAdapter;
import com.example.nectaronlinegrocery.adapter.ItemAdapter;
import com.example.nectaronlinegrocery.model.Category;
import com.example.nectaronlinegrocery.model.Item;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Explore extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;

    private ExploreAdapter exploreAdapter;

    SearchView searchItems;

    ItemAdapter itemAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_explore, container, false);

        setCatRecyclerView(view);

        searchItems = view.findViewById(R.id.searchViewItems);

        searchItems.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setupSearchView(view, query);
                itemAdapter.startListening();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setupSearchView(view, newText);
                itemAdapter.startListening();
                return true;
            }
        });


        return view;
    }

    private void setCatRecyclerView(final View view) {

        collectionReference = db.collection("categories");

        Query query = collectionReference.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();

        exploreAdapter = new ExploreAdapter(options);

        RecyclerView catRecyclerView = view.findViewById(R.id.explore_recycler);
        catRecyclerView.setHasFixedSize(true);
        catRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        catRecyclerView.setAdapter(exploreAdapter);

        exploreAdapter.setOnItemClickListener(new ExploreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Category  category = documentSnapshot.toObject(Category.class);
                Integer id = category.getId();
                String name = category.getName();
                String image = category.getImage();

                Intent i = new Intent(view.getContext(), CategoryItems.class);
                i.putExtra("catId", id);
                i.putExtra("catName", name);
                i.putExtra("catImage", image);

                startActivity(i);
                Toast.makeText(view.getContext(), "id: " + id
                        + " name: " + name
                        , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        exploreAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        exploreAdapter.stopListening();
    }

    private void setupSearchView(View view, String value) {
        collectionReference = db.collection("items");

        Query query = collectionReference.orderBy("name", Query.Direction.ASCENDING)
                .startAt(value)
                .endAt(value + "\uf8ff");

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        itemAdapter = new ItemAdapter(options);

        RecyclerView searchRecyclerView = view.findViewById(R.id.explore_recycler);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        searchRecyclerView.setAdapter(itemAdapter);

    }
}