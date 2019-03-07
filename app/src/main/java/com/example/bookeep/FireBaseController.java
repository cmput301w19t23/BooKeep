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
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        firebaseUser = firebaseAuth.getCurrentUser();
        //this.context = context;
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
                    databaseReference.child("users").child(user.getUserId()).setValue(user);

                    Intent intent = new Intent(context, LoginActivity.class);
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

//    public User getCurrentUser(){
//
//        final User[] user = new User[1];
//        if(isUserLoggedIn()){
//
//            databaseReference.child("user").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    user[0] = dataSnapshot.getValue(User.class);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//
//        }
//
//        return user[0];
//
//    }

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

//    public ArrayList<Book> getBooksOwnedByUser(){
//
//        //final ArrayList<String>[] bookIds = new ArrayList<String>[1];
//        final ArrayList<Book> owned = new ArrayList<Book>();
//        databaseReference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                User user = dataSnapshot.getValue(User.class);
//                ArrayList<String> bookIds = user.getBorrowedIds();
//
//                for (int i = 0; i < bookIds.size(); i++){
//                    databaseReference.child("books").child(bookIds.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            owned.add(dataSnapshot.getValue(Book.class));
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        return owned;
//
//            //ArrayList<Book> owned = new ArrayList<Book>();
//            //for(String bookId: bookIds)
//
//    }

//    public Book getBookByBookId(String bookId){
//
//        final Book[] book = new Book[1];
//        databaseReference.child("books").child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                book[0] = dataSnapshot.getValue(Book.class);
//                //return book;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//        return book[0];
//
//    }






}
