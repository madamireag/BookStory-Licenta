package com.example.bookstory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookstory.R;
import com.example.bookstory.models.Imprumut;

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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = layoutInflater.inflate(resource, parent, false);
        Imprumut imprumut = imprumuturi.get(position);
        if (imprumut != null) {
            TextView tvDataImprumut = view.findViewById(R.id.tvDataImprumut);
            tvDataImprumut.setText(String.valueOf(imprumut.getDataImprumut()));
            TextView tvDataScadenta = view.findViewById(R.id.tvDataScadenta);
            tvDataScadenta.setText(String.valueOf(imprumut.getDataScadenta()));
        }
        return view;
    }
}
