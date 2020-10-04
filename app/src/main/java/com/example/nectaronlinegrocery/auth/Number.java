package com.example.nectaronlinegrocery.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.nectaronlinegrocery.R;

public class Number extends AppCompatActivity {

    EditText mPhoneNumber;
    ImageView mNextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number);

        mPhoneNumber = findViewById(R.id.number_phone);
        mNextBtn = findViewById(R.id.number_next_btn);

        mPhoneNumber.setText(getIntent().getStringExtra("phoneNumber"));
        
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Number.this, Verification.class);
                i.putExtra("phoneNumber", mPhoneNumber.getText().toString());
                startActivity(i);
            }
        });

    }
}