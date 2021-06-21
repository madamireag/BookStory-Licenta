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
import com.example.bookstory.models.AutorCuCarte;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.CarteCuAutor;
import com.example.bookstory.models.Gen;
import com.example.bookstory.models.ImprumutCuCarte;
import com.example.bookstory.models.Utilizator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;

public class RecomandariActivity extends AppCompatActivity {
    private LibraryDB dbInstance;
    private List<ImprumutCuCarte> listaImprumuturiCuCarti = new ArrayList<>();
    List<Carte> listaCartiDeAfisat = new ArrayList<>();
    private List<CarteCuAutor> listaCartiCuAutori = new ArrayList<>();
    private ListView listView;
    FirebaseAuth auth;

    List<AutorCuCarte> autorCuCarti = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomandari);
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        listView = findViewById(R.id.lvCartiRec);
        auth = FirebaseAuth.getInstance();

        Utilizator user = null;
        if (auth.getCurrentUser() != null) {
            user = dbInstance.getUserDao().getUserByUid(auth.getCurrentUser().getUid());
        }
        if (user != null) {
            listaImprumuturiCuCarti = dbInstance.getImprumutCuCarteDao().getImprumutcuCarti(user.getId());
            if (listaImprumuturiCuCarti.isEmpty()) {
                listaImprumuturiCuCarti = dbInstance.getImprumutCuCarteDao().getImprumuturicuCarti();
            }
        }


        // getAutoriFavoriti();
        listaCartiDeAfisat.clear();
        listaCartiCuAutori.clear();
        recomandaCartiDupaGen();


    }

    @Override
    protected void onStart() {
        super.onStart();
        listareCarti();
    }

    public void verificaImprumutAnteriorCarte() {
        for (CarteCuAutor ca : listaCartiCuAutori) {
            for (ImprumutCuCarte ic : listaImprumuturiCuCarti) {
                if (!ic.listaCartiImprumut.contains(ca.carte)) {
                    listaCartiDeAfisat.add(ca.carte);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void recomandaCartiDupaGen() {
        Map<Gen, List<Carte>> cartiByGenre = new HashMap<>();
        for (ImprumutCuCarte i : listaImprumuturiCuCarti) {
            cartiByGenre.putAll(i.listaCartiImprumut.stream()
                    .collect(groupingBy(Carte::getGenCarte)));

        }
        AtomicInteger max = new AtomicInteger();
        cartiByGenre.forEach((k, v) -> {
            if (v.size() > max.get()) {
                max.set(v.size());
            }
            if (v.size() == Integer.parseInt(String.valueOf(max))) {
                listaCartiCuAutori = dbInstance.getCarteCuAutoriDao().getCartiByGenre(k);
            }

        });
    }

    public void listareCarti() {
        boolean found;
        for (CarteCuAutor ca : listaCartiCuAutori) {
            found = false;
            for (ImprumutCuCarte ic : listaImprumuturiCuCarti) {
                for (Carte c : ic.listaCartiImprumut) {
                    if (ca.carte.getIdCarte() == c.getIdCarte()) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                listaCartiDeAfisat.add(ca.carte);
            }
        }

        BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista, listaCartiDeAfisat, getLayoutInflater()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvAutor = view.findViewById(R.id.autor);
                StringBuilder stringBuilder;
                Uri uri = Uri.parse(listaCartiDeAfisat.get(position).getCopertaURI());
                ImageView iv = view.findViewById(R.id.ivCoperta);
                iv.setImageURI(uri);

                stringBuilder = new StringBuilder();
                try {
                    for (Autor a : listaCartiCuAutori.get(position).autori) {
                        stringBuilder.append(a.getNume());
                        if (listaCartiCuAutori.get(position).autori.indexOf(a) != (listaCartiCuAutori.get(position).autori.size() - 1)) {
                            stringBuilder.append(",");
                        }

                    }
                    tvAutor.setText(stringBuilder.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getAutoriFavoriti() {
        listaCartiCuAutori.clear();
        for (ImprumutCuCarte ic : listaImprumuturiCuCarti) {
            for (Carte c : ic.listaCartiImprumut) {
                listaCartiCuAutori.addAll(dbInstance.getCarteCuAutoriDao().getCarteCuAutoriById(c.getIdCarte()));
            }
        }
        int contor = 0;
        Map<Autor, Integer> autoriContor = new HashMap<>();
        for (CarteCuAutor ca : listaCartiCuAutori) {
            for (Autor a : ca.autori) {
                if (!autoriContor.containsKey(a)) {
                    contor = 0;
                    contor++;
                    autoriContor.put(a, contor);
                } else {
                    contor++;
                    autoriContor.remove(a);
                    autoriContor.put(a, contor);
                }

            }
        }

        //LinkedHashMap preserve the ordering of elements in which they are inserted
        LinkedHashMap<Autor, Integer> reverseSortedMap = new LinkedHashMap<>();

        //Use Comparator.reverseOrder() for reverse ordering
        autoriContor.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        reverseSortedMap.forEach((k, v) -> autorCuCarti = dbInstance.getAutorCuCartiDao().getAutoriCuCartiByName(k.getNume()));


        Log.i("GRUPARE-AUTORI", autorCuCarti.toString());
    }

    private void listareCartiDupaAutori() {
        listaCartiDeAfisat.clear();
        for (AutorCuCarte ac : autorCuCarti) {
            listaCartiDeAfisat.addAll(ac.carti);
        }

        BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista, listaCartiDeAfisat, getLayoutInflater()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvAutor = view.findViewById(R.id.autor);
                StringBuilder stringBuilder;
                Uri uri = Uri.parse(listaCartiDeAfisat.get(position).getCopertaURI());
                ImageView iv = view.findViewById(R.id.ivCoperta);
                iv.setImageURI(uri);

                stringBuilder = new StringBuilder();
                try {
                    for (Autor a : listaCartiCuAutori.get(position).autori) {
                        stringBuilder.append(a.getNume());
                        if (listaCartiCuAutori.get(position).autori.indexOf(a) != (listaCartiCuAutori.get(position).autori.size() - 1)) {
                            stringBuilder.append(",");
                        }

                    }
                    tvAutor.setText(stringBuilder.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return view;
            }
        };
        listView.setAdapter(adapter);
    }
}