package com.example.myfitness;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePick extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dlgDatePicker = new DatePickerDialog(getActivity(), (Reservation)getActivity(),  year, month, day);

        // 最大値 七か月先まで表示
        GregorianCalendar maxDate = new GregorianCalendar();
        maxDate.set(year, month + 7, day);

        // 最小値 今日以降のみ表示
        GregorianCalendar minDate = new GregorianCalendar();
        minDate.set(year,month,day);

        DatePicker datePicker = dlgDatePicker.getDatePicker();
        if (datePicker != null) {
            datePicker.setMaxDate(maxDate.getTimeInMillis());
            datePicker.setMinDate(minDate.getTimeInMillis());
        }
        return dlgDatePicker;
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year,
                          int monthOfYear, int dayOfMonth) {
    }

}
