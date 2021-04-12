package com.example.bookstory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bookstory.R;


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
//                Autor autor = new Autor("Mark Manson");
//                Autor autor1 = new Autor("John Green");
//                Autor autor2 = new Autor("alt autor");
//
//                Carte carte = new Carte("Arta Subtila a disperarii","345-3h4g-45r", Gen.FICTION,3,"ceva random");
//                Carte carte1 = new Carte("Fulgi de iubire","444-3h45g-45r", Gen.FICTION,3,"ceva random");
//                LibraryDB db = LibraryDB.getInstanta(getApplicationContext());
//                db.getAutorDao().deleteAll();
//                db.getCartiDao().deleteAll();
//                db.getCarteDao().deleteAllFromJoinTable();
//                autor.setIdAutor(db.getAutorDao().insert(autor));
//                autor1.setIdAutor(db.getAutorDao().insert(autor1));
//                autor2.setIdAutor(db.getAutorDao().insert(autor2));
//
//                carte.setId((int)db.getCartiDao().insert(carte));
//                carte1.setId((int)db.getCartiDao().insert(carte1));
//                AutorCarte autorCarte = new AutorCarte(autor.getIdAutor(),carte.getIdCarte());
//                AutorCarte autorCarte1 = new AutorCarte(autor1.getIdAutor(),carte1.getIdCarte());
//                AutorCarte autorCarte2 = new AutorCarte(autor2.getIdAutor(),carte1.getIdCarte());
//                db.getCarteDao().insert(autorCarte);
//                db.getCarteDao().insert(autorCarte1);
//                db.getCarteDao().insert(autorCarte2);
            }
        });
    }
}