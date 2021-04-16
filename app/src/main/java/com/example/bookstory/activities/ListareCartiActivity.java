package com.example.bookstory.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstory.R;
import com.example.bookstory.adapters.BooksAdapter;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Autor;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.CarteCuAutor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListareCartiActivity extends AppCompatActivity {

    ListView listView;
    private static final int REQUEST_CODE = 200;
    LibraryDB db;
    List<CarteCuAutor> carteCuAutorList = new ArrayList<>();
    List<Carte> carti = new ArrayList<>();
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listare_carti);
        listView = findViewById(R.id.listView);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        db = LibraryDB.getInstanta(getApplicationContext());
        carteCuAutorList = db.getCarteDao().getCarteCuAutori();
        for(CarteCuAutor c : carteCuAutorList){
           carti.add(c.carte);
        }
        registerForContextMenu(listView);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AdaugaCarteActivity.class);
            startActivityForResult(intent,REQUEST_CODE);
        });

        BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista,carti,getLayoutInflater()){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);
                TextView tvAutor = view.findViewById(R.id.autor);
                StringBuilder stringBuilder = new StringBuilder();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null){
            Carte book = (Carte) data.getSerializableExtra(AdaugaCarteActivity.ADD_BOOK);
            if(book != null) {
                carti.add(book);
                BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista,carti,getLayoutInflater()) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        return super.getView(position, convertView, parent);
                    }
                };
                listView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_admin, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ctxedit:

                break;

            case R.id.ctxdelete:
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            BooksAdapter adapter = (BooksAdapter) listView.getAdapter();
            AlertDialog dialog = new AlertDialog.Builder(ListareCartiActivity.this)
                        .setTitle("Confirmare stergere")
                        .setMessage("Doriti sa stergeti cartea?")
                        .setNegativeButton("No", (dialogInterface, which) -> dialogInterface.cancel()).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                db.getCarteDao().deleteBookById(adapter.getItem(info.position).getIdCarte());
                                db.getCartiDao().deleteBook(adapter.getItem(info.position));
                                adapter.remove(adapter.getItem(info.position));
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "Cartea a fost stearsa!",Toast.LENGTH_LONG).show();
                                dialogInterface.cancel();
                            }
                        }).create();
            dialog.show();
            return true;
        }
        return false;
    }
}