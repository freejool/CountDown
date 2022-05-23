package com.example.countdown.tools;

import androidx.room.*;

import com.example.countdown.entity.CountDownRecord;

import java.util.List;

@Dao
public interface CountDownDAO {
    @Query("SELECT * FROM countdownrecord order by end_time")
    List<CountDownRecord> getAll();

    @Insert
    void insertOne(CountDownRecord record);

    @Delete
    void delete(CountDownRecord record);


//
//    @Delete
//    void delete(User user);
}
