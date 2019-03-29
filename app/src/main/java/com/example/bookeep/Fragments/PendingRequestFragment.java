package com.example.bookeep.Fragments;

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

import com.example.bookeep.Book;
import com.example.bookeep.MainActivity;
import com.example.bookeep.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PendingRequestFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


    private OnListFragmentInteractionListener mListener;
    ArrayList<Book> BookList = new ArrayList<Book>();
    MyPendingRequestRecyclerViewAdapter adapter;
    private String currentUserID;

    private ChildEventListener updateListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Boolean added = false;
            Book newBook = dataSnapshot.getValue(Book.class);
            List<String> requesters = newBook.getRequesterIds();
            for(String requesterID : requesters){
               if(requesterID.equals(currentUserID)){
                   BookList.add(newBook);
                   added = true;
               }
            }
            if(!added){
                databaseReference.child("user-requested").child(currentUserID).child(newBook.getBookId()).removeValue();
            }
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Book changedBook = dataSnapshot.getValue(Book.class);

//            for(Book book : BookList){
//                if(changedBook.getBookId().equals(book.getBookId())){
//                    changedBook.
//                }
//            }

            Boolean changed = false;
            for(Book book : BookList) {
                if (book.getBookId().equals(changedBook.getBookId())) {
                    List<String> requesters = changedBook.getRequesterIds();
                    for (String requesterID : requesters) {
                        Log.d("Requester:", requesterID);
                        if (requesterID.equals(currentUserID)) {
                            BookList.remove(book);
                            BookList.add(changedBook);
                            changed = true;
                        }
                    }
                }
            }

            if(!changed){
                for(Book book : BookList) {
                    if (book.getBookId().equals(changedBook.getBookId())) {
                        BookList.remove(book);
                        databaseReference.child("user-requested").child(currentUserID).child(changedBook.getBookId()).removeValue();
                    }
                }
            }
        adapter.notifyDataSetChanged();

        }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Book removedBook = dataSnapshot.getValue(Book.class);
            for(int i = 0; i<BookList.size();i++){
                if(BookList.get(i).getBookId().equals(removedBook.getBookId())){
                    BookList.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PendingRequestFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PendingRequestFragment newInstance(int columnCount) {
        PendingRequestFragment fragment = new PendingRequestFragment();
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
        ((MainActivity) getActivity()).setToolBar("My Pending Requests");
        currentUserID = firebaseUser.getUid();
        BookList = new ArrayList<Book>();

        View view = inflater.inflate(R.layout.fragment_pendingrequest_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.pending_request_recycler_view);

        // Set the adapter
        adapter = new MyPendingRequestRecyclerViewAdapter(BookList, mListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        //Set grid layout for cards
        Context context = view.getContext();
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        databaseReference.child("user-requested").child(currentUserID).addChildEventListener(updateListener);

        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Book item);
    }
}
