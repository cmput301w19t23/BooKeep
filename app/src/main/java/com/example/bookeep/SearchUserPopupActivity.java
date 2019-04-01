package com.example.bookeep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.w3c.dom.Text;
import java.sql.Time;


/**
 * Search for BooKeep users. Reached via the Navigation drawer
 * @see MainActivity
 */
public class SearchUserPopupActivity extends AppCompatActivity {
    private EditText emailText;
    private ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_user_popup);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        final ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Search For a User");
        actionBar.setDisplayHomeAsUpEnabled(true);

        emailText = findViewById(R.id.emailView);
        searchButton = findViewById(R.id.SearchButton);
    }

    public void searchUser(View view){
        final String userEmail = emailText.getText().toString().trim().toLowerCase();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userDataSnapshot: dataSnapshot.getChildren()){
                    User user = userDataSnapshot.getValue(User.class);
                    if (user != null) {
                        String email = user.getEmail().toLowerCase();
                        if (email.equals(userEmail) || user.getUserName().equals(userEmail)) {
                            Intent intent = new Intent(SearchUserPopupActivity.this, UserProfileActivity.class);
                            intent.putExtra("uuid", user.getUserId());
                            startActivity(intent);
                            finish();
                            return;

                        }

                    }
                }
                String error = "Please Enter an existing email or username";

                emailText.setError(error);
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
}
