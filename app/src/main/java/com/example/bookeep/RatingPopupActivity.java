package com.example.bookeep;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.bookeep.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A pop up that allows rating of a user, needs to be passed the uuid and lender through the intent
 * @author Nolan Brost
 * @see User
 * @see Rating
 * @version 1.0.1
 */
public class RatingPopupActivity extends Activity {
    private RatingBar ratingBar;
    private Button cancel;
    private Button addRating;
    private String uuid;
    private boolean lender; //will be true if lender rating, false if  borrower
    private Rating userRating;
    private TextView rateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_pop_up);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * 0.9), (int)(height * 0.3));
        ratingBar = findViewById(R.id.addRatingBar);
        cancel = findViewById(R.id.rating_cancel);
        addRating = findViewById(R.id.rating_add);
        Intent intent = getIntent();
        rateUser = findViewById(R.id.rate_user_text);



        if (intent != null){
            uuid = intent.getStringExtra("uuid");
            lender = intent.getBooleanExtra("lender",false);
            if (lender){
                String text = "Rate this Lender";
                rateUser.setText(text);
            }
            if (uuid == null){
                return;
            }
        } else{
             return;
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lender){

                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("lenderRatings/" + uuid);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            LenderRating rating = dataSnapshot.getValue(LenderRating.class);
                            if (rating == null){
                                rating = new LenderRating(uuid);
                            }
                            float ratingFloat = ratingBar.getRating();
                            rating.addRating(ratingFloat);
                            ref.child("overallRating").setValue(rating.getRating());
                            ref.child("numberRatings").setValue(rating.getNumRatings());
                            ref.child("reviews").setValue(rating.getReviews());
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else{
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("borrowerRatings/" + uuid);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            BorrowerRating rating = dataSnapshot.getValue(BorrowerRating.class);
                            if (rating == null){
                                rating = new BorrowerRating(uuid);
                            }
                            float ratingFloat = ratingBar.getRating();
                            rating.addRating(ratingFloat);
                            ref.child("overallRating").setValue(rating.getRating());
                            ref.child("numberRatings").setValue(rating.getNumRatings());
                            ref.child("reviews").setValue(rating.getReviews());
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
    }




}
