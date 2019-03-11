package com.example.bookeep;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class SignUpActivity extends AppCompatActivity{

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

        edtPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btnSignUp.performClick();
                }
                return false;
            }
        });


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

                    //Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    //startActivity(intent);

                }

            }

        });




    }
    private boolean validation() {

        boolean emailValid = false;
        boolean passwordValid = false;
        boolean phoneValid = false;
        boolean firstNameValid = false;
        boolean lastNameValid = false;
        boolean userNameValid;

        //boolean heartValid = false;
        //boolean dateValid = false;
        //boolean timeValid = false;

        if (Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString().trim()).matches()) {
            emailValid = true;
        } else {
            edtEmail.setError("Invalid email.");
        }
        if (edtPassword.getText().toString().length() > 5) {
            passwordValid = true;
        } else {
            edtPassword.setError("At least 6 characters long.");
        }
        if (Patterns.PHONE.matcher(edtPhone.getText().toString().trim()).matches() && edtPhone.getText().toString().trim().length() == 10){
            phoneValid = true;
        } else {
            edtPhone.setError("Invalid phone number.");
        }
        if (edtFirstName.getText().toString().trim().length() > 0){
            firstNameValid = true;
        } else {
            edtFirstName.setError("Invalid first name.");
        }
        if (edtLastName.getText().toString().trim().length() > 0){
            lastNameValid = true;
        } else {
            edtLastName.setError("Invalid last name.");
        }
        userNameValid = edtUserName.getText().toString().length() > 4;


        return (emailValid && passwordValid && phoneValid && firstNameValid && lastNameValid && userNameValid);
    }

    private PhoneNumber makePhoneNumber(){

        String phoneString = edtPhone.getText().toString();
        String areaString = phoneString.substring(0,3);
        String exchangeString = phoneString.substring(3,6);
        String extensionString = phoneString.substring(6,10);



        PhoneNumber phoneNumber = new PhoneNumber(areaString, exchangeString, extensionString);
        return phoneNumber;



    }


}
