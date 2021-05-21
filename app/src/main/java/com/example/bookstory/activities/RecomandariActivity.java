package com.example.bookstory.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.Gen;
import com.example.bookstory.models.ImprumutCuCarte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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


            }
        }
        Log.i("GRUPARE", cartiByGenre.toString());
        // parcurgere map + numarare carti per fiecare categorie
        cartiByGenre.forEach((k, v) -> {
            switch (k) {
                case CLASSIC:

                    break;
                case FANTASY:

                    break;
                case FICTION:

                    break;
                case ROMANCE:

                    break;
                case SCIENCE:

                    break;
                case BIOGRAPHY:

                    break;
            }
        });
        // gasesc categoria cu valoarea maxima


    }
}