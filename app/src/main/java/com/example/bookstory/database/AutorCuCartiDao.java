package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.bookstory.models.AutorCuCarte;

import java.util.List;

@Dao
public interface AutorCuCartiDao {

    @Query("select * from autori where nume=:numeAutor")
    List<AutorCuCarte> getAutoriCuCartiByName(String numeAutor);
}
