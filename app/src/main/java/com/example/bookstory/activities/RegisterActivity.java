package com.example.bookstory.activities;

import android.os.Bundle;
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
    boolean isValidUser = true;


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
        verificaCredentiale(email, password);
        if (!isValidUser) {
            progressBar.setVisibility(View.GONE);
        }
        if (!isValidUser) {
            progressBar.setVisibility(View.GONE);
        }
        verificaDateUtilizator(etName.getText().toString(), etNrTelefon.getText().toString(), etAdresa.getText().toString());
        if (isValidUser) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                            Utilizator utilizator = new Utilizator(etName.getText().toString(), etAdresa.getText().toString(),
                                    String.valueOf(etNrTelefon.getText()), email, password, task.getResult().getUser().getUid());
                            utilizator.setId(dbInstance.getUserDao().insert(utilizator));

                            Toast.makeText(getApplicationContext(), R.string.reg_successfull, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.reg_failed, Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }).addOnSuccessListener(authResult -> {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(etName.getText().toString()).build();
                if (authResult.getUser() != null) {
                    authResult.getUser().updateProfile(profileUpdates);
                }
            });
        }
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

    private void verificaCredentiale(String email, String parola) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";

        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.mesaj_err_email_empty));
            isValidUser = false;
        } else {
            isValidUser = true;
        }
        if (!email.matches(emailRegex)) {
            etEmail.setError(getString(R.string.format_invalid_email));
            isValidUser = false;
        } else {
            isValidUser = true;
        }
        if (parola.isEmpty()) {
            etPassword.setError(getString(R.string.mesaj_err_parola_empty));
        }
        if (parola.length() < 6) {
            etPassword.setError(getString(R.string.invalid_password));
        }
    }

    private void verificaDateUtilizator(String nume, String telefon, String adresa) {
        if (nume.isEmpty()) {
            etName.setError(getString(R.string.err_nume_empty));
            isValidUser = false;
        }
        if (adresa.isEmpty()) {
            etAdresa.setError(getString(R.string.err_adresa_empty));
            isValidUser = false;
        }
        if (telefon.isEmpty()) {
            etNrTelefon.setError(getString(R.string.err_telefon_empty));
            isValidUser = false;
        }
        if (!android.util.Patterns.PHONE.matcher(telefon).matches()) {
            etNrTelefon.setError(getString(R.string.err_nr_telefon_invalid));
            isValidUser = false;
        }
    }
}