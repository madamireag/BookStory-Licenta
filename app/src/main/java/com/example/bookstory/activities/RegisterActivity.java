package com.example.bookstory.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Utilizator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    EditText etName;
    EditText etEmail;
    EditText etPassword;
    EditText etNrTelefon;
    EditText etAdresa;
    Button btnRegister;
    FirebaseAuth auth;
    ProgressBar progressBar;
    LibraryDB dbInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeUI();
        auth = FirebaseAuth.getInstance();
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        btnRegister.setOnClickListener(v -> registerNewUser());

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
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Utilizator utilizator = new Utilizator(etName.getText().toString(), etAdresa.getText().toString(),
                        String.valueOf(etNrTelefon.getText()), email, password, task.getResult().getUser().getUid());
                utilizator.setId(dbInstance.getUserDao().insert(utilizator));

                Toast.makeText(getApplicationContext(), R.string.reg_successfull, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), R.string.reg_failed, Toast.LENGTH_LONG).show();
              //  Log.i("EROARE",task.getResult().toString());
            }
            progressBar.setVisibility(View.GONE);
        }).addOnSuccessListener(authResult -> {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(etName.getText().toString()).build();
            authResult.getUser().updateProfile(profileUpdates);
        });

    }

    private void initializeUI() {
        etName = findViewById(R.id.etNume);
        etEmail = findViewById(R.id.etEmail);
        btnRegister = findViewById(R.id.btnRegisterDo);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBarRegister);
        etNrTelefon = findViewById(R.id.etTelefon);
        etAdresa = findViewById(R.id.etAdresa);
    }
}