package com.example.bookeep;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 * @author Nafee Khan, Jeff Kirker
 * @version 1.0.1
 */
public class RequestsOnBookFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_PARAM_REQ = "Book";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private Book mBook;
    private ArrayList<Request> requests;
    private String bookId;
    private FireBaseController fireBaseController = new FireBaseController(getActivity());
    private ArrayList<User> requesters = new ArrayList<User>();
    private boolean isResumed;
    private RequestsOnBookRecyclerViewAdapter adapter;
    private ChildEventListener updateListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Book changedBook = dataSnapshot.getValue(Book.class);
            if (changedBook.getBookId().equals(mBook.getBookId())){
                if(isResumed) {
                    mBook = changedBook;

                    if (mListener != null) {
                        mListener.onBookUpdate(mBook);
                    }
                    //refresh();

                    ArrayList<String> newRequesterIds = changedBook.getRequesterIds();
                    int size = requesters.size();
                    requesters.clear();
                    //adapter.notifyDataSetChanged();
                    adapter.notifyItemRangeRemoved(0,size);

                    for (String requesterId : newRequesterIds) {
                        databaseReference.child("users").child(requesterId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                requesters.add(dataSnapshot.getValue(User.class));
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


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestsOnBookFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RequestsOnBookFragment newInstance(Book book) {
        RequestsOnBookFragment fragment = new RequestsOnBookFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_REQ, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mBook = (Book) getArguments().getSerializable(ARG_PARAM_REQ);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests_on_book, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.requests_recycler_view);

        bookId = mBook.getBookId();
        adapter = new RequestsOnBookRecyclerViewAdapter(requesters, bookId, mListener, mBook.getRequesterIds());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));

        databaseReference.child("books").child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mBook = dataSnapshot.getValue(Book.class);
                mListener.onBookUpdate(mBook);
                ArrayList<String> requesterIds = mBook.getRequesterIds();
                requesters.clear();
                adapter.notifyDataSetChanged();
                for(String requesterId: requesterIds){

                    databaseReference.child("users").child(requesterId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            requesters.add(dataSnapshot.getValue(User.class));
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                }
                databaseReference.child("books").addChildEventListener(updateListener);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
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

    @Override
    public void onPause() {
        databaseReference.removeEventListener(updateListener);
        isResumed = false;
        super.onPause();

    }

    @Override
    public void onResume() {
        isResumed = true;
        super.onResume();

    }

    public void refresh(){
        BookDetailsFragment fragment = (BookDetailsFragment) BookDetailsFragment.newInstance(mBook);
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.book_details_fragment_container,fragment).commit();
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
        public void onListFragmentInteraction(User item);

        public void onBookUpdate(Book book);
    }
}
