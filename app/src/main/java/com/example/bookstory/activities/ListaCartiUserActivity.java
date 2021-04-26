package com.example.bookstory.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListaCartiUserActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    LibraryDB db;
    Button btnFinalizeaza;
    List<CarteCuAutor> carteCuAutorList = new ArrayList<>();
    List<Carte> carti = new ArrayList<>();
    List<Carte> cartiImprumutate = new ArrayList<>();
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_carti_user);
        listView = findViewById(R.id.lvCartiUser);
        btnFinalizeaza = findViewById(R.id.btnFinalizeazaImprumut);
        searchView = findViewById(R.id.search);
        db = LibraryDB.getInstanta(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        carteCuAutorList = db.getCarteDao().getCarteCuAutori();

        listareCarti();
        registerForContextMenu(listView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                carteCuAutorList.clear();
                carti.clear();
                carteCuAutorList = db.getCarteDao().getCarteCuAutoriByName(searchView.getQuery().toString());
                listareCarti();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               return false;
            }
        });
        btnFinalizeaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(ListaCartiUserActivity.this)
                        .setTitle(R.string.confirmare_finalizare)
                        .setMessage(R.string.mesaj_confirm_finalizare)
                        .setNegativeButton("Nu", (dialogInterface, which) -> {
                            cartiImprumutate.clear();
                            dialogInterface.cancel();
                        })
                        .setPositiveButton("Da", (dialogInterface, which) -> {
                            // Cum fac rost de id-ul userului din room? :/// - adaug uid din firebase in room si caut dupa el?
                            Utilizator utilizator = db.getUserDao().getUserByUid(auth.getCurrentUser().getUid());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            calendar.add(Calendar.DATE,14);
                            Imprumut imprumut = new Imprumut(utilizator.getId(), new Date(),calendar.getTime(),0);
                            imprumut.setIdImprumut(db.getImprumutDao().insert(imprumut));
                            for(Carte c : cartiImprumutate) {
                                ImprumutCarte imprumutCarte = new ImprumutCarte(imprumut.getIdImprumut(),c.getIdCarte());
                                db.getImprumutCuCarteDao().insert(imprumutCarte);
                            }
                            // Parcurge lista carti imprumutate si creez obiecte imprumutcarte

                            Toast.makeText(getApplicationContext(), R.string.imprumut_finalizat_toast, Toast.LENGTH_LONG).show();
                            dialogInterface.cancel();
                        }).create();
                dialog.show();
            }
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
        if (item.getItemId() == R.id.ctxImprumutaCarte) {
            cartiImprumutate.add(adapter.getItem(info.position));
        }
        return true;
    }

    private void listareCarti() {
        for(CarteCuAutor c : carteCuAutorList){
            carti.add(c.carte);
        }
        BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista,carti,getLayoutInflater()){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);
                TextView tvAutor = view.findViewById(R.id.autor);
                StringBuilder stringBuilder = new StringBuilder();
                Uri uri = Uri.parse(carti.get(position).getCopertaURI());
                ImageView iv = view.findViewById(R.id.ivCoperta);
                iv.setImageURI(uri);

                for(Autor c : carteCuAutorList.get(position).autori){
                    stringBuilder.append(c.getNume());
                    stringBuilder.append(",");
                }
                tvAutor.setText(stringBuilder.toString());
                return view;
            }
        };
        listView.setAdapter(adapter);
    }
}