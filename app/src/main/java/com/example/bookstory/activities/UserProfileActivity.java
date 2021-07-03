package com.example.bookstory.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Utilizator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE_PROFILE = 111;
    Button btnSaveChanges;
    Button btnDeleteAccount;
    EditText etDisplayName;
    EditText etChangeEmail;
    EditText etChangePassword;
    EditText etChangeAdresa;
    EditText etChangePhone;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    CircleImageView circleImageView;
    Utilizator utilizator;
    public Uri profilePic = Uri.EMPTY;
    LibraryDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initializeUI();
        auth = FirebaseAuth.getInstance();
        db = LibraryDB.getInstanta(getApplicationContext());
        if (auth.getCurrentUser() != null) {
            firebaseUser = auth.getCurrentUser();
            utilizator = db.getUserDao().getUserByUid(firebaseUser.getUid());
            if (firebaseUser.getPhotoUrl() != null) {
                profilePic = Uri.parse(firebaseUser.getPhotoUrl().toString());
                etDisplayName.setText(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "");
                etChangeEmail.setText(firebaseUser.getEmail());
                etChangePhone.setText(utilizator.getNrTelefon());
                etChangeAdresa.setText(utilizator.getAdresa());
                circleImageView.setImageURI(profilePic);
            }
        }


        btnSaveChanges.setOnClickListener(v -> {
            if (!etChangeEmail.getText().toString().isEmpty()) {
                firebaseUser.updateEmail(etChangeEmail.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        utilizator.setEmail(etChangeEmail.getText().toString());
                    }
                });
            }
            if (!etChangePassword.getText().toString().isEmpty()) {
                firebaseUser.updatePassword(etChangePassword.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        utilizator.setPassword(etChangePassword.getText().toString());
                    }
                });
            }
            if (!etChangePhone.getText().toString().isEmpty()) {
                utilizator.setNrTelefon(etChangePhone.getText().toString());
            }
            if (!etChangeAdresa.getText().toString().isEmpty()) {
                utilizator.setAdresa(etChangeAdresa.getText().toString());
            }
            db.getUserDao().update(utilizator);
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(etDisplayName.getText().toString())
                    .build();
            firebaseUser.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            etDisplayName.setText(firebaseUser.getDisplayName());
                            utilizator.setNumeComplet(etDisplayName.getText().toString());
                        }
                    });
            Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_LONG).show();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(UserProfileActivity.this)
                    .setTitle("Confirmare stergere")
                    .setMessage("Doriti sa stergeti contul?")
                    .setPositiveButton("Da", (dialog, which) ->
                            stergeContUtilizator())
                    .setNegativeButton("Nu", (dialog, which) -> dialog.cancel()).create();
            alertDialog.show();
        });

        circleImageView.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE_PROFILE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                profilePic = data.getData();
            }
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(profilePic)
                    .build();
            firebaseUser.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            circleImageView.setImageURI(profilePic);
                        }
                    });
        }
    }

    private void stergeContUtilizator() {
        firebaseUser.delete().addOnCompleteListener(task -> {
            Toast.makeText(getApplicationContext(), "Cont sters!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    private void initializeUI() {
        circleImageView = findViewById(R.id.imageView2);
        etDisplayName = findViewById(R.id.etDisplayName);
        etChangeEmail = findViewById(R.id.etChangeEmail);
        etChangePassword = findViewById(R.id.etChangePassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        etChangeAdresa = findViewById(R.id.etChangeAddress);
        etChangePhone = findViewById(R.id.etChangeTelefon);
    }
}