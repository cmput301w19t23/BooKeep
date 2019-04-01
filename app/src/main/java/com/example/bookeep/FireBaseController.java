package com.example.bookeep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

/**
 * Controller that can handle some firebase functionality
 * @author Nafee Khan
 * @see User
 * @see PhoneNumber
 * @version 1.0.1
 */
public class FireBaseController {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private Context context;

    public FireBaseController(Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        firebaseUser = firebaseAuth.getCurrentUser();
        this.context = context;
    }

    /**
     * creates a new user and adds them to firebase
     * @param userName String of username
     * @param email string of email
     * @param password string of password
     * @param firstName string of first name
     * @param lastName string of last name
     * @param phoneNumber string of phone numbner
     */
    public void createNewUser(final String userName,final String email,String password,final String firstName,final String lastName,final PhoneNumber phoneNumber,final Bitmap bitmap){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Toast.makeText(context, "New user added to FireBase", Toast.LENGTH_SHORT);
                    firebaseUser = firebaseAuth.getCurrentUser();

                    User user = new User(userName, email, firstName,lastName, firebaseUser.getUid());
                    user.setPhoneNumber(phoneNumber);

                    //user.setUserName("nafee");
                    databaseReference.child("users").child(user.getUserId()).setValue(user);
                    Toast.makeText(context, "Fail", Toast.LENGTH_SHORT);

                    uploadImageToFireBase(bitmap, user);

                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Failed to add new user", Toast.LENGTH_SHORT);
                }
            }

        });
    }

    public void uploadImageToFireBase (Bitmap bitmap,final User user) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Calendar now = Calendar.getInstance();
        String strDate = Integer.toString(now.get(Calendar.YEAR)) +
                Integer.toString(now.get(Calendar.MONTH)) +
                Integer.toString(now.get(Calendar.DAY_OF_MONTH)) +
                Integer.toString(now.get(Calendar.HOUR_OF_DAY)) +
                Integer.toString(now.get(Calendar.MINUTE)) +
                Integer.toString(now.get(Calendar.SECOND)) +
                Integer.toString(now.get(Calendar.MILLISECOND));

        StorageReference storageReference = storage.getReferenceFromUrl("gs://bookeep-684ab.appspot.com");
        final StorageReference imageRef = storageReference.child(strDate + ".jpg");

        Bitmap resize = Bitmap.createScaledBitmap(bitmap, 270,270, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resize.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    user.setImageURL(task.getResult().toString());
                    databaseReference.child("users").child(user.getUserId()).setValue(user);
                }
                else {
                    return;
                }
            }
        });
    }

    /**
     * signs a user in to the app through firebase
     * @param email users email string
     * @param password users password string
     */
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
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                } else {

                    Toast.makeText(context, "Failed to sign in", Toast.LENGTH_SHORT);

                }

            }

        });

    }

    /**
     * checks if user is signed in
     * @return user signed in boolean
     */
    public Boolean isUserLoggedIn(){

        return firebaseUser != null;

    }

    /**
     * signs the user out
     */
    public void signOut(){

        firebaseAuth.signOut();

    }


    /**
     * returns the current users id
     * @return string of user id
     */
    public String getCurrentUserId(){
        return firebaseUser.getUid();
    }

    /**
     * launches main activity when a user is logged in
     */
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
}
