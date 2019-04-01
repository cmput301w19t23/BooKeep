package com.example.bookeep;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    private ImageView userPhoto;
    private Button btnSignUp;
    private FireBaseController fireBaseController = new FireBaseController(this);
    private FirebaseDatabase database;
    private ArrayList<String> users;
    private ArrayList<String> emails;

    /**
     * onCreate method initializes the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        final ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Create a New Account");
        actionBar.setDisplayHomeAsUpEnabled(true);

        edtUserName = (EditText) findViewById(R.id.user_name);
        edtFirstName = (EditText) findViewById(R.id.first_name);
        edtLastName = (EditText) findViewById(R.id.last_name);
        edtEmail = (EditText) findViewById(R.id.signup_email);
        edtPassword = (EditText) findViewById(R.id.signup_password);
        edtPhone = (EditText) findViewById(R.id.phone);
        btnSignUp = (Button) findViewById(R.id.create_user);
        userPhoto = findViewById(R.id.UserPhoto2);

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
                    Bitmap bitmap;
                    userPhoto.setDrawingCacheEnabled(true);
                    userPhoto.buildDrawingCache();
                    bitmap = ((BitmapDrawable) userPhoto.getDrawable()).getBitmap();
                    fireBaseController.createNewUser(edtUserName.getText().toString(),
                            edtEmail.getText().toString(),
                            edtPassword.getText().toString(),
                            edtFirstName.getText().toString(),
                            edtLastName.getText().toString(),
                            phoneNumber, bitmap);
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
     * This function supports back navigation.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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

    /**
     * Gets a user uploaded image
     * @param view View
     */
    public void ImageUpload(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent,69);
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        if (requestCode == 69 && resultCode == RESULT_OK) {
            if (data != null) {
                super.onActivityResult(requestCode,resultCode,data);
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                    userPhoto.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Deletes the user's profile image.
     * @param view
     */
    public void onDeleteButtonClicked(View view) {
        userPhoto.setImageResource(R.drawable.profile_pic);
    }
}
