package com.example.bookstory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bookstory.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    EditText etNume;
    EditText etEmail;
    EditText etPassword;
    Button btnRegister;
    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeUI();
        auth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if(password.length() < 6){
            Toast.makeText(getApplicationContext(), R.string.invalid_password, Toast.LENGTH_LONG).show();
        }
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.reg_successfull, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.reg_failed, Toast.LENGTH_LONG).show();
                    Log.i("EROARE",task.getResult().toString());
                    progressBar.setVisibility(View.GONE);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(etNume.getText().toString()).build();
                authResult.getUser().updateProfile(profileUpdates);
            }
        });

    }

    private void initializeUI(){
        etNume = findViewById(R.id.etNume);
        etEmail = findViewById(R.id.etEmail);
        btnRegister = findViewById(R.id.btnRegisterDo);
        etPassword = findViewById(R.id.etPassword);
        progressBar=findViewById(R.id.progressBarRegister);
    }
}