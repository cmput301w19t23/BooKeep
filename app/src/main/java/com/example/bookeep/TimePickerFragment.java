package com.example.bookeep;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;
/**This Fragment class is used to get a time picker. A time picker is a clock view for a potential
 * meetup. This ensures that a user will input a proper time.
 * Resources used to create this: https://developer.android.com/guide/topics/ui/controls/pickers
 * @author kyle
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public OnTimeSelectedListener onTimeSelectedListener;

    /**Used so we can send a message back to any activity or fragment that calls this when making
     * this fragment. This is used so that we can send the proper time back to the SetLocation
     * activity that called it.
     *
     * @param onTimeSelectedListener
     */
    public void setOnTimeSelectedListener(OnTimeSelectedListener onTimeSelectedListener) {
        this.onTimeSelectedListener = onTimeSelectedListener;
    }

    /**Interface used so that a different activity or fragment can call this and get the time.
     *
     */
    public interface OnTimeSelectedListener {
        void getTime(int hour,int minute);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    /**This is called when the user sets the clock time. This will send the
     * time back to whatever fragment or activity that calls it.
     * @param view
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker view,int hourOfDay,int minute) {
        onTimeSelectedListener.getTime(hourOfDay, minute);
    }
}
