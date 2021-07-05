package com.example.bookstory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstory.R;
import com.example.bookstory.models.Carte;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {
    EditText etComment;
    ImageView ivSendComment;
    ListView listView;
    List<String> listaComentarii = new ArrayList<>();
    Map<Integer, List<String>> mapComentariiCarte = new HashMap<>();
    Intent intent;
    Carte c;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        etComment = findViewById(R.id.etComentariu);
        ivSendComment = findViewById(R.id.ivComentariu);
        listView = findViewById(R.id.lvCom);
        intent = getIntent();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        if (intent.hasExtra(ListaCartiUserActivity.CARTE)) {
            c = (Carte) intent.getSerializableExtra(ListaCartiUserActivity.CARTE);
            for (int i = 0; i < pref.getString(String.valueOf(c.getIdCarte()), "").length(); i++) {
                listaComentarii = Arrays.asList(pref.getString(String.valueOf(c.getIdCarte()), "").split(","));
            }
            mapComentariiCarte.put(c.getIdCarte(), listaComentarii);
//            if (mapComentariiCarte.containsKey(c.getIdCarte())) {
//                listaComentarii = mapComentariiCarte.get(c.getIdCarte());
//            }
            if (listaComentarii != null) {
                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaComentarii);
                listView.setAdapter(itemsAdapter);
            }
        }

        ivSendComment.setOnClickListener(v -> {
            String comment = etComment.getText().toString();
            if (!comment.isEmpty()) {
                listaComentarii.add(comment);
            }

            if (mapComentariiCarte.containsKey(c.getIdCarte())) {
                mapComentariiCarte.remove(c.getIdCarte());
                mapComentariiCarte.put(c.getIdCarte(), listaComentarii);
            } else {
                mapComentariiCarte.put(c.getIdCarte(), listaComentarii);
            }

            SharedPreferences.Editor editor = pref.edit();
            mapComentariiCarte.forEach((k, val) -> editor.putString(String.valueOf(k), val.toString()));
            editor.apply();
            Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_LONG).show();
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mapComentariiCarte.get(c.getIdCarte()));
            listView.setAdapter(itemsAdapter);
        });

    }
}