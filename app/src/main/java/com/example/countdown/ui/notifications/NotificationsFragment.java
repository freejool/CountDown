package com.example.countdown.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.countdown.R;
import com.example.countdown.databinding.FragmentNotificationsBinding;
import com.example.countdown.entity.CountDownRecord;
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
        ArrayList<String> a=new ArrayList<>();
        List<CountDownRecord> records= getRecordsNeedNotifying(rawRecordList);
        RecordNotificationAdapter adapter = new RecordNotificationAdapter(getContext(), R.layout.single_notification, records);

        recordContainer.setAdapter(adapter);
        return root;
    }

    // 将需要显示的记录从所有记录中挑出来，指当前时间在设置的提醒时间后&在结束时间+1分钟前，而且设置了提醒的记录
    private List<CountDownRecord> getRecordsNeedNotifying(List<CountDownRecord> rawRecords){
        ArrayList<CountDownRecord> ret = new ArrayList<>();
        for (CountDownRecord rec:rawRecords){
            LocalDateTime endtime=LocalDateTime.parse(rec.endTime);
            LocalDateTime now = LocalDateTime.now();
            if (rec.alertBeforeMinutes>=0&&
                    now.isBefore(endtime.plusMinutes(1))&&
                    now.isAfter(endtime.minusMinutes(rec.alertBeforeMinutes))
            ){
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