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

    FragmentManager fragmentManager = getSupportFragmentManager();
    BookDetailsFragment fragment = null;
    RequestsOnBookFragment requestsFragment = null;
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
                // Launch BookDetailsFragment

                try {
                    fragment = (BookDetailsFragment) BookDetailsFragment.newInstance(book);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fragmentManager.beginTransaction().replace(R.id.book_details_fragment_container, fragment).commit();

                if(book.getOwner().equals(currentUserId)){
                    try {
                        requestsFragment = RequestsOnBookFragment.newInstance(book);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fragmentManager.beginTransaction().replace(R.id.requests_fragment_container, requestsFragment).commit();
                }
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
                        Intent intent = new Intent(BookDetailsActivity.this,RatingPopupActivity.class);
                        intent.putExtra("uuid",book.getCurrentBorrowerId());
                        intent.putExtra("lender",false);
                        startActivity(intent);
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
                        Intent intent = new Intent(BookDetailsActivity.this,RatingPopupActivity.class);
                        intent.putExtra("uuid",book.getOwner());
                        intent.putExtra("lender",true);
                        startActivity(intent);

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
}


