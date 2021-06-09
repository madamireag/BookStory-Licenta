package com.example.bookstory.activities;

import android.os.Bundle;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Imprumut;
import com.example.bookstory.models.Utilizator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    LibraryDB dbInstance;
    FirebaseAuth auth;
    List<Imprumut> imprumuturi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendarView);
        dbInstance = LibraryDB.getInstanta(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        Utilizator utilizator = null;

        if(auth.getCurrentUser() != null) {
           utilizator = dbInstance.getUserDao().getUserByUid(auth.getCurrentUser().getUid());
        }

        if (utilizator != null) {
            imprumuturi = dbInstance.getImprumutDao().getAllImprumuturiForUser(utilizator.getId());
        }


    }
}