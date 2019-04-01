package com.example.bookeep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
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
public class SearchUserPopupActivity extends Activity {
    private EditText emailText;
    private ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_user_popup);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

/*        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * 0.9), (int) (height * 0.3));*/
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
        /*myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }
}
