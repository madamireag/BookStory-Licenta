package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bookstory.models.Carte;

@Dao
public interface CarteDao {
    @Insert
    long insert(Carte c);

    @Query("delete from carti")
    void deleteAll();

    @Delete
    void deleteBook(Carte c);
}
