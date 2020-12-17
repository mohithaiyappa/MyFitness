package com.example.myfitness;

import android.os.Bundle;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

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
        EventRepo.getInstance().getNotificationData().observe(this, new Observer<Pair<Boolean, Spannable>>() {
            @Override
            public void onChanged(Pair<Boolean, Spannable> booleanSpannablePair) {
                setNotificationText(booleanSpannablePair);
            }
        });
        super.onResume();
    }

    private void setNotificationText(Pair<Boolean, Spannable> booleanSpannablePair) {
        notificationTextView.setText("");
        try {
            boolean shouldWrap = booleanSpannablePair.first;
            Spannable notificationText = booleanSpannablePair.second;

            if (!shouldWrap) {
                float dp = getResources().getDisplayMetrics().density;
                LayoutParams lp = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        (int) (35 * dp));

                notificationScrollView.setLayoutParams(lp);


            } else {
                LayoutParams lp = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);

                notificationScrollView.setLayoutParams(lp);
            }

            notificationTextView.setText(notificationText);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
