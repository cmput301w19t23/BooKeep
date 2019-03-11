package com.example.bookeep.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookeep.AddEditBookActivity;
import com.example.bookeep.Book;
import com.example.bookeep.MainActivity;
import com.example.bookeep.R;
import com.example.bookeep.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A fragment representing a list of Items.
 *
 * @author Jeff Kirker
 */
public class StandFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private String currentUserID;

    ArrayList<Book> BookList = new ArrayList<>();
    MyStandRecyclerViewAdapter adapter;

    /** This is a ChildEventListener for firebase that listens for any changes to the user-books
     * child and updates the owned book list in real time.
     */
    private ChildEventListener updateListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Book newBook = dataSnapshot.getValue(Book.class);
            BookList.add(newBook);
            adapter.notifyDataSetChanged();
            Log.d("BookListSize1: ", Integer.toString(BookList.size()));
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Book changedBook = dataSnapshot.getValue(Book.class);

            for(int i = 0; i < BookList.size(); i++){

                if(BookList.get(i).getBookId().equals(changedBook.getBookId())){

                    BookList.remove(i);
                    BookList.add(changedBook);
                    adapter.notifyDataSetChanged();

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

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StandFragment() {}

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static StandFragment newInstance(int columnCount) {
        StandFragment fragment = new StandFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setToolBar("My Owned Books");
        currentUserID = firebaseUser.getUid();
        BookList = new ArrayList<>();

        //Inflate the view and create recyclerView
        View view = inflater.inflate(R.layout.fragment_stand_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.stand_recycler_view);

        // Set the adapter
        adapter = new MyStandRecyclerViewAdapter(BookList, mListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        //Set grid layout for cards
        Context context = view.getContext();
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        // FAB to add more books
        FloatingActionButton addBook = (FloatingActionButton) view.findViewById(R.id.addBook);
        addBook.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddEditBookActivity.class);
                /** TODO: Get rid of entire startActivityForResult chain
                 *
                 */
                startActivity(intent);
            }

        });

        databaseReference.child("user-books").child(currentUserID).addChildEventListener(updateListener);

        return view;
    }

    /**
     * Depreciated method, but necessary until we can solve the TODOs
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 23) {
            if (resultCode == RESULT_OK) {
                /**TODO: Get rid of the entire startActivityForResult chain
                 *
                 */
            }
        }
    }

    /**
     * Connects the stand to our MainActivity so books are clickable
     * @param context
     */
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

    /**
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onListFragmentInteraction(Book item);
    }
}
