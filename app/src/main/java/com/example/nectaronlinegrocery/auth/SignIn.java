package com.example.nectaronlinegrocery.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nectaronlinegrocery.MainActivity;
import com.example.nectaronlinegrocery.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignIn extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    FirebaseAuth mAuth;
    FirebaseUser user;
    EditText phoneNumber;
    Button googleBtn, signinWithEmail;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        phoneNumber = findViewById(R.id.signin_phone);
        googleBtn = findViewById(R.id.signin_google_btn);
        signinWithEmail = findViewById(R.id.signin_with_email);

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        phoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == event.ACTION_DOWN) && (keyCode == event.KEYCODE_ENTER)) {
                    String phone_number = phoneNumber.getText().toString();

                    if (TextUtils.isEmpty(phone_number) || phone_number.length() != 10) {
                        Toast.makeText(SignIn.this, "Please enter your correct phone number.", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {

                        Intent i = new Intent(SignIn.this, Number.class);
                        i.putExtra("phoneNumber", phoneNumber.getText().toString());
                        startActivity(i);
                        return true;
                    }

                }
                return false;
            }
        });

        signinWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                user = mAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(SignIn.this, "Signed in Successfully with your google account. Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    Intent googleIntent = new Intent(SignIn.this, MainActivity.class);
                    startActivity(googleIntent);
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignIn.this, "Sign in Failed ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                user = mAuth.getCurrentUser();

                if (user != null) {
                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                            .setDisplayName(account.getDisplayName())
                            .setPhotoUri(account.getPhotoUrl())
                            .build();

                    user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignIn.this, "Your details has been updated Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed.
                Toast.makeText(this, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }
}