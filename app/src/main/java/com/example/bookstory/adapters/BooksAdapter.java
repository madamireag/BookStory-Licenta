package com.example.bookstory.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookstory.R;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.CarteCuAutor;

import java.util.List;
import java.util.Locale;

public class BooksAdapter extends ArrayAdapter<Carte> {

    private LayoutInflater layoutInflater;
    private Context context;
    private int resource;
    private List<Carte> booksList;
    public BooksAdapter(@NonNull Context context, int resource, List<Carte> booksList, LayoutInflater layoutInflater) {
        super(context, resource,booksList);
        this.context = context;
        this.resource = resource;
        this.layoutInflater = layoutInflater;
        this.booksList = booksList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = layoutInflater.inflate(resource, parent, false);
        Carte carte = booksList.get(position);
        if(carte!=null){
            TextView tvTitlu = view.findViewById(R.id.titlu);
            tvTitlu.setText(carte.getTitlu());
            TextView tvAutor = view.findViewById(R.id.autor);
            TextView tvGen = view.findViewById(R.id.gen);
            tvGen.setText(String.valueOf(carte.getGenCarte()));
            ImageView cover = view.findViewById(R.id.imageView);
            cover.setImageURI(Uri.parse(carte.getCopertaURI()));
        }
        return view;
    }

}
