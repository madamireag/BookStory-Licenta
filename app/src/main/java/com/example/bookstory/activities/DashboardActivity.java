package com.example.bookstory.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.bookstory.R;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    CardView cvProfile;
    CardView cvSignOut;
    CardView cvListaCarti;
    CardView cvRecomandari;
    CardView cvImprumuturi;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initializeUI();
        auth = FirebaseAuth.getInstance();
        cvSignOut.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        cvProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
        });
        cvListaCarti.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ListaCartiUserActivity.class);
            startActivity(intent);
        });
        cvRecomandari.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RecomandariActivity.class);
            startActivity(intent);
        });
        cvImprumuturi.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), VizualizareImprumuturiActivity.class);
            startActivity(intent);
        });

    }

    private void initializeUI() {
        cvProfile = findViewById(R.id.cvProfile);
        cvSignOut = findViewById(R.id.cvLogout);
        cvListaCarti = findViewById(R.id.cvListaCarti);
        cvRecomandari = findViewById(R.id.cvRecomandari);
        cvImprumuturi = findViewById(R.id.cvImprumuturi);
    }
}