package com.example.bookstory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Autor;

public class AddAuthorActivity extends AppCompatActivity {
     EditText etNumeAutor;
     Button btnAddAutor;
     LibraryDB dbInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_author);
        etNumeAutor = findViewById(R.id.etNumeAutor);
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        btnAddAutor.setOnClickListener(v -> {
            Autor autor = new Autor(etNumeAutor.getText().toString());
          //  autor.setIdAutor(dbInstance.getAutorDao().insert(autor));

        });
    }
}