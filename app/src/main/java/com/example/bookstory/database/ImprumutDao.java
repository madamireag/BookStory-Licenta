package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bookstory.models.Imprumut;

import java.util.List;

@Dao
public interface ImprumutDao {

    @Insert
    long insert(Imprumut i);

    @Query("SELECT * FROM imprumuturi where idUtilizator=:userId")
    List<Imprumut> getAllImprumuturiForUser(long userId);

    @Query("SELECT * FROM imprumuturi")
    List<Imprumut> getAll();

    @Delete
    void delete(Imprumut i);

}
