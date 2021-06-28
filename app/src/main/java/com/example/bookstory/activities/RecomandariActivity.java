package com.example.bookstory.activities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.bookstory.models.Imprumut;
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
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.groupingBy;

public class RecomandariActivity extends AppCompatActivity {

    private LibraryDB dbInstance;
    private List<ImprumutCuCarte> listaImprumuturiCuCarti = new ArrayList<>();
    List<Carte> listaCartiDeAfisat = new ArrayList<>();
    private List<CarteCuAutor> listaCartiCuAutori = new ArrayList<>();
    private ListView listView;
    FirebaseAuth auth;
    List<CarteCuAutor> cartiDePastrat = new ArrayList<>();
    List<AutorCuCarte> autorCuCarti = new ArrayList<>();
    List<Imprumut> imprumuturi = null;
    Utilizator user = null;
    boolean areImprumuturiAnterioare = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomandari);
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        listView = findViewById(R.id.lvCartiRec);
        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null) {
            user = dbInstance.getUserDao().getUserByUid(auth.getCurrentUser().getUid());
        }

        if (user != null) {
            imprumuturi = dbInstance.getImprumutDao().getAllImprumuturiForUser(user.getId());
            if (imprumuturi.isEmpty()) {
                listaImprumuturiCuCarti = dbInstance.getImprumutCuCarteDao().getImprumuturicuCarti();
            } else {
                areImprumuturiAnterioare = true;
                listaImprumuturiCuCarti = dbInstance.getImprumutCuCarteDao().getImprumutcuCarti(user.getId());
            }
        }
        curataListe();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        recomandaCartiDupaGen();
        listareCarti();
    }

    private void curataListe() {
        listaCartiDeAfisat.clear();
        listaCartiCuAutori.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void recomandaCartiDupaGen() {
        Map<Gen, List<Carte>> cartiByGenre = new HashMap<>();
        Map<Gen, List<Carte>> cartiByGenreFinal = new HashMap<>();
        for (ImprumutCuCarte i : listaImprumuturiCuCarti) {
            cartiByGenre.putAll(i.listaCartiImprumut.stream()
                    .collect(groupingBy(Carte::getGenCarte)));
            cartiByGenre.forEach((k, v) -> {
                if (cartiByGenreFinal.containsKey(k)) {
                    List<Carte> cartiExistenteInMap = cartiByGenreFinal.get(k);
                    if (cartiExistenteInMap != null) {
                        cartiExistenteInMap.addAll(cartiByGenre.get(k));
                    }
                    cartiByGenreFinal.remove(k);
                    cartiByGenreFinal.put(k, cartiExistenteInMap);
                } else {
                    cartiByGenreFinal.put(k, cartiByGenre.get(k));
                }
            });
        }
        AtomicInteger max = new AtomicInteger();
        cartiByGenreFinal.forEach((k, v) -> {
            if (v.size() > max.get()) {
                max.set(v.size());
            }
            if (v.size() == Integer.parseInt(String.valueOf(max))) {
                listaCartiCuAutori = dbInstance.getCarteCuAutoriDao().getCartiByGenre(k);
            }

        });
    }

    private void verificaImprumutAnteriorCarti() {
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
            if (areImprumuturiAnterioare) {
                if (!found) {
                    listaCartiDeAfisat.add(ca.carte);
                    cartiDePastrat.add(ca);
                }
            } else {
                listaCartiDeAfisat.add(ca.carte);
                cartiDePastrat.add(ca);
            }
        }
    }

    public void listareCarti() {
        verificaImprumutAnteriorCarti();

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
                    for (Autor a : cartiDePastrat.get(position).autori) {
                        stringBuilder.append(a.getNume());
                        if (cartiDePastrat.get(position).autori.indexOf(a) != (cartiDePastrat.get(position).autori.size() - 1)) {
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

    private void preluareCartiImprumutate() {
        for (ImprumutCuCarte ic : listaImprumuturiCuCarti) {
            for (Carte c : ic.listaCartiImprumut) {
                listaCartiCuAutori.addAll(dbInstance.getCarteCuAutoriDao().getCarteCuAutoriById(c.getIdCarte()));
            }
        }
    }

    private void calculFrecventaAparitieAutori() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void selectareAutoriFavoriti() {
        curataListe();
        preluareCartiImprumutate();
        int contor = 0;
        Map<String, Integer> autoriCuFrecventaAparitiei = new HashMap<>();
        for (CarteCuAutor ca : listaCartiCuAutori) {
            for (Autor a : ca.autori) {
                if (!autoriCuFrecventaAparitiei.containsKey(a.getNume())) {
                    contor = 0;
                    contor++;
                } else {
                    contor++;
                    autoriCuFrecventaAparitiei.remove(a.getNume());
                }
                autoriCuFrecventaAparitiei.put(a.getNume(), contor);
            }
        }
        LinkedHashMap<String, Integer> autoriCuFrecventaAparitieiDescrescator = new LinkedHashMap<>();
        autoriCuFrecventaAparitiei.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> autoriCuFrecventaAparitieiDescrescator.put(x.getKey(), x.getValue()));

        LinkedHashMap<String, Integer> finalReverseSortedMap = new LinkedHashMap<>();
        AtomicInteger n = new AtomicInteger();
        autoriCuFrecventaAparitieiDescrescator.forEach((k, v) -> {
                    if (n.get() != 2) {
                        finalReverseSortedMap.put(k, v);
                    }
                    n.getAndIncrement();
                }
        );
        finalReverseSortedMap.forEach((k, v) -> autorCuCarti.addAll(dbInstance.getAutorCuCartiDao().getAutoriCuCartiByName(k)));
    }

    private void listareCartiDupaAutori() {
        listaCartiDeAfisat.clear();
        for (AutorCuCarte ac : autorCuCarti) {
            listaCartiDeAfisat.addAll(ac.carti);
        }

        BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista, listaCartiDeAfisat, getLayoutInflater()) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvAutor = view.findViewById(R.id.autor);
                StringBuilder stringBuilder = null;
                Uri uri = Uri.parse(listaCartiDeAfisat.get(position).getCopertaURI());
                ImageView iv = view.findViewById(R.id.ivCoperta);
                iv.setImageURI(uri);

                try {
                    int nrAutori = 0;
                    Map<StringBuilder, Integer> autoriCuIdCarte = new HashMap<>();
                    for (Carte c : listaCartiDeAfisat) {
                        stringBuilder = new StringBuilder();
                        for (AutorCuCarte ac : autorCuCarti) {
                            for (Carte carte : ac.carti) {
                                if (carte.getIdCarte() == c.getIdCarte()) {
                                    nrAutori++;
                                    stringBuilder.append(ac.Autor.getNume());
                                    if (nrAutori > 1) {
                                        stringBuilder.append(",");
                                    }
                                }
                            }
                        }
                        autoriCuIdCarte.put(stringBuilder, c.getIdCarte());
                    }
                    AtomicReference<StringBuilder> stringBuilder2 = new AtomicReference<>(new StringBuilder());
                    autoriCuIdCarte.forEach((k, v) -> {
                        if (listaCartiDeAfisat.get(position).getIdCarte() == v) {
                            stringBuilder2.set(k);
                        }
                    });


                    assert stringBuilder != null;
                    tvAutor.setText(stringBuilder2.toString());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_recomandari, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.optiuneAutori) {
            selectareAutoriFavoriti();
            listareCartiDupaAutori();
            return false;
        }
        return true;
    }
}