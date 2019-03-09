package com.example.bookeep;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;


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

    private ChildEventListener updateListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {

            Book newBook = dataSnapshot.getValue(Book.class);
            BookList.add(newBook);
            adapter.notifyDataSetChanged();
            Log.d("BookListSize1: ", Integer.toString(BookList.size()));

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {

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
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public SearchFragment() {}

    public static SearchFragment newInstance () {
        SearchFragment fragment = new SearchFragment();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        BookList = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_search_list,container,false);
        Context context = view.getContext();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.search_recycler_view);

        adapter = new SearchRecyclerViewAdapter(BookList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        databaseReference.child(BookStatus.AVAILABLE.toString()).addChildEventListener(updateListener);

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
