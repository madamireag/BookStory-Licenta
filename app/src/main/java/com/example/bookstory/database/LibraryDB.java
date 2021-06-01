package com.example.bookstory.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.bookstory.models.Autor;
import com.example.bookstory.models.AutorCarte;
import com.example.bookstory.models.Carte;
import com.example.bookstory.models.Imprumut;
import com.example.bookstory.models.ImprumutCarte;
import com.example.bookstory.models.Utilizator;

@Database(entities = {Carte.class, Autor.class, AutorCarte.class, Utilizator.class, Imprumut.class, ImprumutCarte.class}, version = 5, exportSchema = false)
@TypeConverters({DateConvertor.class, EnumConvertor.class})
public abstract class LibraryDB extends RoomDatabase {
    private final static String DB_NAME = "library.db";
    private static LibraryDB instanta;

    public static LibraryDB getInstanta(Context context) {
        if (instanta == null) {
            synchronized (LibraryDB.class) {
                if (instanta == null) {
                    instanta = Room.databaseBuilder(context, LibraryDB.class, DB_NAME)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return instanta;
    }

    public abstract CarteCuAutoriDao getCarteCuAutoriDao();

    public abstract CarteDao getCartiDao();

    public abstract AutorDao getAutorDao();

    public abstract UserDao getUserDao();

    public abstract ImprumutDao getImprumutDao();

    public abstract ImprumutCuCarteDao getImprumutCuCarteDao();
}


