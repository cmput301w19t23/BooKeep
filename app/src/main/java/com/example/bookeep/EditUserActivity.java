package com.example.bookeep;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditUserActivity extends AppCompatActivity {
    private EditText userName;
    private ImageView userPicture;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText phoneNumber;
    private Button updateButton;
    private User user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<String> users;
    private ArrayList<String> emails;
    private String userId;


    //need to add get text from edit user to prefill the edit texts
    //need to change user info in firebase
    //also need to add on click listener to actually get here

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


    }

    @Override
    public void onResume(){
        super.onResume();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        userId = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        DatabaseReference userNameRef = database.getReference("users");
        Query query = userNameRef.orderByChild("userName");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren() ){
                    User userFromDatabase = data.getValue(User.class);
                    users.add(userFromDatabase.getUserName());
                    emails.add(userFromDatabase.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    userName.setText(currentUser.getUserName());
                    firstName.setText(currentUser.getFirstname());
                    lastName.setText(currentUser.getFirstname());
                    email.setText(currentUser.getEmail());
                    phoneNumber.setText(currentUser.getPhoneNumber().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()){
                    updateUser();
                }
            }
        });
    }

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
        finish();
    }

    private boolean validation() {

        boolean emailValid = false;
        boolean usernameValid;
        boolean phoneValid = false;
        boolean firstNameValid = false;
        boolean lastNameValid = false;

        //boolean heartValid = false;
        //boolean dateValid = false;
        //boolean timeValid = false;

        if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailValid = !emails.contains(email.getText().toString());
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
        usernameValid = (userName.getText().toString().length() > 4 && !users.contains(userName.getText().toString()));

        return (emailValid && usernameValid && phoneValid && firstNameValid && lastNameValid);
    }

}
