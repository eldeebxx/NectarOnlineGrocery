package com.example.nectaronlinegrocery.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nectaronlinegrocery.MainActivity;
import com.example.nectaronlinegrocery.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Verification extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    EditText verifyCode;
    ImageView nextBtn;
    TextView resendCode;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        verifyCode = findViewById(R.id.phone_verify_code);
        nextBtn = findViewById(R.id.verify_next_btn);
        resendCode = findViewById(R.id.verify_resend_code);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = verifyCode.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(Verification.this, "Please Enter your code!", Toast.LENGTH_SHORT).show();
                } else {
                    verifyPhoneNumberWithCode(mVerificationId, code);
                }
            }
        });

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                String phoneNumber = i.getStringExtra("phoneNumber");
                resendVerificationCode(phoneNumber, mResendToken);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Verification.this, "Failed to verify your phone !" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent i = getIntent();
        String phoneNumber = i.getStringExtra("phoneNumber");
        if (phoneNumber != null) {
            sendVerificationCode(phoneNumber);
        }
    }

    private void sendVerificationCode(String phoneNumber) {
        mAuth.useAppLanguage();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+20" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks

        );
    }


    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+20" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Verification.this, "You've signed in successfully with " + mAuth.getCurrentUser().getPhoneNumber() , Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Verification.this, MainActivity.class);
                            startActivity(i);

                        } else {
                            Toast.makeText(Verification.this, "Failed ! " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
}