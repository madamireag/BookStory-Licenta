package com.example.bookstory.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.bookstory.models.AutorCarte;
import com.example.bookstory.models.CarteCuAutor;

import java.util.List;

@Dao
public interface CarteCuAutoriDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(AutorCarte ca);

    @Transaction
    @Query("SELECT * FROM carti")
    List<CarteCuAutor> getCarteCuAutori();

    @Query("DELETE from AutorCarte")
    void deleteAllFromJoinTable();
}
