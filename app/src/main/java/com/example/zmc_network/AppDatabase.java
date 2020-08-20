package com.example.zmc_network;


import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {Person.class}, version = 1,exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract PersonDao personDao();
}
