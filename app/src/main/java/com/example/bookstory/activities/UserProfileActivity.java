package com.example.bookstory.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstory.R;
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
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    CircleImageView circleImageView;

    public Uri profilePic = Uri.EMPTY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initializeUI();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            firebaseUser = auth.getCurrentUser();
            if (firebaseUser.getPhotoUrl() != null) {
                profilePic = Uri.parse(firebaseUser.getPhotoUrl().toString());
            }
            etDisplayName.setText(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "");
            etChangeEmail.setText(firebaseUser.getEmail());
            circleImageView.setImageURI(profilePic);
        }

        circleImageView.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE_PROFILE);
        });

        btnSaveChanges.setOnClickListener(v -> {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(etDisplayName.getText().toString())
                    .build();
            if (!etChangeEmail.getText().toString().isEmpty()) {
                firebaseUser.updateEmail(etChangeEmail.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Email updated", Toast.LENGTH_LONG).show();
                    }
                });
            }
            if (!etChangePassword.getText().toString().isEmpty()) {
                firebaseUser.updatePassword(etChangePassword.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Password updated", Toast.LENGTH_LONG).show();
                    }
                });
            }
            firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Profile picture updated", Toast.LENGTH_LONG).show();
                }
            });
        });

        btnDeleteAccount.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(UserProfileActivity.this)
                    .setTitle("Confirmare stergere")
                    .setMessage("Doriti sa stergeti contul?")
                    .setPositiveButton("Da", (dialog, which) -> firebaseUser.delete().addOnCompleteListener(task -> {
                        Toast.makeText(getApplicationContext(), "Cont sters!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    })).setNegativeButton("Nu", (dialog, which) -> dialog.cancel()).create();
            alertDialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == Activity.RESULT_OK) {
            profilePic = data.getData();

            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(profilePic)
                    .build();
            firebaseUser.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Profile pic updated!", Toast.LENGTH_LONG).show();
                            circleImageView.setImageURI(profilePic);
                            Log.i("PROFILE-PIC-GALLERY", profilePic.toString());
                        }
                    });
        }
    }

    private void initializeUI() {
        circleImageView = findViewById(R.id.imageView2);
        etDisplayName = findViewById(R.id.etDisplayName);
        etChangeEmail = findViewById(R.id.etChangeEmail);
        etChangePassword = findViewById(R.id.etChangePassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
    }
}