package com.example.nafee.bookeep;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private JSONObject obj;
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
        PhoneNumber phoneNumber = new PhoneNumber(587,938,3713);
        user.setPhoneNumber(phoneNumber);
        mDatabase.child("users").child("nafee").setValue(user);
        if(isNetworkAvailable()){
            //GoogleApiRequest.execute("");
            GoogleApiRequest googleApiRequest = new GoogleApiRequest();
            try {
                obj = (JSONObject) googleApiRequest.execute("9780545010221").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        try {
            JSONArray jsonArray = (JSONArray) obj.getJSONArray("items");
            JSONObject item1 = jsonArray.getJSONObject(0);
            JSONObject volumeInfo = item1.getJSONObject("volumeInfo");
            String title = volumeInfo.getString("title");
            String description = volumeInfo.getString("description");
            Book book = new Book();
            book.setTitle(title);
            book.setDescription(description);
            //book.setOwner(user);
            //book.setISBN(9780545010221);
            mDatabase.child("books").child(book.getBookId().toString()).setValue(book);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
