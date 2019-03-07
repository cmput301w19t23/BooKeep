package com.example.bookeep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BookDetailsActivity extends AppCompatActivity implements BookDetailsFragment.OnFragmentInteractionListener, RequestsOnBookFragment.OnListFragmentInteractionListener {

    private Menu menu;
    private Book book;
    private User currentUser;
    private String currentUserId;//= "nafee1";
    private FireBaseController fireBaseController = new FireBaseController(this);
    private ImageView bookImage;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private List<Request> requests;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details2);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        //toolbar.setTitle("Title");
        setSupportActionBar(toolbar);
        //FirebaseDatabase.getInstance().getReference().child("blah").setValue("gh");


        Intent received = getIntent();
        final String bookId = received.getStringExtra("Book ID");

///TEST



        //PhoneNumber phoneNumber = new PhoneNumber("587", "938", "3713");
        //String email = "name@yahoo.com";
        //String password = "123456";
        //String first = " Nafee";
        //String last = "Khan";
        //fireBaseController.createNewUser(email,password,first,last,phoneNumber);


/*
        //currentUser = fireBaseController.getCurrentUser();
        currentUser = (User) getIntent().getSerializableExtra("User");
        book = new Book();
        book.setTitle("Harry Potter and the Deathly Hallows");
        //ArrayList<String> authors = new ArrayList<>();
        //authors.add("J. K. Rowling");

        book.setAuthor("J.K.Rowling");
        book.setStatus(BookStatus.AVAILABLE);
        book.setOwner(currentUser.getUserId());
        //book.setBookId("book1");
        book.setDescription("The magnificent final book in J. K. Rowling's seven-part saga comes to readers July 21, 2007. You'll find out July 21!");
        //ok.setISBN(9780545010221);
        databaseReference.child("books").child(book.getBookId()).setValue(book);









//TEST
*/
        //book = fireBaseController.getBookByBookId(book.getBookId());

        databaseReference.child("books").child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //book[0] = dataSnapshot.getValue(Book.class);
                //books.add(dataSnapshot.getValue(Book.class));
                //if(dataSnapshot.getValue(Book.class).getBookId().equals(bookId)) {
                book = dataSnapshot.getValue(Book.class);
                //return book;
                toolbar.setTitle(book.getTitle());
                toolbarLayout.setTitle(book.getTitle());
                currentUserId = firebaseUser.getUid();
            databaseReference.child("users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //if(dataSnapshot.getValue(User.class).getUserId().equals(currentUserId)) {
                    currentUser = dataSnapshot.getValue(User.class);
                    bookImage = (ImageView) findViewById(R.id.book_image);
                    DownloadImageTask downloadImageTask = new DownloadImageTask();
                    try {
                        Bitmap bitmap = downloadImageTask.execute("http://books.google.com/books/content?id=H8sdBgAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api").get();
                        bookImage.setImageBitmap(bitmap);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Below: code for if user IS book owner:
                    if (currentUserId.equals(book.getOwner())) {
                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        //edit book button for owner
                        fab.setImageResource(R.drawable.pencil);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                                Intent intent = new Intent(BookDetailsActivity.this, AddEditBookActivity.class);
                                intent.putExtra("Book to edit", book);
                                startActivity(intent);

                            }
                        });

                    } else {

                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        //fab.setEnabled(false);
                        //fab.setVisibility(View.GONE);
                        // add request button for borrower
                        fab.setImageResource(R.drawable.ic_add);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (book.getStatus().equals(BookStatus.AVAILABLE)) {

                                    //fireBaseController.addRequestToBookByBookId(book.getBookId());
                                    if(book.getStatus().equals(BookStatus.AVAILABLE) || book.getStatus().equals(BookStatus.REQUESTED)) {

                                        book.addRequest(currentUserId);
                                        book.setStatus(BookStatus.REQUESTED);
                                        databaseReference.child("books").child(book.getBookId()).setValue(book);

                                    }

                                }
                            }
                        });


                    }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_book_details, menu);
        //hideOption(R.id.action_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

            }
            else if (id == R.id.action_book_details) {
                fragment = BookDetailsFragment.newInstance(book, currentUser);
                //return true;
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.book_details_fragment_container, fragment).commit();

            //return true;

        } else {//noinspection SimplifiableIfStatement
            if (id == R.id.action_edit) {
                return true;
            } else if (id == R.id.action_requesters) {
                return true;
            } else if (id == R.id.action_book_details) {
                return true;
            }

            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBookUpdate(Book book) {
        this.book = book;
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarLayout.setTitle(book.getTitle());
        toolbar.setTitle(book.getTitle());
    }

    //@Override
    public void onListFragmentInteraction(Request item) {

    }

    @Override
    public void onListFragmentInteraction(User item) {

        Intent intent = new Intent(BookDetailsActivity.this, UserProfileActivity.class);
        intent.putExtra("User", item);
        startActivity(intent);

    }
/*
    public Book getBook() {
        return this.book;
    }*/


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

}


