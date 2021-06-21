package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.bookstory.models.CarteCuImprumut;

import java.util.List;

@Dao
public interface CarteCuImprumutDao {

    @Query("select * from carti")
    List<CarteCuImprumut> getAll();
}
