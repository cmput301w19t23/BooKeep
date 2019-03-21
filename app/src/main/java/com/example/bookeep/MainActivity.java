package com.example.bookeep;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookeep.Fragments.MyStandRecyclerViewAdapter;
import com.example.bookeep.Fragments.ShelfFragment;
import com.example.bookeep.Fragments.StandFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Main activity of the app, users can navigate to all use cases from here
 * @author Nafee Khan, Nolan Brost, Jeff Kirker, Dusan Krstic, Hugh Bagan, Kyle Fujishige
 * @version 1.0.1
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener, StandFragment.OnListFragmentInteractionListener, ShelfFragment.OnListFragmentInteractionListener{
    private FireBaseController fireBaseController = new FireBaseController(this);

    Menu menu;
    Class fragmentClass = null;

    private int allOwned = 0;
    private int available = 1;
    private int requested = 2;
    private int accepted = 3;
    private int borrowed = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment = null;
        fragmentClass = StandFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Sets a value event listener on the user so any changes to their name/email will be displayed.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        DatabaseReference myRef = database.getReference("users").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null){
                    View headerView = navigationView.getHeaderView(0);
                    TextView nameView = headerView.findViewById(R.id.name);
                    TextView emailView = headerView.findViewById(R.id.email);
                    String nameString = user.getFirstname() + " " + user.getLastname();
                    String emailString = user.getEmail();
                    nameView.setText(nameString);
                    emailView.setText(emailString);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
/*      //sets up on
        // Set the Drawer layout to display the currently logged in User
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Check to make sure user is signed in.
        if (currentUser != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView name = (TextView) headerView.findViewById(R.id.name);
            name.setText(currentUser.getDisplayName()); // This isn't actually set anywhere
            TextView email = (TextView) headerView.findViewById(R.id.email);
            email.setText(currentUser.getEmail());
        } else {
            // User is not signed in: set default text
            View headerView = navigationView.getHeaderView(0);
            TextView name = (TextView) headerView.findViewById(R.id.name);
            name.setText(R.string.app_name);
            TextView email = (TextView) headerView.findViewById(R.id.email);
            email.setText(R.string.not_signed_in);*/

    }

    /**
     * goes back to log in when back is pressed
     */
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        //moveTaskToBack(true);

    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu menu to be opened
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
//        showFilter(true);

        FireBaseController fireBaseController = new FireBaseController(this);
        //User user = fireBaseController.getCurrentUser();
        //userText.setText(user.getFirstName() + " " + user.getLastName());
        ImageButton updateProfile = findViewById(R.id.UserProfileButton);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditUserActivity.class);
                startActivity(intent);
            }
        });
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     * @param item menu item  selected
     * @return super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_button) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }



    /**
     * Handle navigation view item clicks here.
     * @param item menu item clicked
     * @return true
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;

        int id = item.getItemId();

        if (id == R.id.nav_stand) {
            fragmentClass = StandFragment.class;
//            showFilter(true);
        } else {
            fragmentClass = ShelfFragment.class;
//            showFilter(false);
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * //Do what you need to do
     * @param firebaseAuth firebaseauth
     */
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }


    }

    /**
     * displays the book
     * @param item book to be displayed
     */
    @Override
    public void onListFragmentInteraction(Book item) {

        Intent intent = new Intent(MainActivity.this, BookDetailsActivity.class);
        intent.putExtra("Book ID", item.getBookId());
        startActivity(intent);

    }

    /**
     * sets the tool bar
     * @param title of tool bar
     */
    public void setToolBar(String title) {
        getSupportActionBar().setTitle(title);
    }
}