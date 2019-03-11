package com.example.bookeep;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Activity that allows the user to sign up
 * @author Nafee Khan, Nolan Brost, Jeff Kirker, Dusan Krstic, Hugh Bagan, Kyle Fujishige
 * @see User
 * @see PhoneNumber
 * @version 1.0.1
 */
public class SignUpActivity extends AppCompatActivity{

    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtPhone;
    private EditText edtUserName;
    private Button btnSignUp;
    private FireBaseController fireBaseController = new FireBaseController(this);
    private FirebaseDatabase database;
    private ArrayList<String> users;
    private ArrayList<String> emails;

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

        database = FirebaseDatabase.getInstance();
        DatabaseReference userNameRef = database.getReference("users");
        Query query = userNameRef.orderByChild("userName");
        users = new ArrayList<>();
        emails = new ArrayList<>();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren() ){
                    if (data != null) {
                        try {
                            User userFromDatabase = data.getValue(User.class);
                            users.add(userFromDatabase.getUserName());
                            emails.add(userFromDatabase.getEmail());
                        } catch (DatabaseException exc) {

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    /**
     * validates that the user entered info is valid
     * @return true if valid, false if not
     */
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
            edtEmail.setError("Not a valid email");
        }
        if (edtEmail.getText().toString().trim().length() < 1) {
            edtEmail.setError("Not a valid email");
        }
        if (emails.contains(edtEmail.getText().toString())){
            edtEmail.setError("Email isn't unique");
            emailValid = false;
        }
        if (edtPassword.getText().toString().length() > 5) {
            passwordValid = true;
        } else {
            edtPassword.setError("Password at least 6 characters long.");
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
        if (!userNameValid) {
            edtUserName.setError("Username at least 5 characters long");

        } if (users.contains(edtUserName.getText().toString().trim())) {
            userNameValid = false;
            edtUserName.setError("Username exists already");
        }


        return (emailValid && passwordValid && phoneValid && firstNameValid && lastNameValid && userNameValid);
    }

    /**
     * makes a phone number from the entered phone number string
     * @return PhoneNumber object
     */
    private PhoneNumber makePhoneNumber(){

        String phoneString = edtPhone.getText().toString();
        String areaString = phoneString.substring(0,3);
        String exchangeString = phoneString.substring(3,6);
        String extensionString = phoneString.substring(6,10);



        PhoneNumber phoneNumber = new PhoneNumber(areaString, exchangeString, extensionString);
        return phoneNumber;



    }


}
