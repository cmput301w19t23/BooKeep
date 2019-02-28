package com.example.bookeep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

    }
    private boolean validation(){
        boolean emailValid = false;
        boolean passwordValid = false;
        //boolean heartValid = false;
        //boolean dateValid = false;
        //boolean timeValid = false;

        if (Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            emailValid = true;
        }
        if (edtPassword.getText().toString().length() > 0) {
            passwordValid = true;
        }
        if (Patterns.PHONE.matcher(edtPhone.getText().toString().matches()))
        /*
        if (editHeartRate.getText().toString().length() > 0) {
            heartValid = true;
        }
        if (pickDate.getText().toString().length() > 0) {
            dateValid = true;
        }
        if (pickTime.getText().toString().length() > 0) {
            timeValid = true;
        }*/

        return (emailValid && passwordValid);
    }
}
