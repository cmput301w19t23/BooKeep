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
import com.example.bookeep.R;
import com.example.bookeep.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class StandFragment extends Fragment {

    private boolean isResumed;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private String currentUserID;
    private User currentUser;

    ArrayList<Book> BookList = new ArrayList<>();
    MyStandRecyclerViewAdapter adapter;


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

    /*OLD DTSRUCTURE
    private ChildEventListener updateListener = new ChildEventListener() {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            User changedUser = dataSnapshot.getValue(User.class);

            if(changedUser.getUserId().equals(currentUserID)){

                if(changedUser != null) {

                    currentUser = changedUser;

                    if(isResumed){

                        ArrayList<String> booksOwnedIds = currentUser.getOwnedIds();
                        BookList.clear();
                        adapter.notifyDataSetChanged();
                        for(String bookId: booksOwnedIds){
                            databaseReference.child("books").child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    BookList.add(dataSnapshot.getValue(Book.class));
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });

                        }

                    }

                }

            }

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };*/
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StandFragment() {

    }

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

        // FAB to add more buttons
        FloatingActionButton addBook = (FloatingActionButton) view.findViewById(R.id.addBook);
        addBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddEditBookActivity.class);
                /** TODO: Get rid of entire startActivityForResult chain
                 *
                 */
                //startActivityForResult(intent, 23);
                startActivity(intent);
                //getActivity().finish();
            }
        });

        databaseReference.child("user-books").child(currentUserID).addChildEventListener(updateListener);


/*OLD STRUCTURE
        databaseReference.child("users").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentUser = dataSnapshot.getValue(User.class);
                ArrayList<String> booksOwnedIds = currentUser.getOwnedIds();
                BookList.clear();
                adapter.notifyDataSetChanged();
                for(String bookId: booksOwnedIds){
                    databaseReference.child("books").child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            BookList.add(dataSnapshot.getValue(Book.class));
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

                }
                databaseReference.child("users").addChildEventListener(updateListener);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        return view;
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
        public void onListFragmentInteraction(Book item);
    }


//    public ArrayList<Book> getBookList() {
//        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        BookList.clear();
//        databaseReference.child("users").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                User currentUser = dataSnapshot.getValue(User.class);
//                ArrayList<String> ownedIds = currentUser.getOwnedIds();
//                for(int i = 0; i < ownedIds.size(); i++){
//                    databaseReference.child("books").child(ownedIds.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Book book = dataSnapshot.getValue(Book.class);
//                            BookList.add(book);
//                            Log.d("BookListSize1: ", Integer.toString(BookList.size()));
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        Log.d("BookListSize2: ", Integer.toString(BookList.size()));
//        return BookList;
//    }


}
