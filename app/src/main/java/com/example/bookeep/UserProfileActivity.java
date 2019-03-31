package com.example.bookeep;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
 * Displays a users profile, will allow editing that profile if it is the current user's
 * Needs to be passed the user profile to be displayed in the intent through uuid
 * @author Nolan Brost
 * @see User
 * @version 1.0.1
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
    private DatabaseReference lenderRef;
    private DatabaseReference borrowerRef;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

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
        Button button = findViewById(R.id.button4);
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
        lenderRef = database.getReference("lenderRatings").child(userId);
        borrowerRef = database.getReference("borrowerRatings").child(userId);
        myRef = database.getReference("users").child(userId);

        lenderRef.addValueEventListener(new ValueEventListener() {                      //updates the lender review info
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Rating rating = dataSnapshot.getValue(LenderRating.class);
                if (rating == null){
                    rating = new LenderRating(userId);
                } else {
                    rating.recalculateRating();
                }
                Float overallRating = rating.getRating();
                int numRatings = rating.getNumRatings();
                lenderRatingBar.setRating(overallRating);
                String numRatingString = numRatings + " Lender Reviews";
                numLenderReveiewsView.setText(numRatingString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        borrowerRef.addValueEventListener(new ValueEventListener() {                    //updates the borrower review info
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Rating rating = dataSnapshot.getValue(BorrowerRating.class);
                if (rating == null){
                    rating = new BorrowerRating(userId);
                } else {
                    rating.recalculateRating();
                }
                Float overallRating = rating.getRating();
                int numRatings = rating.getNumRatings();
                borrowerRatingBar.setRating(overallRating);
                String numRatingString = numRatings + " Borrower Reviews";
                numBorrowerReviewsView.setText(numRatingString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String userFirstname = user.getFirstname();
                String userLastname = user.getLastname();
                String username = user.getUserName();
                PhoneNumber userPhoneNumber = user.getPhoneNumber();
                String userEmail = user.getEmail();
                if (userFirstname != null && userLastname != null) {
                    String name = userFirstname + " " + userLastname;
                    nameView.setText(name);
                }
                if (username != null) {
                    usernameView.setText(username);
                }
                if (userPhoneNumber != null) {
                    phoneNumberView.setText(userPhoneNumber.toString());
                }
                if (userEmail != null) {
                    emailAddressView.setText(userEmail);
                }
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

    public void onPopup(View view){
        Intent intent = new Intent(this, RatingPopupActivity.class);
        intent.putExtra("uuid",userId);
        intent.putExtra("lender", true);
        startActivity(intent);
    }

}
