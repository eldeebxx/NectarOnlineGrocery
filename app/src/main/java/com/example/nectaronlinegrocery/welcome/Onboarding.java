package com.example.nectaronlinegrocery.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nectaronlinegrocery.R;
import com.example.nectaronlinegrocery.auth.SignIn;

public class Onboarding extends AppCompatActivity {

    Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        getStarted = findViewById(R.id.get_started_btn);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Onboarding.this, SignIn.class);
                startActivity(i);
                finish();
            }
        });

    }
}