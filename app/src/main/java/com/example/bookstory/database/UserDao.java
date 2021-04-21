package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bookstory.models.Utilizator;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(Utilizator u);

    @Query("select * from utilizatori")
    List<Utilizator> getAllUsers();

}
