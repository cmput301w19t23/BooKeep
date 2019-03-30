package com.example.bookeep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    public BookDetailsActivity() {
    }

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
        setSupportActionBar(toolbar);

        Intent received = getIntent();
        final String bookId = received.getStringExtra("Book ID");

        databaseReference.child("books").child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                book = dataSnapshot.getValue(Book.class);

                toolbar.setTitle(book.getTitle());
                toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                currentUserId = firebaseUser.getUid();

                databaseReference.child("users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentUser = dataSnapshot.getValue(User.class);

                        // Launch BookDetailsFragment
                        BookDetailsFragment fragment = null;
                        RequestsOnBookFragment requestsFragment = null;
                        try {
                            fragment = (BookDetailsFragment) BookDetailsFragment.newInstance(book, currentUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            requestsFragment = RequestsOnBookFragment.newInstance(book);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.book_details_fragment_container, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.requests_fragment_container, requestsFragment).commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

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
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                if(book.getISBN().equals(result.getContents())){

                    if(book.getCurrentBorrowerId().equals(currentUserId) && book.getStatus().equals(BookStatus.ACCEPTED)){
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

    /** TODO: Get rid of this options menu.
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }

    /** TODO: Get rid of this too.
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

    /** TODO: Get rid of this too
     * hides an option
     * @param id id of option to be hid
     */
    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    /** TODO: Get rid of this too
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
     */

    /** TODO: Redundant.
     * downloads the image task
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

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


