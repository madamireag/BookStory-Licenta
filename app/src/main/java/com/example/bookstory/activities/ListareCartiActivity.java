package com.example.bookstory.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstory.R;
import com.example.bookstory.adapters.BooksAdapter;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Autor;
import com.example.bookstory.models.AutorCarte;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.CarteCuAutor;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ListareCartiActivity extends AppCompatActivity {

    ListView listView;
    private static final int REQUEST_CODE = 200;
    public static final int REQUEST_CODE_EDIT_BOOK = 300;
    private static final int REQUEST_CODE_ADD_AUTOR = 210;
    public static final String EDIT_BOOK = "editBook";
    LibraryDB db;
    public int poz;
    List<CarteCuAutor> carteCuAutorList = new ArrayList<>();
    List<Carte> carti = new ArrayList<>();
    List<Autor> autori = new ArrayList<>();
    List<Autor> autoriCarte = new ArrayList<>();
    FloatingActionButton floatingActionButton;
    String[] authorNames;
    long[] authorIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listare_carti);
        listView = findViewById(R.id.listView);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        db = LibraryDB.getInstanta(getApplicationContext());
        carteCuAutorList = db.getCarteCuAutoriDao().getCarteCuAutori();
        populeazaListaCarti();
        registerForContextMenu(listView);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AdaugaCarteActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

        updateUI();
        autori = db.getAutorDao().getAll();
        updateListaAutori();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Carte book = (Carte) data.getSerializableExtra(AdaugaCarteActivity.ADD_BOOK);
            if (book != null) {
                carti.add(book);
                BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista, carti, getLayoutInflater()) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        return super.getView(position, convertView, parent);
                    }
                };
                listView.setAdapter(adapter);
            }
        } else if (requestCode == REQUEST_CODE_EDIT_BOOK && resultCode == RESULT_OK && data != null) {
            Carte book = (Carte) data.getSerializableExtra(AdaugaCarteActivity.ADD_BOOK);
            {
                if (book != null) {
                    carteCuAutorList.get(poz).carte.setISBN(book.getISBN());
                    carteCuAutorList.get(poz).carte.setTitlu(book.getTitlu());
                    carteCuAutorList.get(poz).carte.setGenCarte(book.getGenCarte());
                    carteCuAutorList.get(poz).carte.setNrCopiiDisponibile(book.getNrCopiiDisponibile());
                    carteCuAutorList.get(poz).carte.setCopertaURI(book.getCopertaURI());
                    populeazaListaCarti();
                    BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista, carti, getLayoutInflater()) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView tvAutor = view.findViewById(R.id.autor);
                            StringBuilder stringBuilder = new StringBuilder();
                            for (CarteCuAutor c : carteCuAutorList) {
                                for (Autor a : c.autori) {
                                    stringBuilder.append(a.getNume());
                                    stringBuilder.append(",");
                                }
                            }
                            tvAutor.setText(stringBuilder.toString());
                            return view;
                        }
                    };
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == REQUEST_CODE_ADD_AUTOR && resultCode == RESULT_OK && data != null) {
            Autor autor = (Autor) data.getSerializableExtra(AddAuthorActivity.ADD_AUTOR);
            autori.add(autor);
            updateListaAutori();
            populeazaListaCarti();
        }
    }

    private void populeazaListaCarti() {
        for (CarteCuAutor c : carteCuAutorList) {
            carti.add(c.carte);
        }
        for (CarteCuAutor c : carteCuAutorList) {
            autoriCarte = c.autori;
        }
    }

    private void updateListaAutori() {
        authorNames = new String[autori.size()];
        int i = 0;
        authorIds = new long[autori.size()];
        for (Autor a : autori) {
            authorNames[i] = autori.get(i).getNume();
            authorIds[i] = autori.get(i).getIdAutor();
            i++;
        }
    }

    private void updateUI() {
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
                try {
                    for (Autor a : carteCuAutorList.get(position).autori) {
                        stringBuilder.append(a.getNume());
                        if (carteCuAutorList.get(position).autori.indexOf(a) != (carteCuAutorList.get(position).autori.size() - 1)) {
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_admin, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        BooksAdapter adapter = (BooksAdapter) listView.getAdapter();
        boolean[] checkedAuthors = new boolean[authorNames.length];
        switch (item.getItemId()) {

            case R.id.ctxaddautor:
                poz = info.position;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Autori disponibili");
                builder.setMultiChoiceItems(authorNames, checkedAuthors, (dialog, which, isChecked) -> checkedAuthors[which] = isChecked);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    Toast.makeText(getApplicationContext(), "OK button was pressed", Toast.LENGTH_LONG).show();
                    for (int i = 0; i < checkedAuthors.length; i++) {
                        boolean checked = checkedAuthors[i];
                        if (checked) {
                            AutorCarte ac = new AutorCarte(authorIds[i], adapter.getItem(info.position).getIdCarte());
                            Log.i("AUTOR-ID", String.valueOf(authorIds[i]));
                            db.getCarteCuAutoriDao().insert(ac);
                            updateUI();
                        }
                    }
                });
                builder.setNeutralButton("Adauga autor", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), AddAuthorActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ADD_AUTOR);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(getApplicationContext(), "Cancel button was pressed", Toast.LENGTH_LONG).show());
                AlertDialog newDialog = builder.create();
                newDialog.show();
                break;
            case R.id.ctxedit:
                AdaugaCarteActivity.isUpdate = true;
                poz = info.position;
                Intent intent = new Intent(getApplicationContext(), AdaugaCarteActivity.class);
                intent.putExtra(EDIT_BOOK, adapter.getItem(info.position));
                startActivityForResult(intent, REQUEST_CODE_EDIT_BOOK);
                break;

            case R.id.ctxdelete:
                AlertDialog dialog = new AlertDialog.Builder(ListareCartiActivity.this)
                        .setTitle(R.string.confirmare_stergere)
                        .setMessage(R.string.mesaj_stergere)
                        .setNegativeButton("No", (dialogInterface, which) -> dialogInterface.cancel())
                        .setPositiveButton("Yes", (dialogInterface, which) -> {
                            db.getCarteCuAutoriDao().deleteBookById(adapter.getItem(info.position).getIdCarte());
                            db.getCartiDao().deleteBook(adapter.getItem(info.position));
                            adapter.remove(adapter.getItem(info.position));
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), R.string.carte_stearsa, Toast.LENGTH_LONG).show();
                            dialogInterface.cancel();
                        }).create();
                dialog.show();
                return true;
        }
        return false;
    }


}