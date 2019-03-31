package com.example.bookeep;

import android.content.Intent;
import android.graphics.Bitmap;
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

import java.util.concurrent.ExecutionException;

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
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private User user;

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                String name = user.getFirstname() + " " + user.getLastname();
                nameView.setText(name);
                String username = user.getUserName();
                if (username.length() > 22) {
                    username = username.replace(" ","\n");
                }
                usernameView.setText(username);
                phoneNumberView.setText(user.getPhoneNumber().toString());
                emailAddressView.setText(user.getEmail());
                DownloadImageTask downloadImageTask = new DownloadImageTask();
                try {
                    Bitmap bitmap = downloadImageTask.execute(user.getImageURL()).get();
                    profilePicture.setImageBitmap(bitmap);

                } catch (ExecutionException e) {
                    profilePicture.setImageResource(R.drawable.profile_pic);
                } catch (InterruptedException e) {
                    profilePicture.setImageResource(R.drawable.profile_pic);
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

    public void onImageClick(View view) {
        Intent intent = new Intent(this, ImageViewActivity.class);
        intent.putExtra("image", user.getImageURL());
        startActivity(intent);
    }
}
