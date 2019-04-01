package com.example.bookeep;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;

/**
 * Searches books available
 * @author Kyle Fujishige, Nafee Khan, Jeff Kirker
 * @version 1.0.1
 * @see Book
 * @see User
 * @see SearchKeyWordsFragment
 * @see SearchActivity
 */
public class SearchFragment extends Fragment {

    private boolean isResumed;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    ArrayList<Book> BookList;
    SearchRecyclerViewAdapter adapter;

    // TODO: Customize parameters
    private OnListFragmentInteractionListener mListener;

    public SearchFragment() {}

    /**
     * creates a new fragment
     * @return returns a new search fragment
     */
    public static SearchFragment newInstance () {
        SearchFragment fragment = new SearchFragment();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * creates the view for the fragment
     * @param inflater layoutinflator
     * @param container viewgroup
     * @param savedInstanceState bundle
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        BookList = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_search_list,container,false);
        Context context = view.getContext();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.search_recycler_view);

        adapter = new SearchRecyclerViewAdapter(BookList, mListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));



        databaseReference = database.getReference("books/");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {
                Book book = dataSnapshot.getValue(Book.class);
                if ((book.getStatus().equals(BookStatus.AVAILABLE) ||
                        book.getStatus().equals(BookStatus.REQUESTED)) && !book.getOwner().equals(firebaseUser.getUid())) {
                    BookList.add(book);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onListFragmentInteraction(Book item);
    }

    @Override
    public void onResume() {
        isResumed = true;
        super.onResume();
    }

    @Override
    public void onPause(){
        isResumed = false;
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
