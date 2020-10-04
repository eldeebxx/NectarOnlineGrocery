package com.example.nectaronlinegrocery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nectaronlinegrocery.R;
import com.example.nectaronlinegrocery.model.CartItems;
import com.example.nectaronlinegrocery.model.Item;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class CartAdapter extends FirestoreRecyclerAdapter<CartItems, CartAdapter.CartViewHolder> {

    Double total = 0.0;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    DocumentReference cartRef = FirebaseFirestore.getInstance().collection("users")
            .document(user.getUid());

    private OnItemClickListener listener;

    public CartAdapter(@NonNull FirestoreRecyclerOptions<CartItems> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final CartViewHolder holder, final int position, @NonNull final CartItems model) {

        final Item index = model.getItemDetails().get(0);

        final double itemsTotal = index.getPrice() * model.getQuantity();

        holder.item_name.setText(index.getName());
        holder.setItem_image(index.getImage());
        holder.item_desc.setText(index.getDescription());
        holder.item_price.setText(String.valueOf(itemsTotal));
        holder.item_quantity.setText(String.valueOf(model.getQuantity()));

        holder.item_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (position != -1) {
                    getSnapshots().getSnapshot(position).getReference().delete();
                }
            }
        });

        holder.item_addQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Q = Integer.parseInt(holder.item_quantity.getText().toString());
                Q += 1;
                holder.item_quantity.setText(String.valueOf(Q));
                model.setQuantity(Q);

//                cartRef.collection("adminView").document(String.valueOf(model.getItemId())).update("quantity", Q);
                cartRef.collection("userCart").document(String.valueOf(index.getId())).update("quantity", Q);
                cartRef.collection("userCart").document(String.valueOf(index.getId())).update("totalPrice", index.getPrice()*Q);

            }
        });
        holder.item_minusQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Q = Integer.parseInt(holder.item_quantity.getText().toString());

                if (Q != 1) {
                    Q -= 1;
                }
                holder.item_quantity.setText(String.valueOf(Q));
                model.setQuantity(Q);

//                cartRef.collection("adminView").document(String.valueOf(model.getItemId())).update("quantity", Q);
                cartRef.collection("userCart").document(String.valueOf(index.getId())).update("quantity", Q);
                cartRef.collection("userCart").document(String.valueOf(index.getId())).update("totalPrice", index.getPrice()*Q);

            }
        });
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card, parent, false);
        return new CartViewHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image, item_addQ, item_minusQ, item_del;
        TextView item_name, item_desc, item_quantity, item_price;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.fav_item_name);
            item_desc = itemView.findViewById(R.id.fav_item_desc);
            item_quantity = itemView.findViewById(R.id.cart_item_quantity);
            item_price = itemView.findViewById(R.id.fav_item_price);

            item_image = itemView.findViewById(R.id.fav_item_image);
            item_addQ = itemView.findViewById(R.id.cart_item_plus);
            item_minusQ = itemView.findViewById(R.id.cart_item_minus);
            item_del = itemView.findViewById(R.id.cart_item_delete);

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
}
