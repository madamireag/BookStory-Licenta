package com.example.bookstory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstory.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    Button btnRegister;
    FirebaseAuth auth;
    RelativeLayout layout;
    boolean isValidEmail = true;
    boolean isValidPassword = true;
    public final String ADMIN_EMAIL = "admin@test.com";
    public final String ADMIN_PASS = "ComplicatedAdminPass1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeUI();
        auth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });

    }

    private void loginUser() {
        verificaCredentiale(etEmail.getText().toString(), etPassword.getText().toString());
        if (isValidEmail && isValidPassword) {
            auth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText()
                    .toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.login_successfull, Toast.LENGTH_LONG).show();
                    if (ADMIN_EMAIL.equals(etEmail.getText().toString()) && ADMIN_PASS.equals(etPassword.getText().toString())) {
                        Intent intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_LONG).show();
                    Snackbar.make(layout, task.getException().getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> Snackbar.make(layout, e.getMessage(), Snackbar.LENGTH_LONG).show());
        } else {
            Toast.makeText(getApplicationContext(), R.string.mesaj_date_conectare_invalide, Toast.LENGTH_LONG).show();
        }

    }

    private void initializeUI() {
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        layout = findViewById(R.id.layoutLogin);
    }

    private void verificaCredentiale(String email, String parola) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.mesaj_err_email_empty));
            isValidEmail = false;
        } else {
            isValidEmail = true;
        }
        if (!email.matches(emailRegex)) {
            etEmail.setError(getString(R.string.format_invalid_email));
            isValidEmail = false;
        } else {
            isValidEmail = true;
        }
        if (parola.isEmpty()) {
            etPassword.setError(getString(R.string.mesaj_err_parola_empty));
            isValidPassword = false;
        } else {
            isValidPassword = true;
        }
    }
}