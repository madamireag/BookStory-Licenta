package com.example.bookstory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Autor;
import com.example.bookstory.models.AutorCarte;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.CarteCuAutor;
import com.example.bookstory.models.Gen;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                Autor autor = new Autor("Mark Manson");
                Carte carte = new Carte("Arta Subtila a disperarii","345-3h4g-45r", Gen.FICTION,3,"ceva random");
                LibraryDB db = LibraryDB.getInstanta(getApplicationContext());
                db.getAutorDao().deleteAll();
                db.getCartiDao().deleteAll();
                autor.setIdAutor(db.getAutorDao().insert(autor));
                carte.setId((int)db.getCartiDao().insert(carte));
                AutorCarte autorCarte = new AutorCarte(autor.getIdAutor(),carte.getIdCarte());
                db.getCarteDao().insert(autorCarte);
            }
        });
    }
}