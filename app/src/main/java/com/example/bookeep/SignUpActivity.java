package com.example.bookeep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtPhone;
    private Button btnSignUp;
    private FireBaseController fireBaseController = new FireBaseController(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtFirstName = (EditText) findViewById(R.id.first_name);
        edtLastName = (EditText) findViewById(R.id.last_name);
        edtEmail = (EditText) findViewById(R.id.signup_email);
        edtPassword = (EditText) findViewById(R.id.signup_password);
        edtPhone = (EditText) findViewById(R.id.phone);
        btnSignUp = (Button) findViewById(R.id.create_user);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validation()){

                    PhoneNumber phoneNumber = makePhoneNumber();
                    fireBaseController.createNewUser(edtEmail.getText().toString(),
                            edtPassword.getText().toString(),
                            edtFirstName.getText().toString(),
                            edtLastName.getText().toString(),
                            phoneNumber);

                }

            }

        });




    }
    private boolean validation(){

        boolean emailValid = false;
        boolean passwordValid = false;
        boolean phoneValid = false;
        boolean firstNameValid = false;
        boolean lastNameValid = false;

        //boolean heartValid = false;
        //boolean dateValid = false;
        //boolean timeValid = false;

        if (Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString().trim()).matches()) {
            emailValid = true;
        } else {
            edtEmail.setError("Invalid email!");
        }
        if (edtPassword.getText().toString().length() > 0) {
            passwordValid = true;
        } else {
            edtPassword.setError("Invalid password!");
        }
        if (Patterns.PHONE.matcher(edtPhone.getText().toString().trim()).matches() && edtPhone.getText().toString().trim().length() == 10){
            phoneValid = true;
        } else {
            edtPhone.setError("Invalid phone number!");
        }
        if (edtFirstName.getText().toString().trim().length() > 0){
            firstNameValid = true;
        } else {
            edtFirstName.setError("Invalid first name!");
        }
        if (edtLastName.getText().toString().trim().length() > 0){
            lastNameValid = true;
        } else {
            edtLastName.setError("Invalid last name!");
        }
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

        return (emailValid && passwordValid && phoneValid && firstNameValid && lastNameValid);
    }

    private PhoneNumber makePhoneNumber(){

        String phoneString = edtPhone.getText().toString();
        String areaString = phoneString.substring(0,3);
        String exchangeString = phoneString.substring(3,6);
        String extensionString = phoneString.substring(6,10);

        //Integer area = Integer.valueOf(areaString);
        //nteger exchange = Integer.valueOf(exchangeString);
        //Ineger extension = Integer.valueOf(extensionString);

        PhoneNumber phoneNumber = new PhoneNumber(areaString, exchangeString, extensionString);
        return phoneNumber;


    }


}
