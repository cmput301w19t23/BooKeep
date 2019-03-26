package com.example.bookeep;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Displays the users profile
 * not currently used
 */
public class UserProfileActivity extends AppCompatActivity {
    private ImageView profilePicture;
    private TextView usernameView;
    private TextView nameView;
    private RatingBar ratingBar;
    private TextView numReviewsView;
    private TextView phoneNumberView;
    private TextView emailAddressView;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null){
            userId = intent.getStringExtra("uuid");
        } else{
            userId = "";
        }
        usernameView = findViewById(R.id.username_Profile);
        profilePicture = findViewById(R.id.profile_pic);
        nameView = findViewById(R.id.name_Profile);
        ratingBar = findViewById(R.id.profile_rating);
        numReviewsView = findViewById(R.id.profile_number_reviews);
        emailAddressView = findViewById(R.id.profile_email);
        phoneNumberView = findViewById(R.id.profile_email);
        usernameView.setText(userId);



    }
}
