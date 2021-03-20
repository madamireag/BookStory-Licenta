package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bookstory.models.Autor;

@Dao
public interface AutorDao {
    @Insert
    long insert(Autor autor);
    @Query("delete from autori")
    void deleteAll();

}
