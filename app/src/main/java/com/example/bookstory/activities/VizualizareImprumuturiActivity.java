package com.example.bookstory.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Date;
import java.util.List;

public class VizualizareImprumuturiActivity extends AppCompatActivity {

    ListView listView;
    List<Imprumut> imprumuturi = new ArrayList<>();
    List<ImprumutCuCarte> imprumutCuCarteList = new ArrayList<>();
    LibraryDB dbInstance;
    FirebaseAuth auth;
    Utilizator utilizator = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vizualizare_imprumuturi);
        listView = findViewById(R.id.lvImprumuturi);
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            utilizator = dbInstance.getUserDao().getUserByUid(auth.getCurrentUser().getUid());
        }
        if (utilizator != null) {
            imprumutCuCarteList = dbInstance.getImprumutCuCarteDao().getImprumutcuCartiByUserId(utilizator.getId());
        } else {
            imprumutCuCarteList = dbInstance.getImprumutCuCarteDao().getImprumuturicuCarti();
        }
        registerForContextMenu(listView);
        if (!imprumutCuCarteList.isEmpty()) {
            for (ImprumutCuCarte ic : imprumutCuCarteList) {
                imprumuturi.add(ic.imprumut);
            }

            ImprumutAdapter adapter = new ImprumutAdapter(getApplicationContext(), R.layout.element_imprumut, imprumuturi, getLayoutInflater()) {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    StringBuilder stringBuilder;
                    Imprumut imprumut = imprumuturi.get(position);
                    TextView tvCartiImprumut = view.findViewById(R.id.tvCartiImprumut);
                    TextView tvData = view.findViewById(R.id.tvDataScadenta);
                    stringBuilder = new StringBuilder();
                    for (Carte c : imprumutCuCarteList.get(position).listaCartiImprumut) {
                        stringBuilder.append(c.getTitlu());
                        if (imprumutCuCarteList.get(position).listaCartiImprumut.indexOf(c) != (imprumutCuCarteList.get(position).listaCartiImprumut.size() - 1)) {
                            stringBuilder.append(System.lineSeparator());
                        }
                    }
                    tvCartiImprumut.setText(stringBuilder.toString());
                    Date dataCurenta = new Date();
                    if (imprumut.getDataScadenta().after(dataCurenta)) {
                        tvData.setTextColor(Color.parseColor("#008000"));
                    }
                    return view;
                }
            };
            listView.setAdapter(adapter);
        }


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_imprumuturi, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ImprumutAdapter adapter = (ImprumutAdapter) listView.getAdapter();
        if (item.getItemId() == R.id.ctxAddTaxa) {
            LayoutInflater layoutInflater = getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.dialog_layout, null);
            AlertDialog alertDialog = new AlertDialog.Builder(VizualizareImprumuturiActivity.this).create();
            alertDialog.setTitle(getString(R.string.taxa));
            alertDialog.setCancelable(false);
            final EditText etTaxa = view.findViewById(R.id.etTaxa);

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                if(utilizator != null) {
                    Toast.makeText(getApplicationContext(), "Nu aveti permisiunea sa introduceti o taxa!", Toast.LENGTH_LONG).show();
                } else
                {
                    adapter.getItem(info.position).setTaxaIntarziere(Double.parseDouble(etTaxa.getText().toString()));
                    dbInstance.getImprumutDao().update(adapter.getItem(info.position));
                }

            });


            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> alertDialog.dismiss());


            alertDialog.setView(view);
            alertDialog.show();
            return true;
        }
        return false;

    }

}