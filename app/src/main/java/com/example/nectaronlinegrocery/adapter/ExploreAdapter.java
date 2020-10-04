package com.example.nectaronlinegrocery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nectaronlinegrocery.R;
import com.example.nectaronlinegrocery.model.Category;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ExploreAdapter extends FirestoreRecyclerAdapter<Category, ExploreAdapter.ExploreViewHolder> {

    private OnItemClickListener listener;


    public ExploreAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ExploreViewHolder holder, int position, @NonNull Category model) {
        holder.cat_name.setText(model.getName());
        holder.setCat_image(model.getImage());
    }

    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_card, parent, false);
        return new ExploreViewHolder(view);
    }

    class ExploreViewHolder extends RecyclerView.ViewHolder {
        TextView cat_name;
        ImageView cat_image;

        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);

            cat_name = itemView.findViewById(R.id.explore_cat_name);
            cat_image = itemView.findViewById(R.id.explore_cat_image);

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

        public void setCat_image(String image) {
            Picasso.get().load(image).into(cat_image);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
