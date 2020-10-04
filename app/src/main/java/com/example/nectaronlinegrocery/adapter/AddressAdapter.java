package com.example.nectaronlinegrocery.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nectaronlinegrocery.R;
import com.example.nectaronlinegrocery.model.Address;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Source;

public class AddressAdapter extends FirestoreRecyclerAdapter<Address, AddressAdapter.AddressViewHolder> {

    public AddressAdapter(@NonNull FirestoreRecyclerOptions<Address> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final AddressViewHolder holder, final int position, @NonNull final Address model) {
        holder.ad_id.setText(model.getId());
        holder.ad_city.setText(model.getCity());
        holder.ad_details.setText(model.getDetails());
        holder.ad_time.setText(model.getTimeAdded().toDate().toString());
        holder.ad_label.setText(model.getLabel());
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = model.getLabel();
                String details = model.getCity() + " , "
                        + model.getDetails();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("details", details);
                resultIntent.putExtra("label", label);

                ((Activity) v.getContext()).setResult(Activity.RESULT_OK, resultIntent);
                ((Activity) v.getContext()).finish();

            }
        });

    }
    
    

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_card, parent, false);
        return new AddressViewHolder(view);
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView ad_id, ad_city, ad_details, ad_time, ad_label;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            ad_id = itemView.findViewById(R.id.address_card_id);
            ad_city = itemView.findViewById(R.id.address_card_city);
            ad_details = itemView.findViewById(R.id.address_card_address);
            ad_time = itemView.findViewById(R.id.address_card_time);
            ad_label = itemView.findViewById(R.id.address_card_label);

        }
    }
}
