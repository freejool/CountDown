package com.example.countdown.ui.dashboard;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.countdown.R;
import com.example.countdown.databinding.FragmentDashboardBinding;
import com.example.countdown.entity.CountDownRecord;
import com.example.countdown.tools.AlertTimer;
import com.example.countdown.tools.AppDatabase;
import com.example.countdown.tools.CountDownDAO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

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


        CheckBox alertCheckbox = (CheckBox) binding.alertCheckbox;
        EditText alertText = (EditText) binding.alertText;



        Button buttonSubmit = binding.buttonSubmit;
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int alertBeforeMinutes;
                if (alertCheckbox.isChecked()) {
                    try {
                        alertBeforeMinutes = Integer.parseInt(alertText.getText().toString());
                    }catch (Exception e){
                        Toast.makeText(getContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(alertBeforeMinutes<0){
                        Toast.makeText(getContext(),"Timer show be > 0",Toast.LENGTH_LONG).show();
                    }
                } else {
                    alertBeforeMinutes = -1;
                }
                LocalDateTime endtime = LocalDateTime.of(datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute());
                CountDownRecord record = new CountDownRecord(endtime, title_text.getText().toString(), alertBeforeMinutes);
                dao.insertOne(record);
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