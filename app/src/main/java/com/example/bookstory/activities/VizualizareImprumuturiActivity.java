package com.example.bookstory.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstory.R;
import com.example.bookstory.adapters.ImprumutAdapter;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.Imprumut;
import com.example.bookstory.models.ImprumutCuCarte;
import com.example.bookstory.models.Utilizator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class VizualizareImprumuturiActivity extends AppCompatActivity {

    ListView listView;
    List<Imprumut> imprumuturi = new ArrayList<>();
    List<ImprumutCuCarte> imprumutCuCarteList = new ArrayList<>();
    LibraryDB dbInstance;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vizualizare_imprumuturi);
        listView = findViewById(R.id.lvImprumuturi);
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        Utilizator utilizator = null;
        if (auth.getCurrentUser() != null) {
            utilizator = dbInstance.getUserDao().getUserByUid(auth.getCurrentUser().getUid());
        }
        imprumutCuCarteList = dbInstance.getImprumutCuCarteDao().getImprumuturicuCarti();
        if (utilizator != null) {
            for (ImprumutCuCarte ic : imprumutCuCarteList) {
                if (ic.imprumut.getIdUtilizator() == utilizator.getId()) {
                    imprumuturi.add(ic.imprumut);
                }
            }
        }
        ImprumutAdapter adapter = new ImprumutAdapter(getApplicationContext(), R.layout.element_imprumut, imprumuturi, getLayoutInflater()) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                StringBuilder stringBuilder;
                TextView tvCartiImprumut = view.findViewById(R.id.tvCartiImprumut);
                stringBuilder = new StringBuilder();
                for (Carte c : imprumutCuCarteList.get(position).listaCartiImprumut) {
                    stringBuilder.append(c.getTitlu());
                    if (imprumutCuCarteList.get(position).listaCartiImprumut.indexOf(c) != (imprumutCuCarteList.get(position).listaCartiImprumut.size() - 1)) {
                        stringBuilder.append(System.lineSeparator());
                    }
                }
                tvCartiImprumut.setText(stringBuilder.toString());
                return view;
            }
        };
        listView.setAdapter(adapter);

    }
}