package com.example.countdown.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.countdown.MainActivity;
import com.example.countdown.R;
import com.example.countdown.databinding.FragmentNotificationsBinding;
import com.example.countdown.entity.CountDownRecord;
import com.example.countdown.tools.AlertTimer;
import com.example.countdown.tools.AppDatabase;
import com.example.countdown.tools.CountDownDAO;
import com.example.countdown.tools.RecordNotificationAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView recordContainer = binding.notificationsContainer;

        CountDownDAO DAO = AppDatabase.getInstance(getContext()).countDownDAO();

        List<CountDownRecord> rawRecordList = DAO.getAll();
        ArrayList<String> a = new ArrayList<>();
        List<CountDownRecord> records = getRecordsNeedNotifying(rawRecordList);
        RecordNotificationAdapter adapter = new RecordNotificationAdapter(getContext(), R.layout.single_notification, records);

        recordContainer.setAdapter(adapter);
        recordContainer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.notification_stop_menu, popupMenu.getMenu());
                Log.i("view", "longClick");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        MainActivity activity=(MainActivity)getActivity();
                        List<AlertTimer> alertTimers = activity.getAlertTimers();
                        CountDownRecord chosenRecord = (CountDownRecord) parent.getItemAtPosition(position);
                        int chosenId=chosenRecord.uid;
                        for (AlertTimer at:alertTimers){
                            if (at.getRecord().uid==chosenId){
                                at.stopMusic();
                                break;
                            }
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });
        return root;
    }

    // ????????????????????????????????????????????????????????????????????????????????????????????????&???????????????+1??????????????????????????????????????????
    private List<CountDownRecord> getRecordsNeedNotifying(List<CountDownRecord> rawRecords) {
        ArrayList<CountDownRecord> ret = new ArrayList<>();
        for (CountDownRecord rec : rawRecords) {
            LocalDateTime endtime = LocalDateTime.parse(rec.endTime);
            LocalDateTime now = LocalDateTime.now();
            if (rec.alertBeforeMinutes >= 0 &&
                    now.isBefore(endtime.plusMinutes(1)) &&
                    now.isAfter(endtime.minusMinutes(rec.alertBeforeMinutes))
            ) {
                ret.add(rec);
            }
        }
        return ret;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}