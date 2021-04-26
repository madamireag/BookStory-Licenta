package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;


import com.example.bookstory.models.ImprumutCarte;
import com.example.bookstory.models.ImprumutCuCarte;

import java.util.List;

@Dao
public interface ImprumutCuCarteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ImprumutCarte ic);

    @Transaction
    @Query("SELECT * FROM imprumuturi")
    List<ImprumutCuCarte> getImprumutcuCarti();


}
