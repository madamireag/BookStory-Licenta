package com.example.bookstory.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bookstory.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class UserProfileActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE_PROFILE = 111;
    Button btnChangeProfilePic;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ImageView imageView;
    public Uri profilePic = Uri.EMPTY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        btnChangeProfilePic = findViewById(R.id.btnSchimbaPoza);
        auth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.imageView2);
        if (auth.getCurrentUser() != null) {
            firebaseUser = auth.getCurrentUser();
            profilePic = Uri.parse(firebaseUser.getPhotoUrl().toString());
            imageView.setImageURI(profilePic);
            Log.i("PROFILE-PIC",firebaseUser.getPhotoUrl().toString());
           
        }
        btnChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                gallery.addFlags(FLAG_GRANT_READ_URI_PERMISSION); gallery.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE_PROFILE);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == Activity.RESULT_OK){
            profilePic = data.getData();

            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(profilePic)
                    .build();
            firebaseUser.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Profile pic updated!",Toast.LENGTH_LONG).show();
                        imageView.setImageURI(profilePic);
                        Log.i("PROFILE-PIC-GALLERY",profilePic.toString());
                }
                }
            });
        }
    }
}