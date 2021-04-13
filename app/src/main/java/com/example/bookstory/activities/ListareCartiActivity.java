package com.example.bookstory.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
            if(book != null){
                carti.add(book);
                BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista,carti,getLayoutInflater()){
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
}