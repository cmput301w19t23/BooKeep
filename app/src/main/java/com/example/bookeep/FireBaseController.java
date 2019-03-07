package com.example.bookeep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.bookeep.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class FireBaseController {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private Context context;

    public FireBaseController(Context context) {
        //this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        firebaseUser = firebaseAuth.getCurrentUser();
        this.context = context;
    }

    public void createNewUser(final String email, String password, final String firstName, final String lastName, final PhoneNumber phoneNumber){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Toast.makeText(context, "New user added to FireBase", Toast.LENGTH_SHORT);
                    firebaseUser = firebaseAuth.getCurrentUser();

                    User user = new User(email, firstName,lastName, firebaseUser.getUid());
                    user.setPhoneNumber(phoneNumber);
                    user.setUserName("nafee");
                    databaseReference.child("users").child(user.getUserId()).setValue(user);
                    //Toast.makeText(context, "Fail", Toast.LENGTH_SHORT);
/*
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);*/
                    //

                    Intent intent = new Intent(context, BookDetailsActivity.class);
                    intent.putExtra("User", user);

                    Book book = new Book();
                    book.setTitle("88Harry Potter and the Deathly Hallows");
                    //ArrayList<String> authors = new ArrayList<>();
                    //authors.add("J. K. Rowling");

                    book.setAuthor("J.K.Rowling");
                    book.setStatus(BookStatus.AVAILABLE);
                    book.setOwner(firebaseUser.getUid());
                    //book.setBookId("book1");
                    book.setDescription("The magnificent final book in J. K. Rowling's seven-part saga comes to readers July 21, 2007. You'll find out July 21!");
                    //ok.setISBN(9780545010221);
                    book.addRequest("1iECbuI1luQliWJypEPhKBERSH42");
                    book.addRequest("1j7ynA45YuhOtEFiaFENvG1HOSF2");
                    book.addRequest("6AiwcVImzdfqpC8xDh1C48ialNG2");
                    databaseReference.child("books").child(book.getBookId()).setValue(book);

                    User newReq = new User("nak123", "nak@gmail.com", "N", "K", "nak123");
                    databaseReference.child("users").child("nak123").setValue(newReq);


                    intent.putExtra("Book ID", book.getBookId());
                    context.startActivity(intent);

                } else {
                    Toast.makeText(context, "Failed to add new user", Toast.LENGTH_SHORT);
                }

            }

        });

    }

    public void signIn(String email, String password){

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    firebaseUser = firebaseAuth.getCurrentUser();
                    Toast.makeText(context, "Signed in successfully", Toast.LENGTH_SHORT);

                    databaseReference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            User user = dataSnapshot.getValue(User.class);
                            Gson gson = new Gson();
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("CurrentUser", gson.toJson(user));
                            context.startActivity(intent);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

                } else {

                    Toast.makeText(context, "Failed to sign in", Toast.LENGTH_SHORT);

                }

            }

        });

    }

    public Boolean isUserLoggedIn(){

        return firebaseUser != null;

    }

    public void signOut(){

        firebaseAuth.signOut();

    }

    public User getCurrentUser(){

        //final User[] user = new User[1];
        final ArrayList<User> users = new ArrayList<User>();
        //final User user;
        if(isUserLoggedIn()){

            databaseReference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users.add(dataSnapshot.getValue(User.class));
                    //user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        return users.get(0);

    }

    public String getCurrentUserId(){
        return firebaseUser.getUid();
    }

    public void launchMainActivity(){

        if(isUserLoggedIn()) {

            databaseReference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("CurrentUser", gson.toJson(user));
                    context.startActivity(intent);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        } else {
            Toast.makeText(context, "sign in first", Toast.LENGTH_SHORT);
        }

    }

    public ArrayList<Book> getBooksOwnedByUser(){

        //final ArrayList<String>[] bookIds = new ArrayList<String>[1];
        final ArrayList<Book> owned = new ArrayList<Book>();
        databaseReference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                ArrayList<String> bookIds = user.getBorrowedIds();

                for (int i = 0; i < bookIds.size(); i++){
                    databaseReference.child("books").child(bookIds.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            owned.add(dataSnapshot.getValue(Book.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return owned;

            //ArrayList<Book> owned = new ArrayList<Book>();
            //for(String bookId: bookIds)

    }
/*
    public Book getBookByBookId(String bookId){

        //final Book[] book = new Book[1];
        //final ArrayList<Book> books = new ArrayList<Book>();
        Book book;
        databaseReference.child("books").child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //book[0] = dataSnapshot.getValue(Book.class);
                //books.add(dataSnapshot.getValue(Book.class));
                book = dataSnapshot.getValue(Book.class);
                return book;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //return book[0];
        return books.get(0);

    }

    public void addRequestToBookByBookId(String bookId){

        if(isUserLoggedIn()) {

            Book book = getBookByBookId(bookId);
            book.addRequest(firebaseUser.getUid());
            if(book.getStatus().equals(BookStatus.AVAILABLE)) {
                book.setStatus(BookStatus.REQUESTED);
                databaseReference.child("books").child(bookId).setValue(book);
            } else if (book.getStatus().equals(BookStatus.REQUESTED)){

                databaseReference.child("books").child(bookId).setValue(book);

            } else {
                Toast.makeText(context, "Sorry book is not available for borrowing", Toast.LENGTH_SHORT);
            }

        }

    }

    public DatabaseReference getDatabaseReference(){
        return this.databaseReference;
    }
/*
    public Book onBookChanged(String bookId){

        final Book[] book = new Book[1];
        databaseReference.child("books").child(bookId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                book[0] = dataSnapshot.getValue(Book.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return book[0];

    }
*/





}
