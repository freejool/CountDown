package com.example.countdown.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.countdown.R;
import com.example.countdown.databinding.FragmentHomeBinding;
import com.example.countdown.entity.CountDownRecord;
import com.example.countdown.tools.AppDatabase;
import com.example.countdown.tools.CountDownDAO;
import com.example.countdown.tools.CountDownRecordAdapter;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView recordContainer = binding.countDownContainer;

        CountDownDAO DAO = AppDatabase.getInstance(getContext()).countDownDAO();

        List<CountDownRecord> recordList = DAO.getAll();
        CountDownRecordAdapter adapter = new CountDownRecordAdapter(getContext(), R.layout.single_countdown, recordList);

        recordContainer.setAdapter(adapter);
        recordContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountDownRecord rec = (CountDownRecord) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), rec.endTime, Toast.LENGTH_LONG).show();
            }
        });
        recordContainer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.main_delete_menu, popupMenu.getMenu());
                Log.i("view","longclick");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        CountDownRecord record = (CountDownRecord) parent.getItemAtPosition(position);
                        DAO.delete(record);
                        return false;
                    }
                });
                popupMenu.show();
                return false;
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