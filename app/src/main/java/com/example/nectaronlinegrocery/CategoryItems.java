package com.example.nectaronlinegrocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nectaronlinegrocery.adapter.CategoryAdapter;
import com.example.nectaronlinegrocery.adapter.ItemAdapter;
import com.example.nectaronlinegrocery.model.Item;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CategoryItems extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;

    private ItemAdapter itemAdapter;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(getIntent().getStringExtra("catName"));
        setSupportActionBar(toolbar);

        setItemsRecyclerView();
    }

    private void setItemsRecyclerView() {

        Intent i = getIntent();
        Integer catId = i.getIntExtra("catId", 0);

        collectionReference = db.collection("items");

        Query query = collectionReference
                .orderBy("name", Query.Direction.ASCENDING)
                .whereEqualTo("category", catId);

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        itemAdapter = new ItemAdapter(options);

        RecyclerView offerRecyclerView = findViewById(R.id.category_items_recycler);
        offerRecyclerView.setHasFixedSize(true);
        offerRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        offerRecyclerView.setAdapter(itemAdapter);


        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Item item = documentSnapshot.toObject(Item.class);
                Integer id = item.getId();
                String name = item.getName();
                String desc = item.getDescription();
                String image = item.getImage();
                Double price = item.getPrice();

                String path = documentSnapshot.getReference().getPath();

                Intent i = new Intent(CategoryItems.this, ProductDetails.class);
                i.putExtra("itemName", name);
                i.putExtra("itemPrice", price);
                i.putExtra("itemDesc", desc);
                i.putExtra("itemImage", image);

                startActivity(i);
                Toast.makeText(CategoryItems.this, "Position: " + position + " ID: " + id
                        + " Name: " + name
                        + " Price: " + price, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        itemAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.filter_menu, menu);

        searchView = (SearchView)menu.findItem(R.id.ic_filter).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setSearchRecyclerView(query);
                itemAdapter.startListening();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setSearchRecyclerView(newText);
                itemAdapter.startListening();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_filter:
//                Intent i = new Intent(this, Search.class);
//                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSearchRecyclerView(String value) {

        Intent i = getIntent();
        Integer catId = i.getIntExtra("catId", 0);

        collectionReference = db.collection("items");

        Query query = collectionReference.orderBy("name", Query.Direction.ASCENDING)
                .whereEqualTo("category", catId)
                .startAt(value)
                .endAt(value + "\uf8ff");

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        itemAdapter = new ItemAdapter(options);

        RecyclerView searchRecyclerView = findViewById(R.id.category_items_recycler);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        searchRecyclerView.setAdapter(itemAdapter);

    }

}