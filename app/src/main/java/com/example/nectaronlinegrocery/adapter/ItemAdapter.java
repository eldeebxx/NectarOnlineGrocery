package com.example.nectaronlinegrocery.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nectaronlinegrocery.ProductDetails;
import com.example.nectaronlinegrocery.R;
import com.example.nectaronlinegrocery.model.CartItems;
import com.example.nectaronlinegrocery.model.Item;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ItemAdapter extends FirestoreRecyclerAdapter<Item, ItemAdapter.ItemHolder> {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    FirebaseFirestore db  = FirebaseFirestore.getInstance();
    DocumentReference cartRef = db.collection("users").document(user.getUid());

    private OnItemClickListener listener;

    public ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ItemHolder holder, final int position, @NonNull final Item model) {
        holder.item_name.setText(model.getName());
        holder.item_description.setText(model.getDescription());
        holder.setItem_image(model.getImage());
        holder.item_price.setText(String.valueOf(model.getPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item  = getSnapshots().getSnapshot(position).toObject(Item.class);
                int id = item.getId();
                String name = item.getName();
                String desc =  item.getDescription();
                String image = item.getImage();
                Double price = item.getPrice();
                int category = item.getCategory();

                String path = getSnapshots().getSnapshot(position).getReference().getPath();

                Intent i = new Intent(v.getContext(), ProductDetails.class);
                i.putExtra("itemId", id);
                i.putExtra("itemName", name);
                i.putExtra("itemPrice", price);
                i.putExtra("itemDesc", desc);
                i.putExtra("itemImage", image);
                i.putExtra("itemCat", category);

                v.getContext().startActivity(i);
                Toast.makeText(v.getContext(),  "Position: " + position + " ID: " + id
                        + " Name: " + name
                        + " Price: " + price, Toast.LENGTH_SHORT).show();
            }
        });

        holder.item_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final int item_id = model.getId();
                final List<Item> itemDetails = new ArrayList<>();

                final DocumentReference reference = cartRef.collection("userCart").document(String.valueOf(item_id));
                reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                Toast.makeText(v.getContext(), "Item is already exists in your cart.", Toast.LENGTH_SHORT).show();
                            } else {

                                final Long id = Calendar.getInstance().getTimeInMillis();

                                String name = model.getName();
                                Double price = model.getPrice();
                                String desc = model.getDescription();
                                String image = model.getImage();

                                itemDetails.add(new Item(item_id, name, price, image, desc,model.getCategory()));

                                CartItems cartItems = new CartItems(itemDetails, 1, price);

                                cartRef.collection("userCart").document(String.valueOf(item_id)).set(cartItems);
//                              cartRef.collection("adminView").document(String.valueOf(item_id)).set(cart);

                                Toast.makeText(v.getContext(), "Item " + model.getName() + "  Added to the Cart & CLicked at " + position, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


            }
        });
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ItemHolder(view);
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView item_name, item_description, item_price;
        ImageView item_image, item_to_cart;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.item_name);
            item_description = itemView.findViewById(R.id.item_desc);
            item_price = itemView.findViewById(R.id.item_price);
            item_image = itemView.findViewById(R.id.item_image);
            item_to_cart = itemView.findViewById(R.id.item_to_cart);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }

        public void setItem_image(String image) {
            Picasso.get().load(image).into(item_image);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
