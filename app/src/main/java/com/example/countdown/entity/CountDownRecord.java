package com.example.countdown.entity;

import androidx.room.*;

import java.time.LocalDateTime;
import java.util.Timer;

@Entity
public class CountDownRecord {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "end_time")
    public String endTime;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "alert_before_minutes")
    public int alertBeforeMinutes;

    @ColumnInfo(name = "alert_music")
    public String alertMusic;

    public CountDownRecord(int uid, String endTime, String content, int alertBeforeMinutes, String alertMusic) {
        this.uid = uid;
        this.endTime = endTime;
        this.content = content;
        this.alertBeforeMinutes = alertBeforeMinutes;
        this.alertMusic = alertMusic;
    }

    public CountDownRecord(LocalDateTime endTime, String content, int alertBeforeMinutes, String alertMusic) {
        this.endTime = endTime.toString();
        this.content = content;
        this.alertBeforeMinutes = alertBeforeMinutes;
        this.alertMusic = alertMusic;
    }

    @Override
    public String toString() {
        return "CountDownRecord{" +
                "uid=" + uid +
                ", endTime='" + endTime + '\'' +
                ", content='" + content + '\'' +
                ", alertBeforeMinutes=" + alertBeforeMinutes +
                ", alertMusic='" + alertMusic + '\'' +
                '}';
    }
}
