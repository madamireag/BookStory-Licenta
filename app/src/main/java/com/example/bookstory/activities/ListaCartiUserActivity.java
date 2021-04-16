package com.example.bookstory.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.bookstory.R;
import com.example.bookstory.adapters.BooksAdapter;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Autor;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.CarteCuAutor;

import java.util.ArrayList;
import java.util.List;

public class ListaCartiUserActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    LibraryDB db;
    List<CarteCuAutor> carteCuAutorList = new ArrayList<>();
    List<Carte> carti = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_carti_user);
        listView = findViewById(R.id.lvCartiUser);
        searchView = findViewById(R.id.search);
        db = LibraryDB.getInstanta(getApplicationContext());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               // Thread thread = new Thread(new Runnable() {
                 //   @Override
                  // public void run() {
                        carteCuAutorList = db.getCarteDao().getCarteCuAutoriByName(searchView.getQuery().toString());
                        for(CarteCuAutor c : carteCuAutorList){
                            carti.add(c.carte);
                        }

                //});
               // thread.start();


                BooksAdapter adapter = new BooksAdapter(getApplicationContext(), R.layout.element_carte_lista,carti,getLayoutInflater()){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view =  super.getView(position, convertView, parent);
                        TextView tvAutor = view.findViewById(R.id.autor);
                        StringBuilder stringBuilder = new StringBuilder();
                       // for(Autor c : carteCuAutorList.get(position).autori){
                       //     stringBuilder.append(c.getNume());
                         //   stringBuilder.append(",");
                       // }
                        tvAutor.setText(stringBuilder.toString());
                        return view;
                    }
                };
                listView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //carteCuAutorList = db.getCarteDao().getCarteCuAutoriByName(searchView.getQuery().toString());
                return true;
            }
        });
    }
}