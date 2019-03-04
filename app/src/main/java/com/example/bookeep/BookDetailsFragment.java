package com.example.bookeep;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
         View view = inflater.inflate(R.layout.fragment_book_details, container, false);
        //activity = (BookDetailsActivity) getActivity();
        //b = BookDetailsActivity.book;
        bookTitle = (TextView) view.findViewById(R.id.book_title);
        bookAuthors = (TextView) view.findViewById(R.id.book_authors);
        bookISBN = (TextView) view.findViewById(R.id.book_isbn);
        bookStatus = view.findViewById(R.id.book_status);
        bookDescription = view.findViewById(R.id.book_description);
        bookOwner = view.findViewById(R.id.book_owner);

        bookAuthors.setText(mBook.getAuthors());
        bookISBN.setText(mBook.getISBN());
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
        void onFragmentInteraction(Uri uri);
    }
}
