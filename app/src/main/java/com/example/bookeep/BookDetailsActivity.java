package com.example.bookeep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.bookeep.AddEditBookActivity.MY_PERMISSIONS_REQUEST_CAMERA;

/**
 * Displays the books details when a book is clicked
 * @author Nafee Khan, Jeff Kirker
 * @see User
 * @see Book
 * @see BookStatus
 * @version 1.0.1
 */
public class BookDetailsActivity extends AppCompatActivity implements BookDetailsFragment.OnFragmentInteractionListener, RequestsOnBookFragment.OnListFragmentInteractionListener {

    private Menu menu;
    private Book book;
    private User currentUser;
    private String currentUserId;
    private FireBaseController fireBaseController = new FireBaseController(this);
    private ImageView bookImage;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private List<Request> requests;

    private boolean isRequested;
    private boolean isBorrowed;

    /**
     * displays the book and its info when selected
     * @param savedInstanceState Bundled saved instance state
     * @see Book
     * @see User
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details2);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        final CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        //toolbar.setTitle("Title");
        setSupportActionBar(toolbar);
        //FirebaseDatabase.getInstance().getReference().child("blah").setValue("gh");


        Intent received = getIntent();
        final String bookId = received.getStringExtra("Book ID");

        databaseReference.child("books").child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //book[0] = dataSnapshot.getValue(Book.class);
                //books.add(dataSnapshot.getValue(Book.class));
                //if(dataSnapshot.getValue(Book.class).getBookId().equals(bookId)) {
                book = dataSnapshot.getValue(Book.class);
                //return book;

                toolbar.setTitle(book.getTitle());
                toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
//                toolbarLayout.setTitle(book.getTitle());
                currentUserId = firebaseUser.getUid();

                databaseReference.child("users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //if(dataSnapshot.getValue(User.class).getUserId().equals(currentUserId)) {
                        currentUser = dataSnapshot.getValue(User.class);
                        // Below: code for if user IS book owner:
                        BookDetailsFragment fragment = null;
                        //Class fragmentClass = null;
                        //fragmentClass = BookDetailsFragment.class;
                        try {
                            fragment = (BookDetailsFragment) BookDetailsFragment.newInstance(book, currentUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.book_details_fragment_container, fragment).commit();
                        //}

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

    /**
     * Process scanning and add the book to Firebase.
     * @param requestCode the code for the activity request
     * @param resultCode the code for the activity result
     * @param data Intent data for scanning
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {

            if(result.getContents() == null) {

                //Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            } else {

                //Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                if(book.getISBN().equals(result.getContents())){

                    if(book.getCurrentBorrowerId().equals(currentUserId) && book.getStatus().equals(BookStatus.ACCEPTED)){
                        /*
                        databaseReference.child("books").child(book.getBookId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                book =
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/

                        book.setStatus(BookStatus.BORROWED);
                        book.endTransaction();
                        databaseReference.child("books").child(book.getBookId()).setValue(book);
                        databaseReference.child("user-books").child(book.getOwner()).child(book.getBookId()).setValue(book);
                        databaseReference.child("user-borrowed").child(currentUserId).child(book.getBookId()).setValue(book);

                    } else if (book.getOwner().equals(currentUserId) && book.getStatus().equals(BookStatus.ACCEPTED)){

                        book.startTransaction();
                        databaseReference.child("books").child(book.getBookId()).setValue(book);
                        databaseReference.child("user-books").child(book.getOwner()).child(book.getBookId()).setValue(book);
                        databaseReference.child("user-borrowed").child(book.getCurrentBorrowerId()).child(book.getBookId()).setValue(book);

                    } else if (book.getOwner().equals(currentUserId) && book.getStatus().equals(BookStatus.BORROWED)){

                        //end of transaction returning book
                        book.setStatus(BookStatus.AVAILABLE);
                        book.endTransaction();
                        databaseReference.child("users").child(book.getCurrentBorrowerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User formerBorrower = dataSnapshot.getValue(User.class);
                                formerBorrower.removeFromBorrowed(book.getBookId());
                                databaseReference.child("users").child(formerBorrower.getUserId()).setValue(formerBorrower);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        databaseReference.child("user-borrowed").child(book.getCurrentBorrowerId()).child(book.getBookId()).removeValue();
                        book.setCurrentBorrower(null);
                        databaseReference.child("books").child(book.getBookId()).setValue(book);
                        databaseReference.child("user-books").child(book.getOwner()).child(book.getBookId()).setValue(book);


                    } else if (book.getCurrentBorrowerId().equals(currentUserId) && book.getStatus().equals(BookStatus.BORROWED)){

                        book.startTransaction();
                        databaseReference.child("books").child(book.getBookId()).setValue(book);
                        databaseReference.child("user-books").child(book.getOwner()).child(book.getBookId()).setValue(book);
                        databaseReference.child("user-borrowed").child(book.getCurrentBorrowerId()).child(book.getBookId()).setValue(book);

                    }

                }

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * creates the options menu
     * @param menu menu to be created
     * @return boolean of if menu is displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Intent received = getIntent();
        this.menu = menu;
        final Menu finalMenu = menu;
        final String bookId = received.getStringExtra("Book ID");

        databaseReference.child("books").child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                book = dataSnapshot.getValue(Book.class);
                if (currentUserId.equals(book.getOwner())) {
                    getMenuInflater().inflate(R.menu.menu_book_details, finalMenu);
                } else {
                    getMenuInflater().inflate(R.menu.menu_book_details_non_owner, finalMenu);
                }
                //hideOption(R.id.action_edit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     * @param item item in menu
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        if (currentUserId.equals(book.getOwner())){

            //BookDetailsFragment bookDetailsFragment;
            //RequestsOnBookFragment requestsOnBookFragment;

            if (id == R.id.action_edit) {

                Intent intent = new Intent(BookDetailsActivity.this, AddEditBookActivity.class);
                intent.putExtra("Book to edit", book);
                startActivity(intent);

            } else if (id == R.id.action_requesters) {
                //return true;
                fragment = RequestsOnBookFragment.newInstance(book);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.book_details_fragment_container, fragment).commit();
            }
            else if (id == R.id.action_book_details) {
                fragment = BookDetailsFragment.newInstance(book, currentUser);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.book_details_fragment_container, fragment).commit();
            }
        } else {//noinspection SimplifiableIfStatement
            if (id == R.id.book_details) {
                fragment = BookDetailsFragment.newInstance(book, currentUser);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.book_details_fragment_container, fragment).commit();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * hides an option
     * @param id id of option to be hid
     */
    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    /**
     * shows an option
     * @param id of option to be shown
     */
    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * updates display when book is updated
     * @param book to be updated
     */
    @Override
    public void onBookUpdate(Book book) {
        this.book = book;
//        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbarLayout.setTitle(book.getTitle());
        toolbar.setTitle(book.getTitle());
    }

    //@Override
    public void onListFragmentInteraction(Request item) {

    }

    /**
     * @param item user to be passed to intent
     * @see User
     */
    @Override
    public void onListFragmentInteraction(User item) {

        Intent intent = new Intent(BookDetailsActivity.this, UserProfileActivity.class);
        intent.putExtra("User", item);
        startActivity(intent);

    }

    /**
     * goes back to book details when pressed back
     *//*
    @Override
    public void onBackPressed(){

        Intent intent = new Intent(BookDetailsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }*/

    /*
    public Book getBook() {
        return this.book;
    }*/

    /**
     * downloads the image task
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        //ImageView bmImage;
        //public DownloadImageTask(ImageView bmImage) {
        // AddEditBookActivity.this.bookImage = bmImage;
        //}

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;

        }

        protected void onPostExecute(Bitmap result) {

            //bmImage.setImageBitmap(result);

        }

    }

    public void onImageViewClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("image", book.getBookImageURL());
        ClickOnImageFragment fragment = new ClickOnImageFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.book_details_fragment_container, fragment).commit();
    }

}


