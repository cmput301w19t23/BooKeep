package com.example.bookeep;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.View;
import android.widget.TimePicker;

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
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private Book book;
    private User user;

    private GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 32;
    private Marker marker;
    private Location location;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_set_location);
        marker = null;
        if (intent.getExtras() != null) {
            book = (Book) intent.getSerializableExtra("Book");
            user = (User) intent.getSerializableExtra("User");

        }
        calendar = Calendar.getInstance();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                location = new Location("PressLocationProvider");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                location.setAccuracy(100);
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
        Boolean pass = Boolean.TRUE;
        if (user.getUserId() == book.getOwner()) {
            book.setBorrowLocation(location);
            book.setReturnLocation(null);
        } else if (user.getUserId() == book.getCurrentBorrowerId()) {
            book.setReturnLocation(location);
            book.setBorrowLocation(null);
        } else {
            pass = Boolean.FALSE;
        }

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");

        book.setCalendarDate(calendar);


        databaseReference.child("books").child(book.getBookId()).setValue(book);
        finish();

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
    }

    @Override
    public void getDate(int year,int month,int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
    }
}
