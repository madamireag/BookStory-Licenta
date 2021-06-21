package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bookstory.models.Autor;

import java.util.List;

@Dao
public interface AutorDao {
    @Insert
    long insert(Autor autor);

    @Query("delete from autori")
    void deleteAll();

    @Query("select * from autori")
    List<Autor> getAll();

    @Query("select * from autori where nume=:numeAutor")
    List<Autor> getAllByName(String numeAutor);

}
