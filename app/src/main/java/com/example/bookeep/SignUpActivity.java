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
    private EditText edtUserName;
    private Button btnSignUp;
    private FireBaseController fireBaseController = new FireBaseController(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtUserName = (EditText) findViewById(R.id.user_name);
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
                    fireBaseController.createNewUser(edtUserName.getText().toString(),
                            edtEmail.getText().toString(),
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
        boolean userNameValid = false;

        //boolean heartValid = false;
        //boolean dateValid = false;
        //boolean timeValid = false;

        if (Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            emailValid = true;
        }
        if (edtPassword.getText().toString().length() > 0) {
            passwordValid = true;
        }
        if (Patterns.PHONE.matcher(edtPhone.getText().toString()).matches() && edtPhone.getText().toString().length() == 10){
            phoneValid = true;
        }
        if (edtFirstName.getText().toString().length() > 0){
            firstNameValid = true;
        }
        if (edtLastName.getText().toString().length() > 0){
            lastNameValid = true;
        }
        if (edtUserName.getText().toString().length() > 5) { //Need to test for uniqueness
            userNameValid = true;
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

        return (emailValid && passwordValid && phoneValid && firstNameValid && lastNameValid && userNameValid);
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
