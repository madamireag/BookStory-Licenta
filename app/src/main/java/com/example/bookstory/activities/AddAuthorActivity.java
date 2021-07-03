package com.example.bookstory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Autor;

public class AddAuthorActivity extends AppCompatActivity {
    EditText etNumeAutor;
    Button btnAddAutor;
    LibraryDB dbInstance;
    Intent intent;
    public static final String ADD_AUTOR = "addAutor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_author);
        initializeUI();
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        intent = getIntent();
        btnAddAutor.setOnClickListener(v -> {
            if (etNumeAutor.getText().toString().isEmpty()) {
                etNumeAutor.setError(getString(R.string.err_autor_empty));
            } else {
                Autor autor = new Autor(etNumeAutor.getText().toString());
                autor.setIdAutor(dbInstance.getAutorDao().insert(autor));
                intent.putExtra(ADD_AUTOR, autor);
            }
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void initializeUI() {
        etNumeAutor = findViewById(R.id.etNumeAutor);
        btnAddAutor = findViewById(R.id.btnAddAutor);
    }
}