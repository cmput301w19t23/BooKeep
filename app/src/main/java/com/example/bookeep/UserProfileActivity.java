package com.example.bookeep;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Displays the users profile
 * not currently used
 */
public class UserProfileActivity extends AppCompatActivity {
    private ImageView profilePicture;
    private TextView usernameView;
    private TextView nameView;
    private RatingBar borrowerRatingBar;
    private RatingBar lenderRatingBar;
    private TextView numBorrowerReviewsView;
    private TextView numLenderReveiewsView;
    private TextView phoneNumberView;
    private TextView emailAddressView;
    private String userId;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
//https://stackoverflow.com/questions/14483393/how-do-i-change-the-android-actionbar-title-and-icon look to for changing action bar to allow edits

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent intent = getIntent();
        if (intent != null){
            userId = intent.getStringExtra("uuid");
        } else{
            userId = "";
        }

        usernameView = findViewById(R.id.username_Profile);
        profilePicture = findViewById(R.id.profile_pic);
        nameView = findViewById(R.id.name_Profile);
        emailAddressView = findViewById(R.id.profile_users_email);
        phoneNumberView = findViewById(R.id.profile_users_phone);
        borrowerRatingBar = findViewById(R.id.borrower_ratingbar);
        lenderRatingBar = findViewById(R.id.lender_ratingbar);
        numBorrowerReviewsView = findViewById(R.id.borrower_number_reviews);
        numLenderReveiewsView = findViewById(R.id.lender_number_reviews);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        myRef = database.getReference("users").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String name = user.getFirstname() + " " + user.getLastname();
                nameView.setText(name);
                usernameView.setText(user.getUserName());
                phoneNumberView.setText(user.getPhoneNumber().toString());
                emailAddressView.setText(user.getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(firebaseUser.getUid().equals(userId)) {
            getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit_icon) {
            Intent intent = new Intent(this, EditUserActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
