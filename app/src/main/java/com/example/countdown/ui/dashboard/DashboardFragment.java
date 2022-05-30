package com.example.countdown.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.countdown.MainActivity;
import com.example.countdown.R;
import com.example.countdown.databinding.FragmentDashboardBinding;
import com.example.countdown.entity.CountDownRecord;
import com.example.countdown.tools.AlertTimer;
import com.example.countdown.tools.AppDatabase;
import com.example.countdown.tools.CountDownDAO;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private Uri selectMusic;
    private ActivityResultLauncher<String>
            fileExplorer;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        AppDatabase db = AppDatabase.getInstance(getContext());
        CountDownDAO dao = db.countDownDAO();


        EditText title_text = binding.titleText;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), title_text::setText);


        DatePicker datePicker = binding.datePicker;
        TimePicker timePicker = binding.timePicker;
        Spinner chooseMusic = binding.chooseMusicSpinner;
        ArrayAdapter<CharSequence> chooseMusicAdapter = ArrayAdapter.createFromResource(getContext(), R.array.choose_music, android.R.layout.simple_spinner_dropdown_item);
        chooseMusic.setAdapter(chooseMusicAdapter);

        TextView musicTitle = binding.musicTitle;

        fileExplorer = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                selectMusic = result;
                musicTitle.setText(selectMusic.getLastPathSegment().replaceFirst("^.+/", ""));
            }
        });
        chooseMusic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        selectMusic = null;
                        musicTitle.setText("");
                        break;
                    }
                    case 1: {
                        fileExplorer.launch("audio/*");
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        CheckBox alertCheckbox = binding.alertCheckbox;
        EditText alertText = binding.alertText;


        Button buttonSubmit = binding.buttonSubmit;
        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int alertBeforeMinutes;
                if (alertCheckbox.isChecked()) {
                    if (alertText.getText().toString().equals("")) {
                        alertBeforeMinutes = 0;
                    } else {
                        try {
                            alertBeforeMinutes = Integer.parseInt(alertText.getText().toString());
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    if (alertBeforeMinutes < 0) {
                        Toast.makeText(getContext(), "Timer show be > 0", Toast.LENGTH_LONG).show();
                    }
                } else {
                    alertBeforeMinutes = -1;
                    selectMusic = null;
                }
                LocalDateTime endtime = LocalDateTime.of(datePicker.getYear(),
                        datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute());
                CountDownRecord record = new CountDownRecord(endtime,
                        title_text.getText().toString(),
                        alertBeforeMinutes,
                        selectMusic == null ? "" : selectMusic.toString());
                dao.insertOne(record);
                if (LocalDateTime.parse(record.endTime).minusMinutes(record.alertBeforeMinutes).isAfter(LocalDateTime.now())) {
                    ((MainActivity) getActivity()).getAlert().schedule(new AlertTimer(record, getContext()),
                            Date.from(LocalDateTime.parse(record.endTime)
                                    .minusMinutes(record.alertBeforeMinutes).atZone(ZoneId.systemDefault()).toInstant()));
                }
                Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}