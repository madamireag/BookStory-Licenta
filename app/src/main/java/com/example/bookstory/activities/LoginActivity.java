package com.example.bookstory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.OnConflictStrategy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.CarteCuAutor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    Button btnRegister;
    FirebaseAuth auth;
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeUI();
        auth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
//        LibraryDB db = LibraryDB.getInstanta(getApplicationContext());
//        List<CarteCuAutor> carteCuAutors = db.getCarteDao().getCarteCuAutori();

//        Log.i("TESTCARTE",carteCuAutors.get(0).carte.toString());
//        Log.i("TESTAUTORICARTE",carteCuAutors.get(0).autori.get(0).toString());
    }

    private void loginUser() {
        auth.signInWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText()
                .toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.login_successfull, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                    startActivity(intent);
                } else {
                    Log.i("EROARE-LOGIN",task.getException().toString());
                    Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_LONG).show();
                    Snackbar.make(layout, task.getException().getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(layout, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void initializeUI() {
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        layout = findViewById(R.id.layoutLogin);
    }
}