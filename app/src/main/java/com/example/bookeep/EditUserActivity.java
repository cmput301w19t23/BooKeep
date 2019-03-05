package com.example.bookeep;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class EditUserActivity extends AppCompatActivity {
    private EditText userName;
    private ImageView userPicture;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText phoneNumber;
    private Button updateButton;
    private User user;

    //need to add get text from edit user to prefill the edit texts
    //need to change user info in firebase
    //also need to add on click listener to actually get here

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        phoneNumber = findViewById(R.id.EditPhoneNumber);
        email = findViewById(R.id.EditEmail);
        lastName = findViewById(R.id.EditLastName);
        firstName = findViewById(R.id.EditFirstName);
        userName = findViewById(R.id.EditUserName);
        userPicture = findViewById(R.id.UserPhoto);
        updateButton = findViewById(R.id.SaveProfile);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()){
                    PhoneNumber phone = new PhoneNumber(phoneNumber.getText().toString());
                    String emailString = email.getText().toString();
                    String lastNameString = lastName.getText().toString();
                    String firstNameString = firstName.getText().toString();
                    String userNameString = userName.getText().toString();


                }
            }
        });
    }

    private boolean validation() {

        boolean emailValid = false;
        boolean usernameValid = false;
        boolean phoneValid = false;
        boolean firstNameValid = false;
        boolean lastNameValid = false;

        //boolean heartValid = false;
        //boolean dateValid = false;
        //boolean timeValid = false;

        if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailValid = true;
        }
        //need to set check for username availability
        usernameValid = true;

        if (Patterns.PHONE.matcher(phoneNumber.getText().toString()).matches() && phoneNumber.getText().toString().length() == 10) {
            phoneValid = true;
        }
        if (firstName.getText().toString().length() > 0) {
            firstNameValid = true;
        }
        if (lastName.getText().toString().length() > 0) {
            lastNameValid = true;
        }

        return (emailValid && usernameValid && phoneValid && firstNameValid && lastNameValid);
    }

}
