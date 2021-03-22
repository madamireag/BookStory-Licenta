package com.example.bookstory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.CarteCuAutor;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LibraryDB db = LibraryDB.getInstanta(getApplicationContext());
        List<CarteCuAutor> carteCuAutors = db.getCarteDao().getCarteCuAutori();

        Log.i("TESTCARTE",carteCuAutors.get(0).carte.toString());
        Log.i("TESTAUTORICARTE",carteCuAutors.get(0).autori.get(0).toString());
    }
}