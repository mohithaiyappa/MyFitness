package com.example.myfitness.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myfitness.R;
import com.example.myfitness.tab_screen.TabActivity;
import com.example.myfitness.utils.CustomViewPager;

public class EventAddedResponseDialog extends Dialog {

    private TextView nextTabButton, currentTabButton;
    private Context mContext;


    public EventAddedResponseDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_event_response);

        nextTabButton = findViewById(R.id.nextTabButton);
        currentTabButton = findViewById(R.id.currentTabButton);

        currentTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        nextTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CustomViewPager) ((TabActivity) mContext).findViewById(R.id.view_pager)).moveTo(3);
                dismiss();
            }
        });
    }
}
