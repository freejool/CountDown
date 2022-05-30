package com.example.countdown.tools;

import androidx.room.*;

import com.example.countdown.entity.CountDownRecord;

import java.util.List;

@Dao
public interface CountDownDAO {
    @Query("SELECT * FROM countdownrecord order by end_time desc")
    List<CountDownRecord> getAll();

    @Insert
    void insertOne(CountDownRecord record);

    @Delete
    void delete(CountDownRecord record);

    @Query("select * from countdownrecord where content like :content order by end_time desc")
    List<CountDownRecord> findByContent(String content);

//
//    @Delete
//    void delete(User user);
}
