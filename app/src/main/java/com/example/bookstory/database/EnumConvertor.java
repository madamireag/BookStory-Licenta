package com.example.bookstory.database;

import androidx.room.TypeConverter;

import com.example.bookstory.models.Gen;

public class EnumConvertor {
    @TypeConverter
    public static String fromGenreToString(Gen genre) {
        return genre != null
                ? genre.toString()
                : null;
    }

    @TypeConverter
    public static Gen fromStringToGenre(String genre) {
        return genre != null
                ? Gen.valueOf(genre)
                : null;
    }
}
