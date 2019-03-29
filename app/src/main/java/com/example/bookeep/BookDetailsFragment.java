package com.example.bookeep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import static com.example.bookeep.AddEditBookActivity.MY_PERMISSIONS_REQUEST_CAMERA;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @author Nafee Khan, Jeff Kirker
 * @verion 1.0.1
 */
public class BookDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "book";
    private static final String ARG_PARAM2 = "current user";






    // TODO: Rename and change types of parameters
    private Book mBook;
    private User mUser;
    private String currentUserId;

    private boolean isRequested;
    private boolean isBorrowed;

    private OnFragmentInteractionListener mListener;

    private TextView bookTitle;
    private TextView bookAuthors;
    private TextView bookISBN;
    private TextView bookStatus;
    private TextView bookDescription;
    private TextView bookOwner;
    private ImageView bookCover;
    private FireBaseController fireBaseController = new FireBaseController(getActivity());
    private boolean isResumed;
    private boolean testBool1;
    private boolean testBool2;
    private BookStatus testStatus;
    private String testOwner;
    private String testBorrower;


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
                    BookStatus prevStatus = changedBook.getStatus();
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
                        bookCover = getView().findViewById(R.id.book_cover);

                        bookTitle.setText(mBook.getTitle());
                        DownloadImageTask downloadImageTask = new DownloadImageTask();
                        try {
                            Bitmap bitmap = downloadImageTask.execute(mBook.getBookImageURL()).get();//"http://books.google.com/books/content?id=H8sdBgAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api").get();
                            bookCover.setImageBitmap(bitmap);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bookAuthors.setText(mBook.getAuthorsString());
                        //bookISBN.setText(mBook.getISBN());
                        bookStatus.setText(mBook.getStatus().toString());
                        bookDescription.setText(mBook.getDescription());
                        /*
                        if (currentUserId.equals(mBook.getCurrentBorrowerId())
                                && mBook.getStatus().equals(BookStatus.ACCEPTED)
                                && !prevStatus.equals(BookStatus.ACCEPTED)){

                        }*/

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

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_book_details, container, false);
        bookTitle = (TextView) view.findViewById(R.id.book_title);
        bookAuthors = (TextView) view.findViewById(R.id.book_authors);
        bookISBN = (TextView) view.findViewById(R.id.book_isbn);
        bookStatus = view.findViewById(R.id.book_status);
        bookDescription = view.findViewById(R.id.book_description);
        bookOwner = view.findViewById(R.id.book_owner);
        bookCover = view.findViewById(R.id.book_cover);
        //isResumed = true;
        databaseReference.child("books").child(mBook.getBookId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBook = dataSnapshot.getValue(Book.class);
                mListener.onBookUpdate(mBook);

                bookAuthors.setText(mBook.getAuthorsString());
                bookISBN.setText(mBook.getISBN());
                bookStatus.setText(mBook.getStatus().toString());
                bookDescription.setText(mBook.getDescription());
                databaseReference.child("users").child(mBook.getOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        bookOwner.setText(user.getEmail());
                        bookOwner.setTextColor(Color.BLUE);
                        bookOwner.setClickable(true);
                        bookOwner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                                intent.putExtra("Current User", user);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                DownloadImageTask downloadImageTask = new DownloadImageTask();
                try {
                    Bitmap bitmap = downloadImageTask.execute(mBook.getBookImageURL()).get();//"http://books.google.com/books/content?id=H8sdBgAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api").get();
                    bookCover.setImageBitmap(bitmap);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bookTitle.setText(mBook.getTitle());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("books").addChildEventListener(updateListener);
        currentUserId = firebaseUser.getUid();

        if(mBook.getStatus().equals(BookStatus.REQUESTED)) {

            for (String requesterId : mBook.getRequesterIds()) {

                if (requesterId.equals(currentUserId)) {

                    isRequested = true;
                    break;

                }

            }

        }

        if(mBook.getStatus().equals(BookStatus.ACCEPTED) || mBook.getStatus().equals(BookStatus.BORROWED)) {

            if (mBook.getCurrentBorrowerId().equals(currentUserId)) {

                isBorrowed = true;

            }

        }
 // = currentUserId.equals(mBook.getCurrentBorrowerId());
        testBool1 = mBook.isInTransaction();

        if (currentUserId.equals(mBook.getOwner()) && !mBook.getStatus().equals(BookStatus.BORROWED) && !mBook.getStatus().equals(BookStatus.ACCEPTED)) {
            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            //edit book button for owner
            fab.setImageResource(R.drawable.pencil);
            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    Intent intent = new Intent(getContext(), AddEditBookActivity.class);
                    intent.putExtra("Book to edit", mBook);
                    startActivity(intent);
                }
            });

        } else if(isBorrowed && mBook.getStatus().equals(BookStatus.ACCEPTED) && mBook.isInTransaction()){
            //borrwer ends transaction by receiving and scanning
            //Scan book to set borrowed
            final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setImageResource(R.drawable.round_done_black_18dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        // Permission has already been granted
                        new IntentIntegrator(getActivity()).initiateScan();
                        fab.setEnabled(false);
                        fab.setVisibility(View.GONE);

                    } else {

                        // Permission is NOT granted
                        // Prompt the user for permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    //fab.setEnabled(false);
                    //fab.setVisibility(View.GONE);
                }
            });

        } else if(!mBook.getStatus().equals(BookStatus.ACCEPTED) && !mBook.getStatus().equals(BookStatus.BORROWED)){//!book.getStatus().equals(BookStatus.BORROWED)){

            final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

            fab.setImageResource(R.drawable.ic_add);

            if (!isRequested) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //fireBaseController.addRequestToBookByBookId(book.getBookId());
                        if (mBook.getStatus().equals(BookStatus.AVAILABLE) || mBook.getStatus().equals(BookStatus.REQUESTED)) {

                            databaseReference.child("books").child(mBook.getBookId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    mBook = dataSnapshot.getValue(Book.class);
                                    mBook.addRequest(currentUserId);
                                    mBook.setStatus(BookStatus.REQUESTED);
                                    mBook.setNewRequest();
                                    databaseReference.child("books").child(mBook.getBookId()).setValue(mBook);
                                    databaseReference.child("user-books").child(mBook.getOwner()).child(mBook.getBookId()).setValue(mBook);
                                    fab.setEnabled(false);
                                    fab.setVisibility(View.GONE);

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });

                        } else {
                            //Toast.makeText(this, "Book not Available for borrowing", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(BookDetailsActivity.this, "Book not Available", Toast.LENGTH_SHORT);
                            fab.setEnabled(false);
                            fab.setVisibility(View.GONE);
                        }
                    }

                });

            } else {
                fab.setEnabled(false);
                fab.setVisibility(View.GONE);
            }

        } else if(currentUserId.equals(mBook.getOwner()) && mBook.getStatus().equals(BookStatus.ACCEPTED) && !mBook.isInTransaction()){
            // owner starts transaction
            final  FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setImageResource(R.drawable.round_done_black_18dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        // Permission has already been granted
                        new IntentIntegrator(getActivity()).initiateScan();
                        fab.setEnabled(false);
                        fab.setVisibility(View.GONE);

                    } else {

                        // Permission is NOT granted
                        // Prompt the user for permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    //fab.setEnabled(false);
                    //fab.setVisibility(View.GONE);
                }
            });


        } else if (currentUserId.equals(mBook.getCurrentBorrowerId()) && mBook.getStatus().equals(BookStatus.BORROWED) && !mBook.isInTransaction()){

            final  FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setImageResource(R.drawable.round_done_black_18dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        // Permission has already been granted
                        new IntentIntegrator(getActivity()).initiateScan();
                        fab.setEnabled(false);
                        fab.setVisibility(View.GONE);

                    } else {

                        // Permission is NOT granted
                        // Prompt the user for permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    //fab.setEnabled(false);
                    //fab.setVisibility(View.GONE);
                }
            });

        } else if (currentUserId.equals(mBook.getOwner()) && mBook.getStatus().equals(BookStatus.BORROWED) && mBook.isInTransaction()){

            final  FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setImageResource(R.drawable.round_done_black_18dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        // Permission has already been granted
                        new IntentIntegrator(getActivity()).initiateScan();
                        fab.setEnabled(false);
                        fab.setVisibility(View.GONE);

                    } else {

                        // Permission is NOT granted
                        // Prompt the user for permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    //fab.setEnabled(false);
                    //fab.setVisibility(View.GONE);
                }
            });

        }

        return view;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {

            if(result.getContents() == null) {

                //Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();

            } else {

                //Log.d("MainActivity", "Scanned");
                Toast.makeText(getContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                if(mBook.getISBN().equals(result.getContents())){
                    //testStatus = mBook.getStatus();
                    //testOwner = mBook.getOwner();
                    //testBorrower = mBook.getCurrentBorrowerId();
                    if(mBook.getCurrentBorrowerId().equals(currentUserId) && mBook.getStatus().equals(BookStatus.ACCEPTED)){
                        /*
                        databaseReference.child("books").child(book.getBookId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                book =
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/

                        mBook.setStatus(BookStatus.BORROWED);
                        mBook.endTransaction();
                        databaseReference.child("books").child(mBook.getBookId()).setValue(mBook);
                        databaseReference.child("user-books").child(mBook.getOwner()).child(mBook.getBookId()).setValue(mBook);
                        databaseReference.child("user-borrowed").child(currentUserId).child(mBook.getBookId()).setValue(mBook);

                    } else if (mBook.getOwner().equals(currentUserId) && mBook.getStatus().equals(BookStatus.ACCEPTED)){

                        mBook.startTransaction();
                        databaseReference.child("books").child(mBook.getBookId()).setValue(mBook);
                        databaseReference.child("user-books").child(mBook.getOwner()).child(mBook.getBookId()).setValue(mBook);
                        databaseReference.child("user-borrowed").child(mBook.getCurrentBorrowerId()).child(mBook.getBookId()).setValue(mBook);

                    } else if (mBook.getOwner().equals(currentUserId) && mBook.getStatus().equals(BookStatus.BORROWED)){

                        //end of transaction returning book
                        mBook.setStatus(BookStatus.AVAILABLE);
                        mBook.endTransaction();
                        databaseReference.child("users").child(mBook.getCurrentBorrowerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User formerBorrower = dataSnapshot.getValue(User.class);
                                formerBorrower.removeFromBorrowed(mBook.getBookId());
                                databaseReference.child("users").child(formerBorrower.getUserId()).setValue(formerBorrower);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        databaseReference.child("user-borrowed").child(mBook.getCurrentBorrowerId()).child(mBook.getBookId()).removeValue();
                        mBook.setCurrentBorrower(null);
                        databaseReference.child("books").child(mBook.getBookId()).setValue(mBook);
                        databaseReference.child("user-books").child(mBook.getOwner()).child(mBook.getBookId()).setValue(mBook);


                    } else if (mBook.getCurrentBorrowerId().equals(currentUserId) && mBook.getStatus().equals(BookStatus.BORROWED)){

                        mBook.startTransaction();
                        databaseReference.child("books").child(mBook.getBookId()).setValue(mBook);
                        databaseReference.child("user-books").child(mBook.getOwner()).child(mBook.getBookId()).setValue(mBook);
                        databaseReference.child("user-borrowed").child(mBook.getCurrentBorrowerId()).child(mBook.getBookId()).setValue(mBook);

                    }

                }

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
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
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;

        }

        protected void onPostExecute(Bitmap result) {

            //bmImage.setImageBitmap(result);

        }

    }




}
