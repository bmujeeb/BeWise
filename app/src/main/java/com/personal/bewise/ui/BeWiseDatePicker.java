package com.personal.bewise.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import com.personal.bewise.utils.DateUtilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BeWiseDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private int year;
    private int day;
    private int month;
    private TextView dateTextView;
    private String startDate;

    private SimpleDateFormat dateFormat;
    private Calendar calendar;

    // TODO: Change also to bundle argument
    public BeWiseDatePicker(TextView dateTextView, String startDate) {
        this.dateFormat = new SimpleDateFormat(DateUtilities.DATE_FORMAT_DD_MM_YYYY_SLASH, java.util.Locale.getDefault());
        this.calendar = Calendar.getInstance();
        this.dateTextView = dateTextView;
        this.startDate = startDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        if (startDate == null) {
            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            try {
                calendar.setTime(dateFormat.parse(startDate));
                this.year = calendar.get(Calendar.YEAR);
                this.month = calendar.get(Calendar.MONTH);
                this.day = calendar.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {

            }
        }
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        calendar.set(year, month, day);
        dateTextView.setText(dateFormat.format(calendar.getTime()));
    }
}