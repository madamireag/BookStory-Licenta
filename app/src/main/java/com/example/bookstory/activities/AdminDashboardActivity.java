package com.example.bookstory.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    CardView cvProfileAdmin;
    CardView cvListBooks;
    CardView cvAddAuthor;
    CardView cvDeleteAllAuthors;
    CardView cvDeleteAllBooks;
    CardView cvSignOut;
    LibraryDB db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        initializeUI();
        auth = FirebaseAuth.getInstance();
        db = LibraryDB.getInstanta(getApplicationContext());

        cvProfileAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
        });
        cvListBooks.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ListareCartiActivity.class);
            startActivity(intent);
        });
        cvDeleteAllAuthors.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(AdminDashboardActivity.this)
                    .setTitle(R.string.confirmare_stergere)
                    .setMessage(R.string.mesaj_sterge_autori)
                    .setNegativeButton("Nu", (dialogInterface, which) -> dialogInterface.cancel())
                    .setPositiveButton("Da", (dialogInterface, which) -> {
                        db.getAutorDao().deleteAll();
                        Toast.makeText(getApplicationContext(), R.string.autori_stersi, Toast.LENGTH_LONG).show();
                        dialogInterface.cancel();
                    }).create();
            dialog.show();
        });
        cvDeleteAllBooks.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(AdminDashboardActivity.this)
                    .setTitle(R.string.confirmare_stergere)
                    .setMessage(R.string.mesaj_stergere_carti)
                    .setNegativeButton("Nu", (dialogInterface, which) -> dialogInterface.cancel())
                    .setPositiveButton("Da", (dialogInterface, which) -> {
                        db.getCartiDao().deleteAll();
                        Toast.makeText(getApplicationContext(), R.string.carti_sterse, Toast.LENGTH_LONG).show();
                        dialogInterface.cancel();
                    }).create();
            dialog.show();
        });
        cvSignOut.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeUI() {
        cvProfileAdmin = findViewById(R.id.cvProfilAdmin);
        cvListBooks = findViewById(R.id.cvListaCartiAdmin);
        cvAddAuthor = findViewById(R.id.cvAddAuthor);
        cvDeleteAllAuthors = findViewById(R.id.cvDeleteAllAuthors);
        cvDeleteAllBooks = findViewById(R.id.cvDeleteAllBooks);
        cvSignOut = findViewById(R.id.cvSignOut);

    }
}