package com.example.myfitness.tab_screen.month_tab;

import android.os.Bundle;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.myfitness.R;
import com.example.myfitness.repository.EventRepo;

public class MonthFragment extends Fragment {

    private TextView notificationTextView;
    private ScrollView notificationScrollView;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        EventRepo.getInstance().loadInitialEvents();
        return inflater.inflate(R.layout.tab_month, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventRepo.getInstance().loadNotifications();
        notificationTextView = view.findViewById(R.id.notificationTextView);
        notificationScrollView = view.findViewById(R.id.notificationScrollView);

    }

    @Override
    public void onResume() {
        super.onResume();
        EventRepo.getInstance().getNotificationData().observe(this, new Observer<Pair<Boolean, Spannable>>() {
            @Override
            public void onChanged(Pair<Boolean, Spannable> booleanSpannablePair) {
                setNotificationText(booleanSpannablePair);
            }
        });

    }

    private void setNotificationText(Pair<Boolean, Spannable> booleanSpannablePair) {

        notificationTextView.setText("");
        Spannable notificationText = booleanSpannablePair.second;
        notificationTextView.setText(notificationText);
    }
}
