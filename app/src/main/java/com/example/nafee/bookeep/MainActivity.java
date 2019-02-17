package com.example.nafee.bookeep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        User user = new User("nafee@ualberta.ca", "Nafee", "Khan");
        Address address = new Address();
        address.setCity("Edmonton");
        address.setProvince("Alberta");
        address.setStreetAddress("10959 102St NW");
        address.setZipCode("T5H2V1");
        user.setAddress(address);
        mDatabase.child("users").child(user.getUserId().toString()).setValue(user);

    }
}
