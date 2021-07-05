package com.example.bookstory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstory.R;
import com.example.bookstory.models.Carte;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {
    EditText etComment;
    ImageView ivSendComment;
    ListView listView;
    TextView tv;
    ArrayList<String> listaComentarii = new ArrayList<>();
    Intent intent;
    Carte c;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        etComment = findViewById(R.id.etComentariu);
        ivSendComment = findViewById(R.id.ivComentariu);
        tv = findViewById(R.id.tvComentariiCarte);
        listView = findViewById(R.id.lvCom);
        intent = getIntent();


        if (intent.hasExtra(ListaCartiUserActivity.CARTE)) {
            c = (Carte) intent.getSerializableExtra(ListaCartiUserActivity.CARTE);
            String string = "Comentarii despre " + c.getTitlu();
            tv.setText(string);

            listaComentarii = getArrayList(String.valueOf(c.getIdCarte()));

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

            saveArrayList(listaComentarii, String.valueOf(c.getIdCarte()));
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaComentarii);
            listView.setAdapter(itemsAdapter);
            Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_LONG).show();

        });

    }


    public void saveArrayList(ArrayList<String> list, String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getArrayList(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPref", 0);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> list = gson.fromJson(json, type);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }
}