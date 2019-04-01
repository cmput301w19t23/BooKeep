package com.example.bookeep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;

import static com.example.bookeep.AddEditBookActivity.MY_PERMISSIONS_REQUEST_CAMERA;


public abstract class RawDump {
/*
        if (currentUserId.equals(mBook.getOwner()) && !mBook.getStatus().equals(BookStatus.BORROWED) && !mBook.getStatus().equals(BookStatus.ACCEPTED)) {
        //FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        //edit book button for owner
        Button button = (Button) view.findViewById(R.id.user_specific_button);
        //fab.setImageResource(R.drawable.pencil);
        button.setText("EDIT BOOK");
        button.setTextColor(Color.parseColor("#344955"));//primary color
        button.setOnClickListener(new View.OnClickListener() {

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
        //final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        //fab.setImageResource(R.drawable.round_done_black_18dp);
        final Button button = (Button) view.findViewById(R.id.user_specific_button);
        //fab.setImageResource(R.drawable.pencil);
        button.setText("COMPLETE TRANSACTION");
        button.setTextColor(Color.parseColor("#ff0000"));//red
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission has already been granted
                    new IntentIntegrator(getActivity()).initiateScan();
                    //fab.setEnabled(false);
                    //fab.setVisibility(View.GONE);
                    button.setEnabled(false);
                    button.setTextColor(Color.parseColor("#11000000"));

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

    } else if(isBorrowed && mBook.getStatus().equals(BookStatus.ACCEPTED) && !mBook.isInTransaction()){

        //get location
        if (mBook.getBorrowLocation() != null){

            //fab for get location.
            final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setImageResource(R.drawable.baseline_place_black_36dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), GetLocationActivity.class);
                    intent.putExtra("Book", mBook);
                    startActivity(intent);
                }
            });

        }


    } else if(!mBook.getStatus().equals(BookStatus.ACCEPTED) && !mBook.getStatus().equals(BookStatus.BORROWED)){//!book.getStatus().equals(BookStatus.BORROWED)){

        //final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        //fab.setImageResource(R.drawable.ic_add);
        final Button button = (Button) view.findViewById(R.id.user_specific_button);
        //fab.setImageResource(R.drawable.pencil);
        button.setText("REQUEST");
        button.setTextColor(Color.parseColor("#F9AA33"));
        //gray equals #11000000

        if (!isRequested) {
            button.setOnClickListener(new View.OnClickListener() {
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
                                databaseReference.child("user-requested").child(currentUserId).child(mBook.getBookId()).setValue(mBook);
                                button.setEnabled(false);
                                button.setTextColor(Color.parseColor("#11000000"));
                                //fab.setVisibility(View.GONE);

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });

                    } else {
                        button.setEnabled(false);
                        button.setTextColor(Color.parseColor("#11000000"));
                        //fab.setVisibility(View.GONE);
                    }
                }

            });

        } else {
            button.setEnabled(false);
            button.setTextColor(Color.parseColor("#11000000"));
            //fab.setVisibility(View.GONE);
        }

    } else if(currentUserId.equals(mBook.getOwner()) && mBook.getStatus().equals(BookStatus.ACCEPTED) && !mBook.isInTransaction()){
        // owner starts transaction
        final  FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.baseline_add_location_black_36dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    /*
                    databaseReference.child("users").child(mBook.getOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final User user = dataSnapshot.getValue(User.class);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                Intent intent = new Intent(getContext(), SetLocationActivity.class);
                intent.putExtra("User", mUser);
                intent.putExtra("Book", mBook);
                startActivity(intent);

            }
        });


        final Button button = (Button) view.findViewById(R.id.user_specific_button);

        button.setText("START TRANSACTION");
        button.setTextColor(Color.parseColor("#008000"));//green

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission has already been granted
                    new IntentIntegrator(getActivity()).initiateScan();
                    button.setEnabled(false);
                    button.setTextColor(Color.parseColor("#11000000"));
                    //fab.setVisibility(View.GONE);

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

        //final  FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        //fab.setImageResource(R.drawable.round_done_black_18dp);
        final Button button = (Button) view.findViewById(R.id.user_specific_button);
        //fab.setImageResource(R.drawable.pencil);
        button.setText("START RETURN");
        button.setTextColor(Color.parseColor("#008000"));//green
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission has already been granted
                    new IntentIntegrator(getActivity()).initiateScan();
                    button.setEnabled(false);
                    button.setTextColor(Color.parseColor("#11000000"));
                    //fab.setVisibility(View.GONE);

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

        //final  FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        //fab.setImageResource(R.drawable.round_done_black_18dp);
        final Button button = (Button) view.findViewById(R.id.user_specific_button);
        //fab.setImageResource(R.drawable.pencil);
        button.setText("COMPLETE RETURN");
        //button.setText(getResources().getString(R.string.abort_btn_txt))
        button.setTextColor(Color.parseColor("#ff0000"));//red
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission has already been granted
                    new IntentIntegrator(getActivity()).initiateScan();
                    button.setEnabled(false);
                    button.setTextColor(Color.parseColor("#11000000"));
                    //fab.setVisibility(View.GONE);

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
    */


/* udateListener for book details fragment//////////////////

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

                        }
 */



}
