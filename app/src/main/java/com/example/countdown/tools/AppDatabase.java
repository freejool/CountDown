package com.example.countdown.tools;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.countdown.entity.CountDownRecord;

@Database(entities = {CountDownRecord.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CountDownDAO countDownDAO();

    private static AppDatabase INSTANCE;
    private static final Object sLock = new Object();



    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user.db")
                                .allowMainThreadQueries()
                                .build();
            }
            return INSTANCE;
        }
    }
}
