package com.example.bookstory.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bookstory.R;
import com.example.bookstory.adapters.BooksAdapter;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Autor;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.CarteCuAutor;
import com.example.bookstory.models.Imprumut;
import com.example.bookstory.models.ImprumutCarte;
import com.example.bookstory.models.Utilizator;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class ListaCartiUserActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private FirebaseAuth auth;
    private LibraryDB db;
    ListView listView;
    Button btnFinalizeaza;
    private List<CarteCuAutor> carteCuAutorList = new ArrayList<>();
    List<Carte> carti = new ArrayList<>();
    List<Carte> cartiImprumutate = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_carti_user);
        listView = findViewById(R.id.lvCartiUser);
        btnFinalizeaza = findViewById(R.id.btnFinalizeazaImprumut);
        db = LibraryDB.getInstanta(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        carteCuAutorList = db.getCarteCuAutoriDao().getCarteCuAutori();
        listareCarti();
        registerForContextMenu(listView);
        btnFinalizeaza.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(ListaCartiUserActivity.this)
                    .setTitle(R.string.confirmare_finalizare)
                    .setMessage(R.string.mesaj_confirm_finalizare)
                    .setNegativeButton("Nu", (dialogInterface, which) -> {
                        cartiImprumutate.clear();
                        dialogInterface.cancel();
                    })
                    .setPositiveButton("Da", (dialogInterface, which) -> {
                        Utilizator utilizator = db.getUserDao().getUserByUid(auth.getCurrentUser().getUid());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.DATE, 14);
                        Imprumut imprumut = new Imprumut(utilizator.getId(), new Date(), calendar.getTime(), 0);
                        imprumut.setIdImprumut(db.getImprumutDao().insert(imprumut));
                        for (Carte c : cartiImprumutate) {
                            ImprumutCarte imprumutCarte = new ImprumutCarte(imprumut.getIdImprumut(), c.getIdCarte());
                            db.getImprumutCuCarteDao().insert(imprumutCarte);
                        }
                        if (!checkPermission()) {
                            requestPermission();
                        }
                        genereazaFisaImprumut(imprumut);
                        Toast.makeText(getApplicationContext(), R.string.imprumut_finalizat_toast, Toast.LENGTH_LONG).show();
                        dialogInterface.cancel();

                    }).create();
            dialog.show();
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_user, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        BooksAdapter adapter = (BooksAdapter) listView.getAdapter();
        switch (item.getItemId()) {
            case R.id.ctxImprumutaCarte:
                cartiImprumutate.add(adapter.getItem(info.position));
                break;
            case R.id.ctxRenunta:
                cartiImprumutate.remove(adapter.getItem(info.position));
                return true;
        }
        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                carteCuAutorList.clear();
                carti.clear();
                carteCuAutorList = db.getCarteCuAutoriDao().getCarteCuAutoriByName(searchView.getQuery().toString());
                listareCarti();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void listareCarti() {
        for (CarteCuAutor c : carteCuAutorList) {
            carti.add(c.carte);
        }
        BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista, carti, getLayoutInflater()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvAutor = view.findViewById(R.id.autor);
                StringBuilder stringBuilder;
                Uri uri = Uri.parse(carti.get(position).getCopertaURI());
                ImageView iv = view.findViewById(R.id.ivCoperta);
                iv.setImageURI(uri);

                stringBuilder = new StringBuilder();
                for (Autor a : carteCuAutorList.get(position).autori) {
                    stringBuilder.append(a.getNume());
                    if (carteCuAutorList.get(position).autori.indexOf(a) != (carteCuAutorList.get(position).autori.size() - 1)) {
                        stringBuilder.append(",");
                    }
                }
                tvAutor.setText(stringBuilder.toString());
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    private void genereazaFisaImprumut(Imprumut imprumut) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        Paint title = new Paint();
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        canvas.drawText("Fisa imprumut la data de " + imprumut.getDataImprumut().toString(), 209, 100, title);
        canvas.drawText("Data: " + imprumut.getDataImprumut().toString(), 100, 100, title);
        document.finishPage(page);
        try {
            String numePDF = "Imprumut" + imprumut.getDataImprumut().toString()+".pdf";
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}