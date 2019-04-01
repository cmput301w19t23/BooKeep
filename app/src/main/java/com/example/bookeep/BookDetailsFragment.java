package com.example.bookeep;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "book";
    private static final String ARG_PARAM2 = "current user";

    private Book mBook;
    private User mUser;
    private String currentUserId;

    private boolean isRequested;
    private boolean isBorrowed;
    private boolean isAccepted;
    private boolean isOwned;

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

    Button editButton;
    Button requestButton;
    Button cancelRequestButton;
    Button abortButton;
    Button handOverButton;
    Button recieveButton;
    //FloatingActionButton geoFAB;

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


                        if(mBook != null) {
                            refresh(mBook);
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
    };

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param book Parameter 1.
     * @return A new instance of fragment BookDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mBook = (Book) getArguments().getSerializable(ARG_PARAM1);
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
        currentUserId = firebaseUser.getUid();
        databaseReference.child("books").child(mBook.getBookId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBook = dataSnapshot.getValue(Book.class);
                if(mBook != null) {
                    mListener.onBookUpdate(mBook);
                }
                bookAuthors.setText(mBook.getAuthorsString());
                bookISBN.setText(mBook.getISBN());
                bookStatus.setText(mBook.getStatus().toString());
                bookDescription.setText(mBook.getDescription());
                databaseReference.child("users").child(mBook.getOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //final User user = dataSnapshot.getValue(User.class);
                        mUser = dataSnapshot.getValue(User.class);
                        bookOwner.setText(mUser.getEmail());
                        bookOwner.setTextColor(Color.BLUE);
                        bookOwner.setClickable(true);
                        bookOwner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
//<<<<<<< HEAD
                                intent.putExtra("Current User", mUser);

                                intent.putExtra("uuid", mUser.getUserId());
//>>>>>>> 31c4089b98632e213f9dc473010bd1208985d425
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

                if(mBook.getStatus().equals(BookStatus.REQUESTED)) {

                    for (String requesterId : mBook.getRequesterIds()) {

                        if (requesterId.equals(currentUserId)) {

                            isRequested = true;
                            break;

                        }

                    }

                }

                if(mBook.getStatus().equals(BookStatus.ACCEPTED)) {

                    if (mBook.getCurrentBorrowerId().equals(currentUserId)) {

                        isAccepted = true;

                    }

                }


                if(mBook.getStatus().equals(BookStatus.BORROWED)) {

                    if (mBook.getCurrentBorrowerId().equals(currentUserId)) {

                        isBorrowed = true;

                    }

                }

                if(mBook.getOwner().equals(currentUserId)) {
                    isOwned = true;
                }
//"#344955" primary
                //"#ff0000" red
                // "#F9AA33" yellow
                // //gray equals #11000000
                //"#008000" green
                if (isOwned) {
                    //you are the owner
                    instantiateOwnerButtonBar(view);
                    final FloatingActionButton geoFAB = (FloatingActionButton) view.findViewById(R.id.fab);

                    if(mBook.getStatus().equals(BookStatus.AVAILABLE) || mBook.getStatus().equals(BookStatus.REQUESTED)){
                        //Button button = (Button) view.findViewById(R.id.user_specific_button);
                        //fab.setImageResource(R.drawable.pencil);
                        //button.setText("EDIT BOOK");
                        //owner + accepted or requested
                        editButton.setTextColor(Color.parseColor("#344955"));//primary color
                        editButton.setEnabled(true);
                        editButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                                Intent intent = new Intent(getContext(), AddEditBookActivity.class);
                                intent.putExtra("Book to edit", mBook);
                                startActivity(intent);
                            }
                        });

                    } else if (mBook.getStatus().equals(BookStatus.ACCEPTED) && !mBook.isInTransaction()){
                        //owner + accepted + not in transaction means handover, abort and fab is in play
                        handOverButton.setTextColor(Color.parseColor("#008000"));
                        abortButton.setTextColor(Color.parseColor("#000000"));//black
                        geoFAB.setImageResource(R.drawable.baseline_add_location_black_36dp);

                        handOverButton.setEnabled(true);
                        abortButton.setEnabled(true);
                        geoFAB.setEnabled(true);

                        handOverButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    // Permission has already been granted
                                    new IntentIntegrator(getActivity()).initiateScan();
                                    handOverButton.setEnabled(false);
                                    handOverButton.setTextColor(Color.parseColor("#11000000"));
                                    //fab.setVisibility(View.GONE);

                                } else {

                                    // Permission is NOT granted
                                    // Prompt the user for permission
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.CAMERA},
                                            MY_PERMISSIONS_REQUEST_CAMERA);
                                }

                            }
                        });
                        geoFAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getContext(), SetLocationActivity.class);
                                intent.putExtra("User", mUser);
                                intent.putExtra("Book", mBook);
                                startActivity(intent);

                            }
                        });
                        abortButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                abortButton.setEnabled(false);
                                abortButton.setTextColor(Color.parseColor("#11000000"));
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

                                databaseReference.child("user-books").child(mBook.getOwner()).child(mBook.getBookId()).setValue(mBook);
                                databaseReference.child("books").child(mBook.getBookId()).setValue(mBook);
                            }
                        });


                    } else if (mBook.getStatus().equals(BookStatus.ACCEPTED) && mBook.isInTransaction()){

                        geoFAB.setImageResource(R.drawable.baseline_add_location_black_36dp);
                        geoFAB.setEnabled(true);
                        geoFAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getContext(), SetLocationActivity.class);
                                intent.putExtra("User", mUser);
                                intent.putExtra("Book", mBook);
                                startActivity(intent);

                            }
                        });
                        abortButton.setTextColor(Color.parseColor("#000000"));//black
                        abortButton.setEnabled(true);
                        abortButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                abortButton.setEnabled(false);
                                abortButton.setTextColor(Color.parseColor("#11000000"));
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
                            }
                        });

                    } else if (mBook.getStatus().equals(BookStatus.BORROWED) && mBook.isInTransaction()){

                        //final FloatingActionButton geoFAB = (FloatingActionButton) view.findViewById(R.id.fab);


                        geoFAB.setEnabled(true);

                        geoFAB.setImageResource(R.drawable.baseline_place_black_36dp);
                        geoFAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), GetLocationActivity.class);
                                intent.putExtra("Book", mBook);
                                startActivity(intent);
                            }
                        });
                        recieveButton.setEnabled(true);
                        recieveButton.setTextColor(Color.parseColor("#ff0000"));
                        recieveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(abortButton != null){
                                    abortButton.setEnabled(false);
                                    abortButton.setTextColor(Color.parseColor("#11000000"));
                                }
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    // Permission has already been granted
                                    new IntentIntegrator(getActivity()).initiateScan();
                                    recieveButton.setEnabled(false);
                                    recieveButton.setTextColor(Color.parseColor("#11000000"));
                                    //refresh();//**

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

                    } else if (mBook.getStatus().equals(BookStatus.BORROWED) && !mBook.isInTransaction()) {
                        //final FloatingActionButton geoFAB = (FloatingActionButton) view.findViewById(R.id.fab);


                        geoFAB.setEnabled(true);

                        geoFAB.setImageResource(R.drawable.baseline_place_black_36dp);
                        geoFAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), GetLocationActivity.class);
                                intent.putExtra("Book", mBook);
                                startActivity(intent);
                            }
                        });
                    }
                } else if (isRequested) {
                    //you are the requester
                    instantiateRequesterButtonBar(view);
                    //cancelRequestButton = (Button) view.findViewById(R.id.edit_req_btn);
                    //cancelRequestButton.setEnabled(true);
                    //cancelRequestButton.setTextColor(Color.parseColor("11000000"))
                    final FloatingActionButton geoFAB = (FloatingActionButton) view.findViewById(R.id.fab);
                    cancelRequestButton.setEnabled(true);
                    geoFAB.setEnabled(false);
                    cancelRequestButton.setTextColor(Color.parseColor("#344955"));//primary color


                    cancelRequestButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelRequestButton.setEnabled(false);
                            cancelRequestButton.setTextColor(Color.parseColor("#11000000"));
                            mBook.removeRequester(currentUserId);
                            if(mBook.getRequesterIds().size() == 0){
                                mBook.setStatus(BookStatus.AVAILABLE);
                            }
                            databaseReference.child("user-requested")
                                    .child(currentUserId)
                                    .child(mBook.getBookId())
                                    .removeValue();
                            for(String requester: mBook.getRequesterIds()){

                                databaseReference.child("user-requested")
                                        .child(requester)
                                        .child(mBook.getBookId())
                                        .setValue(mBook);

                            }

                            //databaseReference.child("books").child(mBook.getBookId()).setValue(mBook);
                            databaseReference.child("user-books").child(mBook.getOwner()).child(mBook.getBookId()).setValue(mBook);
                            //removeRequester(position);
                            databaseReference.child("books").child(mBook.getBookId()).setValue(mBook);

                            //cancelRequestButton.setEnabled(false);
                            //cancelRequestButton.setTextColor(Color.parseColor("#11000000"));
                            //cancelRequestButton.setTextColor(Color.parseColor("#11000000"));
                            //databaseReference.child("books").child(mBook.getBookId()).setValue(mBook);
                            //refresh();
                        }

                    });

                } else if (isAccepted) {

                    if(!mBook.getStatus().equals(BookStatus.BORROWED)) {
                        instantiateRequesterButtonBar(view);
                        final FloatingActionButton geoFAB = (FloatingActionButton) view.findViewById(R.id.fab);


                        geoFAB.setEnabled(true);

                        geoFAB.setImageResource(R.drawable.baseline_place_black_36dp);
                        abortButton.setTextColor(Color.parseColor("#000000"));//black
                        abortButton.setEnabled(true);
                        abortButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                abortButton.setEnabled(false);
                                abortButton.setTextColor(Color.parseColor("#11000000"));
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
                            }
                        });
                        geoFAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), GetLocationActivity.class);
                                intent.putExtra("Book", mBook);
                                startActivity(intent);
                            }
                        });
                    }
                    if(mBook.isInTransaction()){

                        final FloatingActionButton geoFAB = (FloatingActionButton) view.findViewById(R.id.fab);


                        geoFAB.setEnabled(true);

                        geoFAB.setImageResource(R.drawable.baseline_place_black_36dp);
                        geoFAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), GetLocationActivity.class);
                                intent.putExtra("Book", mBook);
                                startActivity(intent);
                            }
                        });

                        recieveButton.setEnabled(true);
                        recieveButton.setTextColor(Color.parseColor("#ff0000"));
                        recieveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(abortButton != null){
                                    abortButton.setEnabled(false);
                                    abortButton.setTextColor(Color.parseColor("#11000000"));
                                }
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    // Permission has already been granted
                                    new IntentIntegrator(getActivity()).initiateScan();
                                    //fab.setEnabled(false);
                                    //fab.setVisibility(View.GONE);
                                    recieveButton.setEnabled(false);
                                    //abortButton.setEnabled(false);
                                    recieveButton.setTextColor(Color.parseColor("#11000000"));
                                    //refresh();

                                } else {

                                    // Permission is NOT granted
                                    // Prompt the user for permission
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.CAMERA},
                                            MY_PERMISSIONS_REQUEST_CAMERA);
                                }
                            }
                        });
                    }

                } else if (isBorrowed) {
                    //you are the borrower
                    instantiateRequesterButtonBar(view);
                    final FloatingActionButton geoFAB = (FloatingActionButton) view.findViewById(R.id.fab);

                    //geoFAB.setEnabled(false);
                    //geoFAB.setImageResource(R.drawable.baseline_place_black_36dp);

                    geoFAB.setImageResource(R.drawable.baseline_add_location_black_36dp);
                    geoFAB.setEnabled(true);
                    geoFAB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getContext(), SetLocationActivity.class);
                            intent.putExtra("User", mUser);
                            intent.putExtra("Book", mBook);
                            startActivity(intent);

                        }
                    });
                    if(!mBook.isInTransaction()){

                        handOverButton.setEnabled(true);
                        handOverButton.setTextColor(Color.parseColor("#008000"));
                        handOverButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    // Permission has already been granted
                                    new IntentIntegrator(getActivity()).initiateScan();
                                    handOverButton.setEnabled(false);
                                    handOverButton.setTextColor(Color.parseColor("#11000000"));

                                    //fab.setVisibility(View.GONE);
                                    //refresh();

                                } else {

                                    // Permission is NOT granted
                                    // Prompt the user for permission
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.CAMERA},
                                            MY_PERMISSIONS_REQUEST_CAMERA);
                                }

                            }
                        });

                    }

                } else {
                    //you are neither

                    instantiateNonRequesterButtonBar(view);
                    final FloatingActionButton geoFAB = (FloatingActionButton) view.findViewById(R.id.fab);

                    geoFAB.setEnabled(false);

                    if (mBook.getStatus().equals(BookStatus.AVAILABLE) || mBook.getStatus().equals(BookStatus.REQUESTED)) {
                        requestButton.setEnabled(true);
                        requestButton.setTextColor(Color.parseColor("#F9AA33"));

                        requestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestButton.setEnabled(false);
                                requestButton.setTextColor(Color.parseColor("#11000000"));
                                mBook.addRequest(currentUserId);
                                mBook.setStatus(BookStatus.REQUESTED);
                                mBook.setNewRequest();
                                databaseReference.child("books").child(mBook.getBookId()).setValue(mBook);
                                databaseReference.child("user-books").child(mBook.getOwner()).child(mBook.getBookId()).setValue(mBook);
                                databaseReference.child("user-requested").child(currentUserId).child(mBook.getBookId()).setValue(mBook);
                                //requestButton.setEnabled(false);
                                //requestButton.setTextColor(Color.parseColor("#11000000"));
                                //fab.setVisibility(View.GONE);


                            }
                        });


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("books").addChildEventListener(updateListener);
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

                    if(mBook.getCurrentBorrowerId().equals(currentUserId) && mBook.getStatus().equals(BookStatus.ACCEPTED)){
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

    public void refresh( Book book){
        //BookDetailsFragment fragment = null;
        //fragment = (BookDetailsFragment) BookDetailsFragment.newInstance(mBook);

        if (mListener != null) {
            mListener.onBookUpdate(book);
        }
        if (abortButton != null) {
            abortButton.setEnabled(false);
            abortButton.setTextColor(Color.parseColor("#11000000"));
        }
        if(cancelRequestButton != null) {
            cancelRequestButton.setEnabled(false);
            cancelRequestButton.setTextColor(Color.parseColor("#11000000"));
        }
        //abortButton.setTextColor(Color.parseColor("#11000000"));
        //cancelRequestButton.setTextColor(Color.parseColor("#11000000"));
        if(requestButton != null){
            requestButton.setEnabled(false);
            requestButton.setTextColor(Color.parseColor("#11000000"));
        }
        if(handOverButton != null){
            handOverButton.setEnabled(false);
            handOverButton.setTextColor(Color.parseColor("#11000000"));
        }
        if(recieveButton != null){
            recieveButton.setEnabled(false);
            recieveButton.setTextColor(Color.parseColor("#11000000"));
        }
        //if(mBook != null) {
        BookDetailsFragment fragment = (BookDetailsFragment) BookDetailsFragment.newInstance(book);
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.book_details_fragment_container, fragment).commit();
        //}
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.setReorderingAllowed(false);
        //ft.setAllowOptimization(false);
        //ft.detach(BookDetailsFragment.this).attach(fragment).commit();
        //ft.detach(fragment).attach(fragment).commitAllowingStateLoss();
    }

    public void instantiateOwnerButtonBar(View view){
        editButton = (Button) view.findViewById(R.id.edit_req_btn);
        handOverButton = (Button) view.findViewById(R.id.hand_over_btn);
        recieveButton = (Button) view.findViewById(R.id.recieve_btn);
        abortButton = (Button) view.findViewById(R.id.abort_btn);
        //geoFAB = (FloatingActionButton) view.findViewById(R.id.fab);

        editButton.setEnabled(false);
        handOverButton.setEnabled(false);
        recieveButton.setEnabled(false);
        abortButton.setEnabled(false);
        //geoFAB.setEnabled(false);

        editButton.setText(getResources().getString(R.string.edit_btn_txt));
        handOverButton.setText(getResources().getString(R.string.hand_over_btn_txt));
        recieveButton.setText(getResources().getString(R.string.receive_btn_txt));
        abortButton.setText(getResources().getString(R.string.abort_btn_txt));

        editButton.setTextColor(Color.parseColor("#11000000"));
        handOverButton.setTextColor(Color.parseColor("#11000000"));
        recieveButton.setTextColor(Color.parseColor("#11000000"));
        abortButton.setTextColor(Color.parseColor("#11000000"));
    }

    public void instantiateNonRequesterButtonBar(View view){
        requestButton = (Button) view.findViewById(R.id.edit_req_btn);
        handOverButton = (Button) view.findViewById(R.id.hand_over_btn);
        recieveButton = (Button) view.findViewById(R.id.recieve_btn);
        abortButton = (Button) view.findViewById(R.id.abort_btn);

        requestButton.setEnabled(false);
        handOverButton.setEnabled(false);
        recieveButton.setEnabled(false);
        abortButton.setEnabled(false);

        requestButton.setText(getResources().getString(R.string.request_btn_txt));
        handOverButton.setText(getResources().getString(R.string.hand_over_btn_txt));
        recieveButton.setText(getResources().getString(R.string.receive_btn_txt));
        abortButton.setText(getResources().getString(R.string.abort_btn_txt));

        requestButton.setTextColor(Color.parseColor("#11000000"));
        handOverButton.setTextColor(Color.parseColor("#11000000"));
        recieveButton.setTextColor(Color.parseColor("#11000000"));
        abortButton.setTextColor(Color.parseColor("#11000000"));
    }

    public void instantiateRequesterButtonBar(View view) {

        cancelRequestButton = (Button) view.findViewById(R.id.edit_req_btn);
        handOverButton = (Button) view.findViewById(R.id.hand_over_btn);
        recieveButton = (Button) view.findViewById(R.id.recieve_btn);
        abortButton = (Button) view.findViewById(R.id.abort_btn);
        //final FloatingActionButton geoFAB = (FloatingActionButton) view.findViewById(R.id.fab);

        cancelRequestButton.setEnabled(false);
        handOverButton.setEnabled(false);
        recieveButton.setEnabled(false);
        abortButton.setEnabled(false);
        //geoFAB.setEnabled(false);

        cancelRequestButton.setText(getResources().getString(R.string.cancel_request_btn_txt));
        handOverButton.setText(getResources().getString(R.string.hand_over_btn_txt));
        recieveButton.setText(getResources().getString(R.string.receive_btn_txt));
        abortButton.setText(getResources().getString(R.string.abort_btn_txt));

        cancelRequestButton.setTextColor(Color.parseColor("#11000000"));
        handOverButton.setTextColor(Color.parseColor("#11000000"));
        recieveButton.setTextColor(Color.parseColor("#11000000"));
        abortButton.setTextColor(Color.parseColor("#11000000"));

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
