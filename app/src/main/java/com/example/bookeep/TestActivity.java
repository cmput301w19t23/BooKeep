package com.example.bookeep;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    FireBaseController fireBaseController = new FireBaseController(this);
    private ArrayList<User> users;
    private TextView text;
    private String dummy = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_test);


        /*DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        User user = new User("xyz@ualberta.ca", "Nafee", "Khan");
        Address address = new Address();
        address.setCity("Edmonton");
        address.setProvince("Alberta");
        address.setStreetAddress("10959 102St NW");
        address.setZipCode("T5H2V1");
        user.setAddress(address);
        PhoneNumber phoneNumber = new PhoneNumber("587","938","3713");
        user.setPhoneNumber(phoneNumber);
        mDatabase.child("users").child(user.getUserId().toString()).setValue(user);*/
        Button button = (Button) findViewById(R.id.button);
        //text = findViewById(R.id.textView2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "xyzwq@ualberta.ca";
                String password = "123456";
                String first= "Nafee";
                String last = "Khan";
                PhoneNumber phoneNumber = new PhoneNumber("587","938","3713");
                fireBaseController.createNewUser(email,password,first,last,phoneNumber);

            }
        });

    }

}
