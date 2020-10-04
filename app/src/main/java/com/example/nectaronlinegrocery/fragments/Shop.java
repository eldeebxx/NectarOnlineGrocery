package com.example.nectaronlinegrocery.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.nectaronlinegrocery.CategoryItems;
import com.example.nectaronlinegrocery.MainActivity;
import com.example.nectaronlinegrocery.ProductDetails;
import com.example.nectaronlinegrocery.R;
import com.example.nectaronlinegrocery.adapter.CategoryAdapter;
import com.example.nectaronlinegrocery.adapter.ItemAdapter;
import com.example.nectaronlinegrocery.adapter.SliderAdapter;
import com.example.nectaronlinegrocery.model.Category;
import com.example.nectaronlinegrocery.model.Item;
import com.example.nectaronlinegrocery.model.SliderItem;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class Shop extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser user;
    SliderView sliderView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private ItemAdapter itemAdapter;
    private CategoryAdapter categoryAdapter;
    private SliderAdapter sliderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        setItemsRecyclerView(view);
        setCatRecyclerView(view);

        //set up slider view
        sliderView = view.findViewById(R.id.imageSlider);
        setupImageSlider(view);
        renewItems(view);
        addNewItem(view);
        removeLastItem(view);


        return view;
    }

    private void setItemsRecyclerView(final View view) {

        collectionReference = db.collection("items");

        Query query = collectionReference.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        itemAdapter = new ItemAdapter(options);

        RecyclerView offerRecyclerView = view.findViewById(R.id.offer_recycler);
        offerRecyclerView.setHasFixedSize(true);
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        offerRecyclerView.setAdapter(itemAdapter);

        RecyclerView BestSellRecyclerView = view.findViewById(R.id.best_sell_recycler);
        BestSellRecyclerView.setHasFixedSize(true);
        BestSellRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        BestSellRecyclerView.setAdapter(itemAdapter);

    }

    private void setCatRecyclerView(final View view) {

        collectionReference = db.collection("categories");

        Query query = collectionReference.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();

        categoryAdapter = new CategoryAdapter(options);

        RecyclerView catRecyclerView = view.findViewById(R.id.main_cat_recycler);
        catRecyclerView.setHasFixedSize(true);
        catRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        catRecyclerView.setAdapter(categoryAdapter);

        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Category category = documentSnapshot.toObject(Category.class);
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
        itemAdapter.startListening();
        categoryAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        itemAdapter.stopListening();
        categoryAdapter.stopListening();
    }

    public void setupImageSlider(final View v) {
        sliderAdapter = new SliderAdapter(v.getContext());
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();


        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                Toast.makeText(v.getContext(), "onIndicatorClicked: " + sliderView.getCurrentPagePosition(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void renewItems(View view) {
        List<SliderItem> sliderItemList = new ArrayList<>();
        //dummy data
        for (int i = 0; i < 2; i++) {
            SliderItem sliderItem = new SliderItem();
            sliderItem.setDescription("Slider Item " + i);
            if (i % 2 == 0) {
                sliderItem.setImageUrl("https://firebasestorage.googleapis.com/v0/b/nectaronlicery.appspot.com/o/banner1.png?alt=media&token=2e44be1a-deb4-4391-9f1a-96dc9714b2cf");
            } else {
                sliderItem.setImageUrl("https://firebasestorage.googleapis.com/v0/b/nectaronlicery.appspot.com/o/banner2.png?alt=media&token=824bd55b-242b-461e-a49e-0870a84c341e");
            }
            sliderItemList.add(sliderItem);
        }
        sliderAdapter.renewItems(sliderItemList);
    }

    public void removeLastItem(View view) {
        if (sliderAdapter.getCount() - 1 >= 0)
            sliderAdapter.deleteItem(sliderAdapter.getCount() - 1);
    }

    public void addNewItem(View view) {
        SliderItem sliderItem = new SliderItem();
        sliderItem.setDescription("Slider Item Added Manually");
        sliderItem.setImageUrl("https://firebasestorage.googleapis.com/v0/b/nectaronlicery.appspot.com/o/banner1.png?alt=media&token=2e44be1a-deb4-4391-9f1a-96dc9714b2cf");
        sliderAdapter.addItem(sliderItem);
    }
}