package com.example.bookeep;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Searches books available
 * @author Kyle Fujishige, Nafee Khan, Jeff Kirker
 * @version 1.0.1
 * @see Book
 * @see User
 * @see SearchKeyWordsFragment
 * @see SearchFragment
 */
public class SearchActivity extends AppCompatActivity implements SearchFragment.OnListFragmentInteractionListener {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Search For Books");
        actionBar.setDisplayHomeAsUpEnabled(true);

        fragment = SearchFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.search_fragment_container, fragment).commit();

    }

    /**
     * creates the options menu
     * @param menu menu to be displayed
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_action));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Bundle bundle = new Bundle();
                bundle.putString("query", s);
                fragment = new SearchKeyWordsFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.search_fragment_container, fragment).commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        return true;
    }

    /**
     * This function supports back navigation.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    /**
     * handles menu items being selected
     * @param item the item selected
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_action) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * handles list fragments interactions
     * @param item book
     */
    @Override
    public void onListFragmentInteraction(Book item) {

        Intent intent = new Intent(SearchActivity.this, BookDetailsActivity.class);
        intent.putExtra("Book ID", item.getBookId());
        startActivity(intent);

    }
}
