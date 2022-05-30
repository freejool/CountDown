package com.example.countdown.tools;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.countdown.R;
import com.example.countdown.entity.CountDownRecord;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CountDownRecordAdapter extends ArrayAdapter<CountDownRecord> {

    private int resource;

    public CountDownRecordAdapter(@NonNull Context context, int resource, @NonNull List<CountDownRecord> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout linearView;
        CountDownRecord record = getItem(position);
        if (convertView == null) {
            linearView = new LinearLayout(getContext());
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater.inflate(resource, linearView, true);
        } else {
            linearView = (LinearLayout) convertView;
        }
        TextView content = (TextView) linearView.findViewById(R.id.textView_content);
        TextView days_left = (TextView) linearView.findViewById(R.id.dayleft);
        TextView days=(TextView )linearView.findViewById(R.id.textView_day);

        CountDownDAO dao = AppDatabase.getInstance(getContext()).countDownDAO();

        content.setText(record.content);
        LocalDateTime target = LocalDateTime.parse(record.endTime);
        if (target.isAfter(LocalDateTime.now())) {
            Long deltaDay = ChronoUnit.DAYS.between(LocalDateTime.now(), target);
            days_left.setText(R.string.days_left);
            days.setText(String.valueOf(deltaDay));

        }
        else {
            Long deltaDay=ChronoUnit.DAYS.between(target, LocalDateTime.now());
            days_left.setText(R.string.days_passed);
            days.setText(String.valueOf(deltaDay));
        }

        return linearView;
    }
}
