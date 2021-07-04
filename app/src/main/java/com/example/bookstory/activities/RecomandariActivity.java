package com.example.bookstory.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bookstory.R;
import com.example.bookstory.adapters.BooksAdapter;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Autor;
import com.example.bookstory.models.AutorCuCarte;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.CarteCuAutor;
import com.example.bookstory.models.Gen;
import com.example.bookstory.models.Imprumut;
import com.example.bookstory.models.ImprumutCarte;
import com.example.bookstory.models.ImprumutCuCarte;
import com.example.bookstory.models.Utilizator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.util.stream.Collectors.groupingBy;

public class RecomandariActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 11;
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
    FloatingActionButton btnFinalizare;
    List<Carte> cartiImprumutate = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomandari);
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        listView = findViewById(R.id.lvCartiRec);
        auth = FirebaseAuth.getInstance();
        btnFinalizare = findViewById(R.id.btnFinalizeazaImprumutRec);
        registerForContextMenu(listView);

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

        btnFinalizare.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(RecomandariActivity.this)
                    .setTitle(R.string.confirmare_finalizare)
                    .setMessage(R.string.mesaj_confirm_finalizare)
                    .setNegativeButton("Nu", (dialogInterface, which) -> {
                        anuleazaImprumut();
                        dialogInterface.cancel();
                    })
                    .setPositiveButton("Da", (dialogInterface, which) -> {
                        if (auth.getCurrentUser() != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            calendar.add(Calendar.DATE, 14);
                            Date data = new Date();
                            Imprumut imprumut = new Imprumut(user.getId(), data, calendar.getTime(), 0);
                            imprumut.setIdImprumut(dbInstance.getImprumutDao().insert(imprumut));
                            for (Carte c : cartiImprumutate) {
                                ImprumutCarte imprumutCarte = new ImprumutCarte(imprumut.getIdImprumut(), c.getIdCarte());
                                dbInstance.getImprumutCuCarteDao().insert(imprumutCarte);
                            }
                            if (!checkPermission()) {
                                requestPermission();
                            }
                            ImprumutCuCarte ic = dbInstance.getImprumutCuCarteDao().getImprumutcuCartiByImprumutId(imprumut.getIdImprumut());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                genereazaFisaImprumut(imprumut, ic);
                            }
                            Toast.makeText(getApplicationContext(), R.string.imprumut_finalizat_toast, Toast.LENGTH_LONG).show();
                            dialogInterface.cancel();
                        }
                    }).create();
            dialog.show();
        });

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void genereazaFisaImprumut(Imprumut imprumut, ImprumutCuCarte ic) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint title = new Paint();
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(31);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextAlign(Paint.Align.CENTER);
        Paint paint = new Paint();
        paint.setTextSize(28);
        paint.setTextAlign(Paint.Align.LEFT);

        int y = 100;
        int x = 460;
        LocalDate localDate = imprumut.getDataImprumut().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        canvas.drawText("Fisa imprumut la data de " + day + "-" + month + "-" + year, x, y, title);
        x = 20;
        y += 100;
        String numeMembru = null;
        if (auth.getCurrentUser() != null) {
            numeMembru = auth.getCurrentUser().getDisplayName();
        }
        canvas.drawText("Nume membru: " + numeMembru, x, y, paint);
        y += 50;
        canvas.drawText("Au fost rezervate pentru imprumut urmatoarele carti:" + System.lineSeparator(), x, y, paint);
        for (Carte c : ic.listaCartiImprumut) {
            y += 50;
            canvas.drawText(c.getTitlu() + System.lineSeparator(), x, y, paint);
        }
        y += 50;
        LocalDate localDate2 = imprumut.getDataScadenta().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year2 = localDate2.getYear();
        int month2 = localDate2.getMonthValue();
        int day2 = localDate2.getDayOfMonth();
        canvas.drawText("Data returnarii este: " + day2 + "-" + month2 + "-" + year2, x, y, paint);
        String mesajTaxa = "In cazul depasirii termenului se va percepe o taxa stabilita in momentul predarii";
        canvas.drawText(mesajTaxa + System.lineSeparator(), x, y + 50, paint);
        document.finishPage(page);
        try {
            String numePDF = "Imprumut" + day + "-" + month + "-" + year + ".pdf";
            File f = new File(Environment.getExternalStorageDirectory(), numePDF);
            document.writeTo(new FileOutputStream(f));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        document.close();
    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_rec, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        BooksAdapter adapter = (BooksAdapter) listView.getAdapter();
        switch (item.getItemId()) {
            case R.id.ctxImprumutaCarteRec:
                cartiImprumutate.add(adapter.getItem(info.position));
                break;
            case R.id.ctxRenuntaRec:
                cartiImprumutate.remove(adapter.getItem(info.position));
                return true;
        }
        return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (writeStorage && readStorage) {
                    Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void anuleazaImprumut() {
        cartiImprumutate.clear();
    }
}