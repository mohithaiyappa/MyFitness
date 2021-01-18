package com.example.myfitness.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myfitness.R;

public class AcknowledgementDialog extends Dialog {

    private final String messageText;
    private final Context context;

    private TextView messageTextView, okTextView;


    public AcknowledgementDialog(@NonNull Context context, String message) {
        super(context);
        this.context = context;
        messageText = message;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_acknowledgement);

        //binding views
        messageTextView = findViewById(R.id.messageText);
        okTextView = findViewById(R.id.okText);

        //binding Data
        messageTextView.setText(messageText);

        //setting click listeners
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
