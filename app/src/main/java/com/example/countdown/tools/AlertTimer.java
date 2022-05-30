package com.example.countdown.tools;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.example.countdown.R;
import com.example.countdown.entity.CountDownRecord;

import java.io.IOException;
import java.util.TimerTask;

public class AlertTimer extends TimerTask {
    private CountDownRecord record;
    private Context context;
    MediaPlayer mediaPlayer;

    public AlertTimer(CountDownRecord record, Context context) {
        this.record = record;
        this.context = context;
    }

    @Override
    public void run() {
        if (!record.alertMusic.equals("")) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this.context, Uri.parse(record.alertMusic));
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            Log.i("alarm", "sound start");
        }
    }

    public void stopMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public CountDownRecord getRecord() {
        return record;
    }

    public  void destroy(){
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
