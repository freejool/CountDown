package com.example.countdown;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.countdown.entity.CountDownRecord;
import com.example.countdown.tools.AlertTimer;
import com.example.countdown.tools.AppDatabase;
import com.example.countdown.tools.CountDownDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.example.countdown.databinding.ActivityMainBinding;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Timer alert;
    private List<AlertTimer> timers=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        setAlarm(getApplicationContext());
    }

    private void setAlarm(Context context) {
        CountDownDAO DAO = AppDatabase.getInstance(context).countDownDAO();

        List<CountDownRecord> recordList = DAO.getAll();
        LocalDateTime now = LocalDateTime.now();
        alert = new Timer();
        for (CountDownRecord record : recordList) {
            if (LocalDateTime.parse(record.endTime).minusMinutes(record.alertBeforeMinutes).isAfter(now) &&
                    record.alertBeforeMinutes >= 0) {
                AlertTimer newAlertTimer = new AlertTimer(record, getApplicationContext());
                timers.add(newAlertTimer);
                alert.schedule(newAlertTimer,
                        Date.from(LocalDateTime.parse(newAlertTimer.getRecord().endTime)
                                .minusMinutes(newAlertTimer.getRecord().alertBeforeMinutes)
                                .atZone(ZoneId.systemDefault()).toInstant()));
            }
        }
    }

    public List<AlertTimer> getAlertTimers() {
        return timers;
    }

    public Timer getScheduleTimer() {
        return alert;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (AlertTimer at: timers){
            at.destroy();
        }
    }
}