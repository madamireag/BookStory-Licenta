package com.example.bookstory.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bookstory.R;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    CardView cvProfile;
    CardView cvSignOut;
    CardView cvListaCarti;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initializeUI();
        auth = FirebaseAuth.getInstance();
        cvSignOut.setOnClickListener(v -> {
            if(auth.getCurrentUser() != null) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        cvProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
        });
        cvListaCarti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListaCartiUserActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initializeUI() {
        cvProfile = findViewById(R.id.cvProfile);
        cvSignOut = findViewById(R.id.cvLogout);
        cvListaCarti = findViewById(R.id.cvListaCarti);
    }
}