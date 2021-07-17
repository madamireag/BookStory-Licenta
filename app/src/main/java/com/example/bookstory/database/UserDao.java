package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bookstory.models.Utilizator;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(Utilizator u);

    @Query("select * from utilizatori")
    List<Utilizator> getAllUsers();

    @Query("select * from utilizatori where uid=:uid")
    Utilizator getUserByUid(String uid);

    @Query("select * from utilizatori where id=:id")
    Utilizator getUserById(int id);

    @Update
    void update(Utilizator u);

    @Delete
    void delete(Utilizator u);

}
