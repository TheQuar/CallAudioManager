package com.quar.turnoffsilentmode.room_database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {NumbersTable.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "database.db";

    private static AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
//    private static final Object sLock = new Object();

    public abstract AppDao appDao();

    public static AppDatabase getInstance(final Context context) {

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }
}
