package com.example.bookeep;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

public class EditUserActivity extends AppCompatActivity {
    private EditText userName;
    private ImageView userPicture;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText phoneNumber;

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
    }

}
