package com.example.bookeep;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestActivity2 extends AppCompatActivity {
    Book book;
    User user;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

    }

    public void getLocationClick(View view) {
        databaseReference.child("books").child("c9d0edcc-f885-4789-ae4c-e343d57289c7").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                book = dataSnapshot.getValue(Book.class);
                Intent intent = new Intent(TestActivity2.this, GetLocationActivity.class);
                intent.putExtra("Book", book);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setLocationClick(View view) {
        databaseReference.child("books").child("c9d0edcc-f885-4789-ae4c-e343d57289c7").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                book = dataSnapshot.getValue(Book.class);
                databaseReference.child("users").child(book.getCurrentBorrowerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        Intent intent = new Intent(TestActivity2.this, SetLocationActivity.class);
                        intent.putExtra("Book", book);
                        intent.putExtra("User", user);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


