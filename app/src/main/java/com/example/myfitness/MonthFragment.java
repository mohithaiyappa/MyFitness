package com.example.myfitness;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.QuoteSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.util.List;

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
        EventRepo.getInstance().getNotificationsLiveData().observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                notificationTextView.setText("");
                if(notifications.size()>2){
                    float dp = getResources().getDisplayMetrics().density;
                    LayoutParams lp = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            (int)(65*dp) );

                    notificationScrollView.setLayoutParams(lp);


                }else {
                    LayoutParams lp = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT );

                    notificationScrollView.setLayoutParams(lp);
                }
                SpannableStringBuilder finalSpannable = new SpannableStringBuilder();
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                boolean start = true;
                for(Notification notification : notifications){

                    spannableStringBuilder.clear();
                    int dateTextLength = notification.getNotificationDate().length();
                    if (!start) {
                        dateTextLength++;
                        spannableStringBuilder.append("\n");
                    }
                    spannableStringBuilder.append(notification.getNotificationDate());
                    spannableStringBuilder.setSpan(
                            new ForegroundColorSpan(Color.BLACK),
                            0, dateTextLength,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    spannableStringBuilder.append("\n");
                    spannableStringBuilder.append(notification.getNotificationText());
                    finalSpannable.append(spannableStringBuilder);
                    start =false;

                }
                notificationTextView.setText(finalSpannable);
            }
        });
        super.onResume();
    }
}
