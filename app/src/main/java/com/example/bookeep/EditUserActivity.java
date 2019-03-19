package com.example.bookeep;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
 * This activity will display the users information (username, first name, lastname,
 * email and phone number. The user can edit any of the fields and the changed information
 * will be updated in the firebase database. The information given will be checked for
 * correctness, specifically the username and email will be checked for uniqueness.
 * If any given info is not correct an error will be displayed when the user attempts
 * to save their changes.
 * @author Nolan Brost
 * @see User
 * @see PhoneNumber
 * @see SignUpActivity
 * @see MainActivity
 * @version 1.0.1
 */
public class EditUserActivity extends AppCompatActivity {
    private EditText userName;
    private ImageView userPicture;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText phoneNumber;
    private TextView editUserText;
    private TextView errorTextView;
    private Button updateButton;
    private User user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<String> users;
    private ArrayList<String> emails;
    private String userId;
    private ArrayList<String> currentUsername;
    private ArrayList<String> currentEmail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        users = new ArrayList<>();
        emails = new ArrayList<>();
        phoneNumber = findViewById(R.id.EditPhoneNumber);
        email = findViewById(R.id.EditEmail);
        lastName = findViewById(R.id.EditLastName);
        firstName = findViewById(R.id.EditFirstName);
        userName = findViewById(R.id.EditUserName);
        userPicture = findViewById(R.id.UserPhoto);
        updateButton = findViewById(R.id.SaveProfile);
        errorTextView = findViewById(R.id.ErrorText);
        editUserText = findViewById(R.id.EditUserInfoView);


    }

    @Override
    public void onResume(){
        super.onResume();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        currentEmail = new ArrayList<>();                               //will contain all emails to check for uniqueness
        currentUsername = new ArrayList<>();                            //will contain all users to check for uniqueness
        errorTextView.setVisibility(View.INVISIBLE);                    //sets error text to invisible
        errorTextView.setTextColor(Color.RED);                          //sets error texts colour to red
        editUserText.setTextColor(Color.BLACK);
        editUserText.setTextSize(22);


        userId = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        DatabaseReference userNameRef = database.getReference("users");
        Query query = userNameRef.orderByChild("userName");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(userId);

        //This will query the data base for all users and store their usernames and email in lists to check for uniqueness
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

        //this will get the users info from the database to prefill editing fields and to check if username/email haven't changed
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    userName.setText(currentUser.getUserName());
                    currentUsername.add(currentUser.getUserName());
                    firstName.setText(currentUser.getFirstname());
                    lastName.setText(currentUser.getLastname());
                    email.setText(currentUser.getEmail());
                    currentEmail.add(currentUser.getEmail());
                    phoneNumber.setText(currentUser.getPhoneNumber().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //will check if enter values are correct and updates the user if so
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()){
                    try {
                        firebaseUser.updateEmail(email.getText().toString());           //changes log in email, but causes exceptions not yet caught.
                        updateUser();
                    } catch (Exception e){
                        String error = e.toString();                                    //catches any exceptions and displays error to be fixed later
                        displayErrorText(error);
                    }
                }
            }
        });
    }

    /**
     * will push changes to user to the firebase database
     * @see User
     */
    private void updateUser(){
        String emailString = email.getText().toString();
        String lastNameString = lastName.getText().toString();
        String firstNameString = firstName.getText().toString();
        String userNameString = userName.getText().toString();
        String phoneString = phoneNumber.getText().toString();
        user = new User(userNameString, emailString, firstNameString, lastNameString, userId);
        user.setPhoneNumber(new PhoneNumber(phoneString));
        user.setEmail(emailString);
        user.setLastname(lastNameString);
        user.setFirstname(firstNameString);
        user.setUserName(userNameString);
        myRef.setValue(user);
        finish();//closes the activity after firebase is updated
    }

    /**
     * checks all editTexts to make sure they were filled correctly
     * @return true if entered info is valid, and false otherwise
     */

    private boolean validation() {

        boolean emailValid = false;
        boolean usernameValid;
        boolean phoneValid = false;
        boolean firstNameValid = false;
        boolean lastNameValid = false;
        String errorText;
        //Checks to make sure entered email is not already used or is the email already associated with the account, and that it is a proper email
        //Sets emailValid to true or false depending on the test
        if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailValid = (!emails.contains(email.getText().toString())
                          || currentEmail.contains(email.getText().toString()));
        }

        //Checks to make sure a proper phone numebr is given and sets phoneValid accordingly
        if (Patterns.PHONE.matcher(phoneNumber.getText().toString()).matches() && phoneNumber.getText().toString().length() == 10) {
            phoneValid = true;
        }

        //Checks to make sure a first name was entered and sets firstNameValid accordingly
        if (firstName.getText().toString().length() > 0) {
            firstNameValid = true;
        }

        //Checks to make sure a last name was entered and sets lastNameValid accordingly
        if (lastName.getText().toString().length() > 0) {
            lastNameValid = true;
        }
        //tests to make sure username is unique (or old username) and longer than 4 characters long and sets usernameValid accordingly
        usernameValid = (userName.getText().toString().length() > 4
                     && (!users.contains(userName.getText().toString())
                     || currentUsername.contains(userName.getText().toString())));

        //displays error text if any of the fields where improperly filled.
        //will only display one at a time
        if (!usernameValid) {
            errorText = "Not a unique username";
            userName.setError(errorText);
        }
        if (!firstNameValid) {
            errorText = "Please enter your first name";
            firstName.setError(errorText);
        }
        if (!lastNameValid){
            errorText = "Please enter your last name";
            lastName.setError(errorText);
        }
        if (!emailValid){
            errorText = "Please enter a unique email address";
            email.setError(errorText);
        }
        if (!phoneValid) {
            errorText = "Please enter your phone number";
            phoneNumber.setError(errorText);
        }

        //returns true if all fields filled correctly, false otherwise
        return (emailValid && usernameValid && phoneValid && firstNameValid && lastNameValid);
    }

    /**
     * sets the errorTextView to the string given and visible
     * @param errorMessage
     */
    public void displayErrorText(String errorMessage){
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(errorMessage);
    }

}
