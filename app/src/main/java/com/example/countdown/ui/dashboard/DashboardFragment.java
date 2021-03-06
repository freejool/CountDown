package com.example.countdown.ui.dashboard;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
        CheckBox chooseMusic = binding.chooseMusicCheckbox;

        TextView musicTitle = binding.musicTitle;

        fileExplorer = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                selectMusic = result;
                musicTitle.setText(selectMusic.getLastPathSegment().replaceFirst("^.+/", ""));
            }

        });

        chooseMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fileExplorer.launch("audio/*");
                } else {
                    musicTitle.setText("");
                }
            }
        });

        CheckBox alertCheckbox = binding.alertCheckbox;
        EditText alertText = binding.alertText;
        alertText.setEnabled(false);
        chooseMusic.setEnabled(false);

        alertCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alertText.setEnabled(true);
                    chooseMusic.setEnabled(true);
                } else {
                    alertText.setEnabled(false);
                    chooseMusic.setEnabled(false);
                }
            }
        });


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
                record = dao.findByEndTime(record.endTime);
                if (LocalDateTime.parse(record.endTime).minusMinutes(record.alertBeforeMinutes).isAfter(LocalDateTime.now())) {
                    AlertTimer newAT = new AlertTimer(record, getContext());
                    ((MainActivity) getActivity()).getAlertTimers().add(newAT);
                    ((MainActivity) getActivity()).getScheduleTimer().schedule(newAT,
                            Date.from(LocalDateTime.parse(record.endTime)
                                    .minusMinutes(record.alertBeforeMinutes).atZone(ZoneId.systemDefault()).toInstant()));
                }
                Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
                chooseMusic.setChecked(false);
                alertCheckbox.setChecked(false);
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
