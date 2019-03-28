package com.example.bookeep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class SetLocationActivity extends FragmentActivity implements OnMapReadyCallback,
        TimePickerFragment.OnTimeSelectedListener,
        DatePickerFragment.OnDateSelectedListener {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private Book book;
    private User user;

    private GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 32;
    private Marker marker;
    private LatLng location;
    static Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_set_location);
        marker = null;
        book = (Book) intent.getSerializableExtra("Book");
        user = (User) intent.getSerializableExtra("User");
        if (book.getCurrentBorrowerId() != user.getUserId() && book.getOwner() != user.getUserId()) {
            finish();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //This chunk of code was used to test books directly from firebase. Call this activity with
        //Intent.putExtra("Book, book) and Intent.putExtra("User", user).

        /*databaseReference.child("books").child("5e5d0a95-f1ee-4503-9f2b-f35017cbdc90").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                book = dataSnapshot.getValue(Book.class);
                databaseReference.child("users").child(book.getOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        if (user.getUserId() != book.getOwner() && user.getUserId() != book.getCurrentBorrowerId()) {
                            finish();
                        }
                        calendar = Calendar.getInstance();
                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(SetLocationActivity.this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        LatLngBounds EDMONTON = new LatLngBounds(
                new LatLng(53,-114),
                new LatLng(54,-113)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EDMONTON.getCenter(),10));
        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                }
                location = latLng;
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Meet Here"));
                View view = findViewById(R.id.set_location_menu);
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * asks for location persmission
     * see https://developer.android.com/training/permissions/requesting#java
     *
     * @param requestCode int
     * @param permissions Sting[]
     * @param grantResults int[]
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                } else {
                    mMap.setMyLocationEnabled(false);
                }
        }
    }

    public void onSaveButtonPressed(View view) {
        if (user.getUserId() == book.getOwner()) {
            book.setBorrowLocation(location.toString());
            book.setReturnLocation(null);
        } else if (user.getUserId() == book.getCurrentBorrowerId()) {
            book.setReturnLocation(location.toString());
            book.setBorrowLocation(null);
        } else {
            finish();
        }

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setOnDateSelectedListener(this);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void onClearButtonPressed(View view) {
        marker.remove();
        View view2 = findViewById(R.id.set_location_menu);
        view2.setVisibility(View.GONE);
    }


    @Override
    public void getTime(int hour,int minute) {
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        book.setCalendarDate(calendar.getTime().toString());

        databaseReference.child("books").child(book.getBookId()).setValue(book);
        finish();
    }

    @Override
    public void getDate(int year,int month,int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setOnTimeSelectedListener(this);
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }
}
