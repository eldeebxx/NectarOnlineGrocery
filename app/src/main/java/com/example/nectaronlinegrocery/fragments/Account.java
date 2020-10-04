package com.example.nectaronlinegrocery.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nectaronlinegrocery.R;
import com.example.nectaronlinegrocery.ShowAddresses;
import com.example.nectaronlinegrocery.ShowOrders;
import com.example.nectaronlinegrocery.model.Data;
import com.example.nectaronlinegrocery.welcome.Onboarding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Account extends Fragment {

    ImageView userImage;
    TextView username, userID;

    FirebaseUser user;

    Button signOut;
    ImageView editName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_account, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        showCxDetails(view);
        editName = view.findViewById(R.id.account_edit_name);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(v);
            }
        });


        // Show list
        setUpListView(view);

        final ListView listView = view.findViewById(R.id.account_list);
        signOut = view.findViewById(R.id.account_sign_out);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(v.getContext(), Onboarding.class));
                    getActivity().finish();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data data = (Data) listView.getItemAtPosition(position);
                switch (position) {
                    case 0: //Orders
                        Toast.makeText(view.getContext(), "You Clicked on " + data.getName(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), ShowOrders.class));
                        break;
                    case 1: //Details
                        Toast.makeText(view.getContext(), "You Clicked on" + data.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    case 2: // Addresses
                        Toast.makeText(view.getContext(), "You Clicked on  " + data.getName(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), ShowAddresses.class));
                        break;
                    case 3:
                        Toast.makeText(view.getContext(), "You Clicked on   " + data.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(view.getContext(), "You  Clicked on  " + data.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Toast.makeText(view.getContext(), "You Clicked  on " + data.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(view.getContext(), "You  Clicked on " + data.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        Toast.makeText(view.getContext(), "You Clicked on :" + data.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(view.getContext(), "No Item Clicked", Toast.LENGTH_SHORT).show();

                }
            }
        });

        return view;
    }

    private void showEditDialog(final View v) {
        final EditText userName = new EditText(v.getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.Theme_AppCompat_DayNight_Dialog)
                .setView(userName)
                .setTitle("Enter Your Name ..")
                .setIcon(R.drawable.ic_logo_colored)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String name = userName.getText().toString();
                            if (!name.isEmpty()) {
                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(v.getContext(), "Username Updated.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void setUpListView(final View view) {
        ListView listView = view.findViewById(R.id.account_list);

        ArrayList<Data> data = new ArrayList<>();

        data.add(new Data("Orders", R.drawable.ic_orders));
        data.add(new Data("My Details", R.drawable.ic_details));
        data.add(new Data("Delivery Addresses", R.drawable.ic_location));
        data.add(new Data("Payment Methods", R.drawable.ic_payments));
        data.add(new Data("Promo Card", R.drawable.ic_promo));
        data.add(new Data("Notifications", R.drawable.ic_bell_icon));
        data.add(new Data("help", R.drawable.ic_help));
        data.add(new Data("about", R.drawable.ic_about));

        ArrayAdapter<Data> dataAdapter = new DataAdapter(view.getContext(), data);

        listView.setAdapter(dataAdapter);

    }

    private void showCxDetails(View view) {

        user = FirebaseAuth.getInstance().getCurrentUser();

        userImage = view.findViewById(R.id.account_image);
        username = view.findViewById(R.id.account_username);
        userID = view.findViewById(R.id.account_id);

        if (!user.getDisplayName().isEmpty()) {
            username.setText(user.getDisplayName());
        } else {
            username.setText("Username..");
        }

        if (!user.getEmail().isEmpty()) {
            userID.setText(user.getEmail());
        } else {
            userID.setText(user.getPhoneNumber());
        }

        if (user.getPhotoUrl() != null) {
            Picasso.get().load(user.getPhotoUrl()).into(userImage);
        }
    }

    public static class DataAdapter extends ArrayAdapter<Data> {

        private Context context;
        private List<Data> dataList;

        public DataAdapter(Context context, ArrayList<Data> data) {
            super(context, 0, data);

            this.context = context;
            this.dataList = data;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Data data = dataList.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.account_card, parent, false);
            }

            ImageView icon = convertView.findViewById(R.id.account_icon);
            TextView name = convertView.findViewById(R.id.account_name);

            name.setText(data.name);
            icon.setImageResource(data.icon);

            return convertView;
        }
    }
}