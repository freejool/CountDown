package com.example.countdown.tools;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.countdown.R;
import com.example.countdown.entity.CountDownRecord;

import java.util.TimerTask;

public class AlertTimer extends TimerTask {
    private CountDownRecord record;
    private Context context;

    public AlertTimer(CountDownRecord record,Context context) {
        this.record = record;
        this.context=context;
    }

    @Override
    public void run() {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.drive);
        mediaPlayer.start();
        Log.i("alarm","sound start");
    }

}
