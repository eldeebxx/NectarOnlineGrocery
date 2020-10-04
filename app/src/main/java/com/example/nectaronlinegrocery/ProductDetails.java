package com.example.nectaronlinegrocery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nectaronlinegrocery.model.CartItems;
import com.example.nectaronlinegrocery.model.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductDetails extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference favRef = db.collection("users").document(user.getUid()).collection("favourite");
    CollectionReference cartRef = db.collection("users").document(user.getUid()).collection("userCart");

    ImageView itemImage, favIcon, increaseQ, decreaseQ;
    TextView itemName, itemPrice, itemDesc, itemQ;

    Button addToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        itemName = findViewById(R.id.details_item_name);
        itemPrice = findViewById(R.id.details_item_price);
        itemDesc = findViewById(R.id.details_item_description);
        itemImage = findViewById(R.id.details_item_image);
        favIcon = findViewById(R.id.details_fav_icon);

        increaseQ = findViewById(R.id.details_item_iQ);
        decreaseQ = findViewById(R.id.details_item_dQ);
        itemQ = findViewById(R.id.details_item_quantity);

        showItemDetails();

        addToCart = findViewById(R.id.details_item_add_to_cart);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        increaseQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Integer.parseInt(itemQ.getText().toString());
                i++;
                itemQ.setText(String.valueOf(i));
            }
        });

        decreaseQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Integer.parseInt(itemQ.getText().toString());
                if (i != 1) {
                    i--;
                }
                itemQ.setText(String.valueOf(i));
            }
        });

        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavourite();
                favIcon.setImageResource(R.drawable.ic_check);
            }
        });

    }

    private void showItemDetails() {
        Intent i = getIntent();

        String name = i.getStringExtra("itemName");
        String desc = i.getStringExtra("itemDesc");
        Double price = i.getDoubleExtra("itemPrice", 0);
        String image = i.getStringExtra("itemImage");

        itemName.setText(name);
        itemDesc.setText(desc);
        itemPrice.setText(String.valueOf(price));
        Picasso.get().load(image).into(itemImage);

    }

    private void addToFavourite() {
        Intent i = getIntent();

        int id = i.getIntExtra("itemId", 0);
        String name = i.getStringExtra("itemName");
        String desc = i.getStringExtra("itemDesc");
        Double price = i.getDoubleExtra("itemPrice", 0);
        String image = i.getStringExtra("itemImage");
        int category = i.getIntExtra("itemCat", 0);

        Item item = new Item(id, name, price, image, desc, category);

        favRef.document(String.valueOf(id)).set(item);

        Toast.makeText(this, "Item Added to favourite", Toast.LENGTH_SHORT).show();

    }

    private void addToCart() {
        Intent i = getIntent();

        int id = i.getIntExtra("itemId", 0);
        String name = i.getStringExtra("itemName");
        String desc = i.getStringExtra("itemDesc");
        double price = i.getDoubleExtra("itemPrice", 0);
        String image = i.getStringExtra("itemImage");
        int category = i.getIntExtra("itemCat", 0);

        Item item = new Item(id, name, price, image, desc, category);

        List<Item> itemDetails = new ArrayList<>();

        itemDetails.add(item);

        int Q = Integer.parseInt(itemQ.getText().toString());
        double totalItemPrice = Q * price;

        CartItems cartItem = new CartItems(itemDetails, Q, totalItemPrice);

        cartRef.document(String.valueOf(id)).set(cartItem);

        Toast.makeText(this, name + " Added to Your Cart with " + Q + " items" , Toast.LENGTH_SHORT).show();
    }
}