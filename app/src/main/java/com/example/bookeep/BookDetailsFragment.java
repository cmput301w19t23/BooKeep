package com.example.bookeep;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "book";
    private static final String ARG_PARAM2 = "current user";

    // TODO: Rename and change types of parameters
    private Book mBook;
    private User mUser;

    private OnFragmentInteractionListener mListener;

    private TextView bookTitle;
    private TextView bookAuthors;
    private TextView bookISBN;
    private TextView bookStatus;
    private TextView bookDescription;
    private TextView bookOwner;
    private FireBaseController fireBaseController = new FireBaseController(getActivity());
    private boolean isResumed;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private ChildEventListener updateListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Book changedBook = dataSnapshot.getValue(Book.class);
            if (changedBook.getBookId().equals(mBook.getBookId())) {

                if(changedBook != null) {
                    //mBook = changedBook;//dataSnapshot.getValue(Book.class);
                    if(isResumed) {
                        mBook = changedBook;
                        if (mListener != null) {
                            mListener.onBookUpdate(mBook);
                        }
                        bookTitle = (TextView) getView().findViewById(R.id.book_title);
                        bookAuthors = (TextView) getView().findViewById(R.id.book_authors);
                        bookISBN = (TextView) getView().findViewById(R.id.book_isbn);
                        bookStatus = getView().findViewById(R.id.book_status);
                        bookDescription = getView().findViewById(R.id.book_description);
                        bookOwner = getView().findViewById(R.id.book_owner);

                        bookTitle.setText(mBook.getTitle());
                        //android.support.v7.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                        //CollapsingToolbarLayout toolbarLayout = getActivity().findViewById(R.id.toolbar_layout);
                        //toolbar.setTitle(mBook.getTitle());
                        //toolbarLayout.setTitle(mBook.getTitle());

                        bookAuthors.setText(mBook.getAuthorsString());
//                  bookISBN.setText(mBook.getISBN());
                        bookStatus.setText(mBook.getStatus().toString());
                        bookDescription.setText(mBook.getDescription());
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
    };
   // private final View view;

    //public BookDetailsActivity activity;

    //private Book b;
   // Book book;

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param book Parameter 1.
     * @param currUser Parameter 2.
     * @return A new instance of fragment BookDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookDetailsFragment newInstance(Book book, User currUser) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, book);
        args.putSerializable(ARG_PARAM2, currUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBook = (Book) getArguments().getSerializable(ARG_PARAM1);
            mUser = (User) getArguments().getSerializable(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         final View view = inflater.inflate(R.layout.fragment_book_details, container, false);
        //activity = (BookDetailsActivity) getActivity();
        //b = BookDetailsActivity.book;
        bookTitle = (TextView) view.findViewById(R.id.book_title);
        bookAuthors = (TextView) view.findViewById(R.id.book_authors);
        bookISBN = (TextView) view.findViewById(R.id.book_isbn);
        bookStatus = view.findViewById(R.id.book_status);
        bookDescription = view.findViewById(R.id.book_description);
        bookOwner = view.findViewById(R.id.book_owner);
        //isResumed = true;
        databaseReference.child("books").child(mBook.getBookId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBook = dataSnapshot.getValue(Book.class);
                mListener.onBookUpdate(mBook);

                bookAuthors.setText(mBook.getAuthorsString());
                //bookISBN.setText(mBook.getISBN());
                bookStatus.setText(mBook.getStatus().toString());
                bookDescription.setText(mBook.getDescription());
                bookOwner.setText(mUser.getEmail());
                bookOwner.setTextColor(Color.BLUE);
                bookOwner.setClickable(true);
                bookOwner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        intent.putExtra("Current User", mUser);
                        startActivity(intent);
                    }
                });


                bookTitle.setText(mBook.getTitle());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("books").addChildEventListener(updateListener);
        //isResumed = true;
        //mBook = fireBaseController.onBookChanged()

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {

        //super.onPause();
        databaseReference.removeEventListener(updateListener);
        isResumed = false;
        super.onPause();

    }

    @Override
    public void onResume() {

        //super.onResume();
        isResumed = true;
        super.onResume();


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //public void setBook(Book book) {
//
        //this.book = book;

  //  }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

        public void onBookUpdate(Book book);
    }

}
