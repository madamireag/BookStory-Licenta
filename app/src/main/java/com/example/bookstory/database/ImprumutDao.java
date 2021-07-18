package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bookstory.models.Imprumut;

import java.util.List;

@Dao
public interface ImprumutDao {

    @Insert
    long insert(Imprumut i);

    @Query("SELECT * FROM imprumuturi where idUtilizator=:userId")
    List<Imprumut> getAllImprumuturiForUser(long userId);

    @Query("SELECT * FROM imprumuturi where idImprumut=:id")
    Imprumut getImprumutById(long id);

    @Query("delete from imprumuturi where idUtilizator=:id")
    void deleteAllForUser(long id);


    @Query("delete from ImprumutCarte where idImprumut=:id")
    void deleteAllIC(long id);

    @Delete
    void delete(Imprumut i);

    @Update
    void update(Imprumut i);

}
