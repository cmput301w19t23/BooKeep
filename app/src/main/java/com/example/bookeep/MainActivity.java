package com.example.bookeep;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.example.bookeep.Fragments.ShelfFragment;
import com.example.bookeep.Fragments.StandFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = StandFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

//        FloatingActionButton addBook = (FloatingActionButton) findViewById(R.id.addBook);
//        addBook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, AddEditBookActivity.class);
//                startActivity(intent);
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
            email.setText(R.string.not_signed_in);
        }


        /*DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        User user = new User("nafee@ualberta.ca", "Nafee", "Khan");
        Address address = new Address();
        address.setCity("Edmonton");
        address.setProvince("Alberta");
        address.setStreetAddress("10959 102St NW");
        address.setZipCode("T5H2V1");
        user.setAddress(address);
        PhoneNumber phoneNumber = new PhoneNumber(587,938,3713);
        user.setPhoneNumber(phoneNumber);
        mDatabase.child("users").child(user.getUserId().toString()).setValue(user);
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
            book.setOwner(user);
            mDatabase.child("books").child(book.getBookId().toString()).setValue(book);
            user.setFirstname("Nafi");
            mDatabase.child("users").child(user.getUserId().toString()).setValue(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        //new IntentIntegrator(this).initiateScan();

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_stand) {
            fragmentClass = StandFragment.class;
        } else {
            fragmentClass = ShelfFragment.class;
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

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            //Do what you need to do
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }


    }
}



    /*
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                txtView = findViewById(R.id.text1);
                if(isNetworkAvailable()){
                    //GoogleApiRequest.execute("");
                    GoogleApiRequest googleApiRequest = new GoogleApiRequest();
                    try {
                        obj = (JSONObject) googleApiRequest.execute(result.getContents()).get();
                        //txtView.setText(obj.toString());
                        JSONArray jsonArray = (JSONArray) obj.getJSONArray("items");
                        JSONObject item1 = jsonArray.getJSONObject(0);
                        JSONObject volumeInfo = item1.getJSONObject("volumeInfo");
                        String author = volumeInfo.getJSONArray("authors").getString(0);
                        txtView.setText(author);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/
//public void onActivityResult
