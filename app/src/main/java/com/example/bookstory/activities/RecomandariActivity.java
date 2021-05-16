package com.example.bookstory.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.Gen;
import com.example.bookstory.models.Imprumut;
import com.example.bookstory.models.ImprumutCuCarte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class RecomandariActivity extends AppCompatActivity {
    LibraryDB dbInstance;
    List<ImprumutCuCarte> imprumuturiCuCarti = new ArrayList<>();
    List<Carte> carti;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomandari);
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        imprumuturiCuCarti = dbInstance.getImprumutCuCarteDao().getImprumutcuCarti();
        Map<Gen, List<Carte>> cartiByGenre = new HashMap<>();
        for (ImprumutCuCarte i : imprumuturiCuCarti) {
            for (Carte c : i.listaCartiImprumut) {
                cartiByGenre = i.listaCartiImprumut.stream()
                        .collect(groupingBy(Carte::getGenCarte));

                Log.i("GRUPARE", cartiByGenre.toString());
            }
        }
        // parcurgere map + numarare carti per fiecare categorie

        // gasesc categoria cu valoarea maxima



    }
}