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
import com.example.nectaronlinegrocery.model.Item;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class FavouriteAdapter extends FirestoreRecyclerAdapter<Item, FavouriteAdapter.FavouriteViewHolder> {

    FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();

    DocumentReference cartRef = FirebaseFirestore.getInstance().collection("users")
            .document(user.getUid());

    private OnItemClickListener listener;

    public FavouriteAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final FavouriteViewHolder holder, final int position, @NonNull final Item model) {

        holder.item_name.setText(model.getName());
        holder.setItem_image(model.getImage());
        holder.item_desc.setText(model.getDescription());
        holder.item_price.setText(String.valueOf(model.getPrice()));

        holder.item_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getSnapshots().getSnapshot(position).getReference().delete();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item  = getSnapshots().getSnapshot(position).toObject(Item.class);
                Integer id = item.getId();
                String name = item.getName();
                String desc =  item.getDescription();
                String image = item.getImage();
                Double price = item.getPrice();

                String path = getSnapshots().getSnapshot(position).getReference().getPath();

                Intent i = new Intent(v.getContext(), ProductDetails.class);
                i.putExtra("itemName", name);
                i.putExtra("itemPrice", price);
                i.putExtra("itemDesc", desc);
                i.putExtra("itemImage", image);

                v.getContext().startActivity(i);
                Toast.makeText(v.getContext(), "Position: " + position + " ID: " + id
                        + " Name: " + name
                        + " Price: " + price, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_card, parent, false);
        return new FavouriteViewHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class FavouriteViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image, item_details;
        TextView item_name, item_desc, item_price;

        public FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.fav_item_name);
            item_desc = itemView.findViewById(R.id.fav_item_desc);
            item_price = itemView.findViewById(R.id.fav_item_price);
            item_details = itemView.findViewById(R.id.fav_item_details);

            item_image = itemView.findViewById(R.id.fav_item_image);

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
