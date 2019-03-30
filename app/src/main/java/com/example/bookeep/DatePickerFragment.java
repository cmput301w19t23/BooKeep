package com.example.bookeep;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;


/**
 * This Fragment class is used to get a date picker. A date picker is a calendar view for a potential
 * meetup. This ensures that a user will input a proper calendar date.
 * Resources used to create this: https://developer.android.com/guide/topics/ui/controls/pickers
 * @author kyle
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public OnDateSelectedListener onDateSelectedListener;

    /**
     * Used so we can send a message back to any activity or fragment that calls this when making
     * this fragment. This is used so that we can send the calendar date back to the SetLocation
     * activity that called it.
     * @param onDateSelectedListener
     */
    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    /**
     * Interface used so that a different activity or fragment can call this and get the date.
     *
     */
    public interface OnDateSelectedListener {
        void getDate(int year,int month,int day);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * This is called when the user sets the calendar date. This will send the
     * date back to whatever fragment or activity that calls it.
     * @param view The DatePicker view containing the calendar date.
     * @param year The year of the date that was set.
     * @param month The month of the date that was set.
     * @param dayOfMonth The day of the month of the date that was set.
     */
    @Override
    public void onDateSet(DatePicker view,int year,int month,int dayOfMonth) {
        onDateSelectedListener.getDate(year, month, dayOfMonth);
    }
}
