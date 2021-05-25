package com.example.bookstory.activities;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstory.R;
import com.example.bookstory.adapters.BooksAdapter;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Autor;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.CarteCuAutor;
import com.example.bookstory.models.Gen;
import com.example.bookstory.models.ImprumutCuCarte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.groupingBy;

public class RecomandariActivity extends AppCompatActivity {
    LibraryDB dbInstance;
    List<ImprumutCuCarte> imprumuturiCuCarti = new ArrayList<>();
    List<Carte> carti = new ArrayList<>();
    List<CarteCuAutor> carteCuAutorList = new ArrayList<>();
    ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomandari);
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        imprumuturiCuCarti = dbInstance.getImprumutCuCarteDao().getImprumutcuCarti();
        listView = findViewById(R.id.lvCartiRec);
        Map<Gen, List<Carte>> cartiByGenre = new HashMap<>();
        for (ImprumutCuCarte i : imprumuturiCuCarti) {
            for (Carte c : i.listaCartiImprumut) {
                cartiByGenre.putAll(i.listaCartiImprumut.stream()
                        .collect(groupingBy(Carte::getGenCarte)));
            }


        }
        Log.i("GRUPARE F", cartiByGenre.toString());

        AtomicInteger max = new AtomicInteger();
        cartiByGenre.forEach((k, v) -> {
            if (v.size() > max.get()) {
                max.set(v.size());
            }
            if (v.size() == Integer.parseInt(String.valueOf(max))) {
                Log.i("CATEG-MAX", k.name());
                carteCuAutorList = dbInstance.getCarteCuAutoriDao().getCartiByGenre(k);
            }

        });
        for(CarteCuAutor ca : carteCuAutorList){
          carti.add(ca.carte);
        }
        BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista, carti, getLayoutInflater()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvAutor = view.findViewById(R.id.autor);
                StringBuilder stringBuilder = null;
                Uri uri = Uri.parse(carti.get(position).getCopertaURI());
                ImageView iv = view.findViewById(R.id.ivCoperta);
                iv.setImageURI(uri);

                //for (CarteCuAutor c : carteCuAutorList) {

                stringBuilder = new StringBuilder();
                for (Autor a : carteCuAutorList.get(position).autori) {
                    stringBuilder.append(a.getNume());
                    stringBuilder.append(",");
                }

                tvAutor.setText(stringBuilder.toString());
                // }

                return view;
            }
        };
        listView.setAdapter(adapter);
       Log.i("CARTI",carti.toString());
    }
}