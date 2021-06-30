package com.example.bookstory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.bookstory.R;
import com.example.bookstory.models.Imprumut;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class ImprumutAdapter extends ArrayAdapter<Imprumut> {
    private List<Imprumut> imprumuturi;
    private LayoutInflater layoutInflater;
    private Context context;
    private int resource;

    public ImprumutAdapter(Context context, int resource, List<Imprumut> imprumuturi, LayoutInflater layoutInflater) {
        super(context, resource, imprumuturi);
        this.layoutInflater = layoutInflater;
        this.imprumuturi = imprumuturi;
        this.context = context;
        this.resource = resource;
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = layoutInflater.inflate(resource, parent, false);
        Imprumut imprumut = imprumuturi.get(position);
        if (imprumut != null) {
            TextView tvDataImprumut = view.findViewById(R.id.tvDataImprumut);
            TextView tvDataScadenta = view.findViewById(R.id.tvDataScadenta);
            LocalDate localDateBorrow;
            if (imprumut.getDataImprumut() != null) {
                localDateBorrow = imprumut.getDataImprumut().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int yearBorrow = localDateBorrow.getYear();
                String monthBorrow = localDateBorrow.getMonth().toString();
                int dayBorrow = localDateBorrow.getDayOfMonth();
                tvDataImprumut.setText(String.format("Data imprumut: %d-%s-%d", dayBorrow, monthBorrow, yearBorrow));
            }
            if (imprumut.getDataScadenta() != null) {
                LocalDate localDateReturn = imprumut.getDataScadenta().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int yearReturn = localDateReturn.getYear();
                String monthReturn = localDateReturn.getMonth().toString();
                int dayReturn = localDateReturn.getDayOfMonth();
                tvDataScadenta.setText(String.format("Data returnare: %d-%s-%d", dayReturn, monthReturn, yearReturn));
            }
        }
        return view;
    }
}
