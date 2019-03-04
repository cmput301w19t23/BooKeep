package com.example.bookeep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.bookeep.Fragments.shelfFragment;

public class BookDetailsActivity extends AppCompatActivity implements BookDetailsFragment.OnFragmentInteractionListener, RequestsOnBookFragment.OnFragmentInteractionListener{

    private Menu menu;
    private Book book;
    private User currentUser;
    private String currentUserId;//= "nafee1";
    private FireBaseController fireBaseController = new FireBaseController(this);
    private ImageView bookImage;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Title");
        setSupportActionBar(toolbar);

        Intent received = getIntent();
        String bookId = received.getStringExtra("Bookd ID");

        book = fireBaseController.getBookByBookId(bookId);
        currentUser = fireBaseController.getCurrentUser();
        currentUserId = fireBaseController.getCurrentUserId();
        toolbar.setTitle(book.getTitle());
        bookImage = (ImageView) findViewById(R.id.book_image);

        /*book = new Book();
        book.setTitle("book1");
        book.setOwner("nafee");
        currentUser = new User();
        currentUser.setUserId("nafee");*/
        // Below: code for if user IS book owner:
        if (currentUserId.equals(book.getOwner())){
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

        } else {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setEnabled(false);
            fab.setVisibility(View.GONE);
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
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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
                return true;
            } else if (id == R.id.action_requesters) {
                //return true;
                fragment = RequestsOnBookFragment.newInstance(book, currentUser);
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
/*
    public Book getBook() {
        return this.book;
    }*/

}

