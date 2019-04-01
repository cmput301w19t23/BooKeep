package com.example.bookeep;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bookeep.Fragments.PendingRequestFragment;
import com.example.bookeep.Fragments.ShelfFragment;
import com.example.bookeep.Fragments.StandFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

import static com.example.bookeep.NotificationHandler.CHANNEL_1_ID;
import static com.example.bookeep.NotificationHandler.CHANNEL_2_ID;

/**
 * Main activity of the app, users can navigate to all use cases from here
 * @author Nafee Khan, Nolan Brost, Jeff Kirker, Dusan Krstic, Hugh Bagan, Kyle Fujishige
 * @version 1.0.1
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FirebaseAuth.AuthStateListener,
        StandFragment.OnListFragmentInteractionListener,
        PendingRequestFragment.OnListFragmentInteractionListener,
        ShelfFragment.OnListFragmentInteractionListener{

    private NotificationManagerCompat notificationManager;

    Menu menu;
    Class fragmentClass = null;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private String currentUserID = firebaseUser.getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();



    // This childEventListener listens for changes to the user's books and
    // pushes notifications if a book is requested.
    private ChildEventListener requestListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Book book = dataSnapshot.getValue(Book.class);
            Log.d("MainActivity", "onChildChanged reached");
            if(book.getOwner().equals(currentUserID)){
                if(book.getNewRequest()){
                    Log.d("MainActivity", "Notification sent");
                    sendOnChannel1(book);
                }
            }
        }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };

    private ChildEventListener acceptedListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Book book = dataSnapshot.getValue(Book.class);
            if(book.getNewAccepted()){
                sendOnChannel2(book);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String shelfFragment = getIntent().getStringExtra("shelfFragment");

        databaseReference.child("user-books").child(currentUserID)
                .addChildEventListener(requestListener);
        databaseReference.child("user-borrowed").child(currentUserID)
                .addChildEventListener(acceptedListener);


        notificationManager = NotificationManagerCompat.from(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment = null;



        if(shelfFragment != null){
            fragmentClass = ShelfFragment.class;
        } else {
            fragmentClass = StandFragment.class;
        }

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
                    ImageButton imageButton = headerView.findViewById(R.id.UserProfileButton);
                    String nameString = user.getFirstname() + " " + user.getLastname();
                    String emailString = user.getEmail();
                    nameView.setText(nameString);
                    emailView.setText(emailString);
                    DownloadImageTask downloadImageTask = new DownloadImageTask();
                    try {

                        Bitmap bitmap = downloadImageTask.execute(user.getImageURL()).get();
                        imageButton.setImageBitmap(bitmap);

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_search_black_24dp));
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final String userId = firebaseUser.getUid();
        ImageButton updateProfile = findViewById(R.id.UserProfileButton);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                intent.putExtra("uuid",userId);
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
        } else if(id == R.id.nav_shelf){
            fragmentClass = ShelfFragment.class;
        } else if(id == R.id.nav_pending){
            fragmentClass = PendingRequestFragment.class;
        } else if(id == R.id.search_users){
            Intent intent = new Intent(this,SearchUserPopupActivity.class);
            startActivity(intent);
            return true;
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
        if(item.getOwner().equals(currentUserID)){
            item.clearNewRequest();
            databaseReference.child("user-books").child(currentUserID).child(item.getBookId()).setValue(item);
            databaseReference.child("books").child(item.getBookId()).setValue(item);
        }
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


    /**
     * This is the notification for when a book is requested.
     * @param book
     */
    public void sendOnChannel1(Book book){

        book.clearNewRequest();
        databaseReference.child("books").child(book.getBookId()).setValue(book);
        databaseReference.child("user-books").child(book.getOwner()).child(book.getBookId()).setValue(book);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("New Request!")
                .setContentText("New request to borrow '" + book.getTitle() + "'")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }

    /**
     * This is the notification for when a book request is accepted.
     * @param book
     */
    public void sendOnChannel2(Book book){


        book.clearNewAccepted();
        databaseReference.child("books").child(book.getBookId()).setValue(book);
        databaseReference.child("user-books").child(book.getOwner()).child(book.getBookId()).setValue(book);
        databaseReference.child("user-borrowed").child(currentUserID).child(book.getBookId()).setValue(book);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Request Accepted!")
                .setContentText("Your request to borrow '" + book.getTitle() + "' has been accepted")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(2, notification);
    }

}